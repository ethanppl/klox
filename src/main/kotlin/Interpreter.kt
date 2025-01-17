package org.klox

class Interpreter : Expr.Visitor<Any?> {
    fun evaluate(expr: Expr): Any? {
        // send the expression back into the interpreter's visitor implementation
        return expr.accept(this)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        // evaluating literals is just return the value of the literal
        return expr.value
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        // recursively evaluate the subexpression and return it
        return evaluate(expr.expression)
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.BANG ->
                !isTruthy(right)

            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                -(right as Double)
            }

            else ->
                // unreachable
                null
        }
    }

    // Anything that is not `null` or `false` is truthy
    // https://craftinginterpreters.com/evaluating-expressions.html#truthiness-and-falsiness
    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }

        if (obj is Boolean) {
            return obj
        }

        return true
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) - (right as Double)
            }

            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) / (right as Double)
            }

            TokenType.STAR -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) * (right as Double)
            }

            TokenType.PLUS -> {
                if (left is Double && right is Double) {
                    return left + right
                }

                if (left is String && right is String) {
                    return left + right
                }

                throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.")
            }

            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) > (right as Double)
            }

            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) >= (right as Double)
            }

            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) < (right as Double)
            }

            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) <= (right as Double)
            }

            TokenType.BANG_EQUAL ->
                return !isEqual(left, right)

            TokenType.EQUAL_EQUAL ->
                return isEqual(left, right)

            else ->
                // unreachable
                null
        }
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) {
            return true
        }

        if (a == null) {
            return false
        }

        return a == b
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) {
            return
        }

        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) {
            return
        }

        throw RuntimeError(operator, "Operand must be numbers.")
    }

    fun interpret(expression: Expr) {
        try {
            val value = evaluate(expression)
            println(stringify(value))
        } catch (error: RuntimeError) {
            Klox.runtimeError(error)
        }
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) {
            return "nil"
        }

        if (obj is Double) {
            var text = obj.toString()

            // just to make it looks like integer
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }

            return text
        }

        return obj.toString()
    }
}

class RuntimeError(val token: Token, message: String) : RuntimeException(message)