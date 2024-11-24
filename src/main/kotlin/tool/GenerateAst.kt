package org.klox.tool

import java.io.PrintWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Usage: generate_ast <output directory>")

        exitProcess(64)
    }

    val outputDir = args[0]

    val expression = listOf(
        "Binary     - left: Expr, operator: Token, right: Expr",
        "Grouping   - expression: Expr",
        "Literal    - value: Any?",
        "Unary      - operator: Token, right: Expr",
    )

    defineAst(outputDir, "Expr", expression)
}

fun defineAst(
    outputDir: String,
    baseName: String,
    types: List<String>
) {
    val path = "$outputDir/$baseName.kt"
    val writer = PrintWriter(path, "UTF-8")

    writer.println("package org.klox")
    writer.println()
    writer.println("abstract class $baseName {")

    defineVisitor(writer, baseName, types)

    // AST classes
    for (type in types) {
        val splits = type.split("-")
        val className = splits[0].trim()
        val fields = splits[1].trim()

        defineType(writer, baseName, className, fields)
    }

    // Base accept method
    writer.println("\n    abstract fun <R> accept(visitor: Visitor<R>): R")

    writer.println("}")
    writer.close()
}

fun defineType(
    writer: PrintWriter,
    baseName: String,
    className: String,
    fieldsString: String
) {
    writer.println("    class $className (")

    val fields = fieldsString.split(",")
    fields.forEachIndexed { index, field ->
        val fieldTrimmed = field.trim()

        if (index == fields.size - 1) {
            writer.println("        val $fieldTrimmed")
        } else {
            writer.println("        val $fieldTrimmed,")
        }
    }

    writer.println("    ): $baseName() {")
    writer.println("        override fun <R> accept(visitor: Visitor<R>) = visitor.visit$className$baseName(this)")
    writer.println("    }\n")
}

fun defineVisitor(
    writer: PrintWriter,
    baseName: String,
    types: List<String>,
) {
    writer.println("    interface Visitor<R> {")

    for (type in types) {
        val typeName = type.split("-")[0].trim()
        writer.println("        fun visit$typeName$baseName(${baseName.lowercase()}: $typeName): R")
    }
    writer.println("    }\n")
}
