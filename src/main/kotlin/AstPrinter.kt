package org.klox

class AstPrinter : Expr.Visitor<String> {
    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    private fun parenthesize(name: String, vararg expressions: Expr): String {
        var output = ""

        output += "($name"
        for (expr in expressions) {
            output += " "
            output += expr.accept(this)
        }
        output += ")"

        return output
    }

    override fun visitBinaryExpr(expr: Expr.Binary): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): String {
        return parenthesize("group", expr.expression)
    }


    override fun visitLiteralExpr(expr: Expr.Literal): String {
        return if (expr.value == null) {
            "nil"
        } else {
            expr.value.toString()
        }
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }
}

fun main() {
    val expression: Expr = Expr.Binary(
        Expr.Unary(
            Token(TokenType.MINUS, "-", null, 1),
            Expr.Literal(123)
        ),
        Token(TokenType.STAR, "*", null, 1),
        Expr.Grouping(Expr.Literal(45.67))
    )

    val printer = AstPrinter()
    println(printer.print(expression))
}