package org.klox

class Scanner(private val source: String) {
    private val tokens: ArrayList<Token> = ArrayList()
    private val sourceCharArray: CharArray = source.toCharArray()

    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): ArrayList<Token> {
        // Repeatedly scan the file for tokens
        while(!isAtEnd()) {
            // Reset the start of each token
            start = current
            scanToken()
        }

        // Reaching the end of the file
        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun scanToken() {
        when (val c = advance()) {
            // single character lexeme
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)

            // operators
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)

            // longer lexemes
            '/' -> handleSlash()
            '"' -> handleString()

            // not lexemes
            ' ', '\r', '\t' -> Unit
            '\n' -> line++

            else -> handleOtherChar(c)
        }
    }

    private fun handleSlash() {
        // A comment goes until the end of the line/file
        if (match('/')) {
            while (peek() != '\n' && !isAtEnd()) {
                advance()
            }

            return
        }

        addToken(TokenType.SLASH)
    }

    private fun handleString() {
        // The string is continuing
        while (peek() != '"' && !isAtEnd()) {
            // This means we support multi-line strings
            if (peek() == '\n') {
                line++
            }

            advance()
        }

        // String without ending double quotation mark
        if (isAtEnd()) {
            Klox.error(line, "Unterminated string (string ending without \")")
            return
        }

        // Advance pass the closing double quotation mark
        advance()

        // Trim the surrounding quotes
        val string = source.substring(start+1, current-1)
        addToken(TokenType.STRING, string)
    }

    private fun handleOtherChar(c: Char) {
        if (isDigit(c)) {
            handleNumber()
            return
        }

        if (isAlpha(c)) {
            handleIdentifier()
            return
        }

        Klox.error(line, "Unexpected character: $c")
    }

    private fun handleNumber() {
        while (isDigit(peek())) {
            advance()
        }

        // Look for decimal point
        if (peek() == '.' && isDigit(peek(1))) {
            // Consume the "."
            advance()

            while (isDigit(peek())) {
                advance()
            }
        }

        val number: Double = source.substring(start, current).toDouble()
        addToken(TokenType.NUMBER, number)
    }

    private fun handleIdentifier() {
        while (isAlphaNumeric(peek())) {
            advance()
        }

        val text: String = source.substring(start, current)
        var type: TokenType? = Keywords[text]
        if (type == null) {
            type = TokenType.IDENTIFIER
        }

        addToken(type)
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    // Get the current character and move the current pointer
    private fun advance(): Char {
        return sourceCharArray[current++]
    }

    // Peek the current or more character and do not move the current pointer
    private fun peek(offset: Int = 0): Char {
        if (current + offset >= source.length) {
            return '\n'
        }

        return sourceCharArray[current + offset]
    }

    // Given an expected character, peek at the current character
    // If character match the expected character, move the current pointer and return true
    // Else return false
    private fun match(expectedCharacter: Char): Boolean {
        // At the end of the file
        if (isAtEnd()) {
            return false
        }

        // If mismatch
        if (peek() != expectedCharacter) {
            return false
        }

        // If match
        current++
        return true
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c == '_'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isDigit(c) || isAlpha(c)
    }

    object Keywords: HashMap<String, TokenType>() {
        private fun readResolve(): Any = Keywords

        init {
            put("and", TokenType.AND)
            put("class", TokenType.CLASS)
            put("else", TokenType.ELSE)
            put("false", TokenType.FALSE)
            put("for", TokenType.FOR)
            put("fun", TokenType.FUN)
            put("if", TokenType.IF)
            put("nil", TokenType.NIL)
            put("or", TokenType.OR)
            put("print", TokenType.PRINT)
            put("return", TokenType.RETURN)
            put("super", TokenType.SUPER)
            put("this", TokenType.THIS)
            put("true", TokenType.TRUE)
            put("var", TokenType.VAR)
            put("while", TokenType.WHILE)
        }
    }
}