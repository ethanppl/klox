import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.klox.Scanner
import org.klox.Token
import org.klox.TokenType

class ScannerTest {
    private fun testScanTokens(source: String, expectedTokens: ArrayList<Token>) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()

        assertEquals(expectedTokens.size, tokens.size)
        expectedTokens.forEachIndexed { index, element ->
            val actual = tokens[index]
            assert(actual == element) { "At $index, expected: $element, actual: $actual" }
        }
    }

    @Test
    fun scanSingleCharacters() {
        var testString = "()"
        var expectedTokens = ArrayList<Token>(mutableListOf(
            Token(TokenType.LEFT_PAREN, "(", null, 1),
            Token(TokenType.RIGHT_PAREN, ")", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "{}"
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.LEFT_BRACE, "{", null, 1),
            Token(TokenType.RIGHT_BRACE, "}", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = ", . - + ; *"
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.COMMA, ",", null, 1),
            Token(TokenType.DOT, ".", null, 1),
            Token(TokenType.MINUS, "-", null, 1),
            Token(TokenType.PLUS, "+", null, 1),
            Token(TokenType.SEMICOLON, ";", null, 1),
            Token(TokenType.STAR, "*", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)
    }

    @Test
    fun scanOperators() {
        var testString = "! !="
        var expectedTokens = ArrayList<Token>(mutableListOf(
            Token(TokenType.BANG, "!", null, 1),
            Token(TokenType.BANG_EQUAL, "!=", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "= =="
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.EQUAL, "=", null, 1),
            Token(TokenType.EQUAL_EQUAL, "==", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "< <="
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.LESS, "<", null, 1),
            Token(TokenType.LESS_EQUAL, "<=", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "> >="
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.GREATER, ">", null, 1),
            Token(TokenType.GREATER_EQUAL, ">=", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)
    }

    @Test
    fun scanSlashAndComments() {
        var testString = "/"
        var expectedTokens = ArrayList<Token>(mutableListOf(
            Token(TokenType.SLASH, "/", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "// comment\n \r \t/"
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.SLASH, "/", null, 2),
            Token(TokenType.EOF, "", null, 2)
        ))
        testScanTokens(testString, expectedTokens)
    }

    @Test
    fun scanString() {
        var testString = "\"hello\""
        var expectedTokens = ArrayList<Token>(mutableListOf(
            Token(TokenType.STRING, "\"hello\"", "hello", 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "\"a\nb\nc\""
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.STRING, "\"a\nb\nc\"", "a\nb\nc", 3),
            Token(TokenType.EOF, "", null, 3)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "\"\""
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.STRING, "\"\"", "", 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)
    }

    @Test
    fun scanNumbers() {
        var testString = "3"
        var expectedTokens = ArrayList<Token>(mutableListOf(
            Token(TokenType.NUMBER, "3", 3.toDouble(), 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "0.33"
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.NUMBER, "0.33", 0.33, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "12 34 56 78 90"
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.NUMBER, "12", 12.toDouble(), 1),
            Token(TokenType.NUMBER, "34", 34.toDouble(), 1),
            Token(TokenType.NUMBER, "56", 56.toDouble(), 1),
            Token(TokenType.NUMBER, "78", 78.toDouble(), 1),
            Token(TokenType.NUMBER, "90", 90.toDouble(), 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "\"\""
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.STRING, "\"\"", "", 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)
    }

    @Test
    fun scanIdentifiers() {
        var testString = "variable"
        var expectedTokens = ArrayList<Token>(mutableListOf(
            Token(TokenType.IDENTIFIER, "variable", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "and class else false for fun if nil or print return super this true var while"
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.AND, "and", null, 1),
            Token(TokenType.CLASS, "class", null, 1),
            Token(TokenType.ELSE, "else", null, 1),
            Token(TokenType.FALSE, "false", null, 1),
            Token(TokenType.FOR, "for", null, 1),
            Token(TokenType.FUN, "fun", null, 1),
            Token(TokenType.IF, "if", null, 1),
            Token(TokenType.NIL, "nil", null, 1),
            Token(TokenType.OR, "or", null, 1),
            Token(TokenType.PRINT, "print", null, 1),
            Token(TokenType.RETURN, "return", null, 1),
            Token(TokenType.SUPER, "super", null, 1),
            Token(TokenType.THIS, "this", null, 1),
            Token(TokenType.TRUE, "true", null, 1),
            Token(TokenType.VAR, "var", null, 1),
            Token(TokenType.WHILE, "while", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)

        testString = "anderson or andy and orchid"
        expectedTokens = ArrayList(mutableListOf(
            Token(TokenType.IDENTIFIER, "anderson", null, 1),
            Token(TokenType.OR, "or", null, 1),
            Token(TokenType.IDENTIFIER, "andy", null, 1),
            Token(TokenType.AND, "and", null, 1),
            Token(TokenType.IDENTIFIER, "orchid", null, 1),
            Token(TokenType.EOF, "", null, 1)
        ))
        testScanTokens(testString, expectedTokens)
    }
}