import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.klox.*

class InterpreterTest {
    private val interpreter = Interpreter()

    @Test
    fun evaluateLiteral() {
        var expr = Expr.Literal(1.0)
        var eval = interpreter.evaluate(expr)

        assertEquals(1.0, eval)

        expr = Expr.Literal("hi")
        eval = interpreter.evaluate(expr)

        assertEquals("hi", eval)

        expr = Expr.Literal(true)
        eval = interpreter.evaluate(expr)

        assertEquals(true, eval)
    }

    private val oneLiteral = Expr.Literal(1.0)
    private val twoLiteral = Expr.Literal(2.0)
    private val stringLiteral = Expr.Literal("he")

    @Test
    fun evaluateGroup() {
        var expr = Expr.Grouping(oneLiteral)
        var eval = interpreter.evaluate(expr)
        assertEquals(1.0, eval)

        expr = Expr.Grouping(expr)
        eval = interpreter.evaluate(expr)
        assertEquals(1.0, eval)
    }

    @Test
    fun evaluateUnary() {
        val bangToken = Token(TokenType.BANG, "!", null, 1)
        var expr = Expr.Unary(bangToken, oneLiteral)
        var eval = interpreter.evaluate(expr)
        assertEquals(false, eval)

        expr = Expr.Unary(bangToken, Expr.Literal(""))
        eval = interpreter.evaluate(expr)
        assertEquals(false, eval)

        expr = Expr.Unary(bangToken, Expr.Literal(false))
        eval = interpreter.evaluate(expr)
        assertEquals(true, eval)

        expr = Expr.Unary(bangToken, Expr.Literal(true))
        eval = interpreter.evaluate(expr)
        assertEquals(false, eval)

        expr = Expr.Unary(Token(TokenType.MINUS, "-", null, 1), oneLiteral)
        eval = interpreter.evaluate(expr)
        assertEquals(-1.0, eval)
    }

    @Test
    fun evaluateBinary() {
        var token = Token(TokenType.MINUS, "-", null, 1)
        var expr = Expr.Binary(oneLiteral, token, twoLiteral)
        var eval = interpreter.evaluate(expr)
        assertEquals(-1.0, eval)

        token = Token(TokenType.SLASH, "/", null, 1)
        expr = Expr.Binary(oneLiteral, token, twoLiteral)
        eval = interpreter.evaluate(expr)
        assertEquals(0.5, eval)

        token = Token(TokenType.STAR, "*", null, 1)
        expr = Expr.Binary(oneLiteral, token, twoLiteral)
        eval = interpreter.evaluate(expr)
        assertEquals(2.0, eval)

        token = Token(TokenType.PLUS, "+", null, 1)
        expr = Expr.Binary(oneLiteral, token, twoLiteral)
        eval = interpreter.evaluate(expr)
        assertEquals(3.0, eval)

        token = Token(TokenType.PLUS, "+", null, 1)
        expr = Expr.Binary(stringLiteral, token, stringLiteral)
        eval = interpreter.evaluate(expr)
        assertEquals("hehe", eval)

        token = Token(TokenType.GREATER, ">", null, 1)
        expr = Expr.Binary(oneLiteral, token, twoLiteral)
        eval = interpreter.evaluate(expr)
        assertEquals(false, eval)

        token = Token(TokenType.GREATER_EQUAL, ">=", null, 1)
        expr = Expr.Binary(oneLiteral, token, oneLiteral)
        eval = interpreter.evaluate(expr)
        assertEquals(true, eval)

        token = Token(TokenType.LESS, "<", null, 1)
        expr = Expr.Binary(oneLiteral, token, twoLiteral)
        eval = interpreter.evaluate(expr)
        assertEquals(true, eval)

        token = Token(TokenType.LESS_EQUAL, "<=", null, 1)
        expr = Expr.Binary(twoLiteral, token, twoLiteral)
        eval = interpreter.evaluate(expr)
        assertEquals(true, eval)

        token = Token(TokenType.BANG_EQUAL, "!=", null, 1)
        expr = Expr.Binary(twoLiteral, token, twoLiteral)
        eval = interpreter.evaluate(expr)
        assertEquals(false, eval)

        token = Token(TokenType.EQUAL_EQUAL, "!=", null, 1)
        expr = Expr.Binary(twoLiteral, token, twoLiteral)
        eval = interpreter.evaluate(expr)
        assertEquals(true, eval)
    }
}