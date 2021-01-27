package io.github.t45k.ninja.usecase.preprocess

import io.github.t45k.ninja.NinjaConfig
import io.github.t45k.ninja.entity.CodeBlock
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom.AST.JLS14
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor
import java.io.File

class JavaParser(private val tokenizer: (String) -> List<Int>, private val config: NinjaConfig) {

    fun extractBlocks(sourceFile: File): Flowable<CodeBlock> =
        Observable.create<CodeBlock> { emitter ->
            val compilationUnit: CompilationUnit = ASTParser.newParser(JLS14)
                .apply { setSource(sourceFile.readText().toCharArray()) }
                .apply {
                    setCompilerOptions(
                        JavaCore.getOptions().apply { put(JavaCore.COMPILER_SOURCE, "11") })
                }
                .let { it.createAST(NullProgressMonitor()) as CompilationUnit }

            val fileName = sourceFile.canonicalPath
            object : ASTVisitor() {
                override fun visit(node: MethodDeclaration): Boolean {
                    val startLine = if (node.javadoc == null) {
                        compilationUnit.getLineNumber(node.startPosition)
                    } else {
                        compilationUnit.getLineNumber(node.getNodeNextToJavaDoc().startPosition)
                    }
                    val endLine = compilationUnit.getLineNumber(node.startPosition + node.length)
                    node.javadoc = null
                    if (endLine - startLine + 1 < config.minLine) {
                        return false
                    }

                    val tokenSequence = tokenizer(node.toString())
                    if (tokenSequence.size < config.minToken) {
                        return false
                    }

                    val nGrams = (0..(tokenSequence.size - config.gramSize))
                        .map { tokenSequence.subList(it, it + config.gramSize).hashCode() }
                        .groupingBy { it }
                        .eachCount()
                        .toMap()
                    emitter.onNext(CodeBlock(fileName, startLine, endLine, nGrams))

                    return false
                }
            }.apply(compilationUnit::accept)
            emitter.onComplete()
        }.toFlowable(BackpressureStrategy.BUFFER)


    @Suppress("UNCHECKED_CAST")
    private fun MethodDeclaration.getNodeNextToJavaDoc(): ASTNode =
        (structuralPropertiesForType() as List<StructuralPropertyDescriptor>)
            .asSequence()
            .drop(1)
            .flatMap {
                when (it) {
                    is ChildListPropertyDescriptor -> (getStructuralProperty(it) as List<ASTNode>).asSequence()
                    is SimplePropertyDescriptor -> emptySequence()
                    else -> sequenceOf(getStructuralProperty(it) as ASTNode?)
                }
            }
            .filterNotNull()
            .first()
}
