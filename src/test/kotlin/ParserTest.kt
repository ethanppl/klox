import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.klox.*

class ParserTest {
    private fun testParseTokens(tokens: ArrayList<Token>, expectedExpression: Expr) {
        val parser = Parser(tokens)
        val expression = parser.parse()
        val printer = AstPrinter()

        val expressionInString = printer.print(expectedExpression)
        val expectedExpressionInString = printer.print(expression!!)

        assertNotNull(expression)
        assertEquals(
            expressionInString,
            expectedExpressionInString
        )
    }

    @Test
    fun parsePrimary() {
        var tokens = ArrayList<Token>(
            mutableListOf(
                Token(TokenType.FALSE, "false", false, 1),
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Literal(false))

        tokens = ArrayList(
            mutableListOf(
                Token(TokenType.TRUE, "true", true, 1),
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Literal(true))

        tokens = ArrayList(
            mutableListOf(
                Token(TokenType.NIL, "nil", null, 1),
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Literal(null))

        tokens = ArrayList(
            mutableListOf(
                Token(TokenType.NUMBER, "34", 34.toDouble(), 1),
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Literal(34.toDouble()))

        tokens = ArrayList(
            mutableListOf(
                Token(TokenType.STRING, "abc", "abc", 1),
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Literal("abc"))
    }

    @Test
    fun parseUnary() {
        val bangToken = Token(TokenType.BANG, "!", null, 1)
        val minusToken = Token(TokenType.MINUS, "-", null, 1)
        val numberExpr = Expr.Literal(34.toDouble())

        var tokens = ArrayList<Token>(
            mutableListOf(
                bangToken,
                Token(TokenType.NUMBER, "34", 34.toDouble(), 1),
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Unary(bangToken, numberExpr))

        tokens = ArrayList(
            mutableListOf(
                minusToken,
                Token(TokenType.NUMBER, "34", 34.toDouble(), 1),
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Unary(minusToken, numberExpr))
    }

    private val numberToken = Token(TokenType.NUMBER, "34", 34.toDouble(), 1)
    private val numberExpr = Expr.Literal(34.toDouble())

    @Test
    fun parseFactor() {
        val slashToken = Token(TokenType.SLASH, "/", null, 1)
        val starToken = Token(TokenType.STAR, "*", null, 1)

        var tokens = ArrayList<Token>(
            mutableListOf(
                numberToken,
                slashToken,
                numberToken,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(numberExpr, slashToken, numberExpr))

        tokens = ArrayList(
            mutableListOf(
                numberToken,
                starToken,
                numberToken,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(numberExpr, starToken, numberExpr))
    }

    private val slashToken = Token(TokenType.SLASH, "/", null, 1)
    private val factorTokens = arrayOf(numberToken, slashToken, numberToken)
    private val factorExpr = Expr.Binary(numberExpr, slashToken, numberExpr)

    @Test
    fun parseTerm() {
        val minusToken = Token(TokenType.MINUS, "-", null, 1)
        val plusToken = Token(TokenType.PLUS, "+", null, 1)

        var tokens = ArrayList<Token>(
            mutableListOf(
                *factorTokens,
                minusToken,
                *factorTokens,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(factorExpr, minusToken, factorExpr))

        tokens = ArrayList(
            mutableListOf(
                *factorTokens,
                plusToken,
                *factorTokens,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(factorExpr, plusToken, factorExpr))
    }

    private val minusToken = Token(TokenType.MINUS, "-", null, 1)
    private val termTokens = arrayOf(*factorTokens, minusToken, *factorTokens)
    private val termExpr = Expr.Binary(factorExpr, minusToken, factorExpr)

    @Test
    fun parseComparison() {
        val greaterToken = Token(TokenType.GREATER, ">", null, 1)
        val greaterEqToken = Token(TokenType.GREATER_EQUAL, ">=", null, 1)
        val lessToken = Token(TokenType.LESS, "<", null, 1)
        val lessEqToken = Token(TokenType.LESS_EQUAL, "<=", null, 1)

        var tokens = ArrayList<Token>(
            mutableListOf(
                *termTokens,
                greaterToken,
                *termTokens,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(termExpr, greaterToken, termExpr))

        tokens = ArrayList(
            mutableListOf(
                *termTokens,
                greaterEqToken,
                *termTokens,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(termExpr, greaterEqToken, termExpr))

        tokens = ArrayList(
            mutableListOf(
                *termTokens,
                lessToken,
                *termTokens,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(termExpr, lessToken, termExpr))

        tokens = ArrayList(
            mutableListOf(
                *termTokens,
                lessEqToken,
                *termTokens,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(termExpr, lessEqToken, termExpr))
    }

    private val greaterToken = Token(TokenType.GREATER, ">", null, 1)
    private val comparisonTokens = arrayOf(*termTokens, greaterToken, *termTokens)
    private val comparisonExpr = Expr.Binary(termExpr, greaterToken, termExpr)

    @Test
    fun parseEquality() {
        val equalEqToken = Token(TokenType.EQUAL_EQUAL, "==", null, 1)
        val bangEqToken = Token(TokenType.BANG_EQUAL, "!=", null, 1)

        var tokens = ArrayList<Token>(
            mutableListOf(
                *comparisonTokens,
                equalEqToken,
                *comparisonTokens,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(comparisonExpr, equalEqToken, comparisonExpr))

        tokens = ArrayList(
            mutableListOf(
                *comparisonTokens,
                bangEqToken,
                *comparisonTokens,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(comparisonExpr, bangEqToken, comparisonExpr))
    }

    @Test
    fun parseParenthesis() {
        val equalEqToken = Token(TokenType.EQUAL_EQUAL, "==", null, 1)
        val leftParenToken = Token(TokenType.LEFT_PAREN, "(", null, 1)
        val rightParenToken = Token(TokenType.RIGHT_PAREN, ")", null, 1)

        var tokens = ArrayList<Token>(
            mutableListOf(
                leftParenToken,
                *comparisonTokens,
                rightParenToken,
                equalEqToken,
                *comparisonTokens,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(Expr.Grouping(comparisonExpr), equalEqToken, comparisonExpr))

        tokens = ArrayList(
            mutableListOf(
                leftParenToken,
                *comparisonTokens,
                rightParenToken,
                minusToken,
                numberToken,
                Token(TokenType.EOF, "", null, 1)
            )
        )
        testParseTokens(tokens, Expr.Binary(Expr.Grouping(comparisonExpr), minusToken, numberExpr))
    }
}
