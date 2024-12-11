package org.klox

class Parser(private val tokens: ArrayList<Token>) {
    private var current = 0

    fun parse(): Expr? {
        return try {
            expression()
        } catch (error: ParseError) {
            null
        }
    }

    // expression     → equality ;
    private fun expression(): Expr {
        return equality()
    }

    // equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    private fun equality(): Expr {
        return parseBinaryExpression(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL) {
            comparison()
        }
    }

    // comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private fun comparison(): Expr {
        return parseBinaryExpression(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL) {
            term()
        }
    }

    // term           → factor ( ( "-" | "+" ) factor )* ;
    private fun term(): Expr {
        return parseBinaryExpression(TokenType.MINUS, TokenType.PLUS) {
            factor()
        }
    }

    // factor         → unary ( ( "/" | "*" ) unary )* ;
    private fun factor(): Expr {
        return parseBinaryExpression(TokenType.SLASH, TokenType.STAR) {
            unary()
        }
    }

    // unary          → ( "!" | "-" ) unary | primary ;
    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }

        return primary()
    }

    // primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
    private fun primary(): Expr {
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Expr.Literal(previous().literal)
        }

        if (match(TokenType.TRUE)) {
            return Expr.Literal(true)
        }

        if (match(TokenType.FALSE)) {
            return Expr.Literal(false)
        }

        if (match(TokenType.NIL)) {
            return Expr.Literal(null)
        }

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }

        throw error(peek(), "Expect expression.")
    }

    private fun synchronize() {
        advance()

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) {
                return
            }

            when (peek().type) {
                TokenType.CLASS, TokenType.FUN, TokenType.VAR, TokenType.FOR, TokenType.IF, TokenType.WHILE, TokenType.RETURN -> return
                else -> advance()
            }
        }
    }


    private fun parseBinaryExpression(vararg types: TokenType, parseSubexpression: () -> Expr): Expr {
        var expr = parseSubexpression()

        while (match(*types)) {
            val operator = previous()
            val right = parseSubexpression()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }

        return false
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) {
            return false
        }

        return peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) {
            current++
        }

        return previous()
    }

    private fun isAtEnd(): Boolean {
        return peek().type == TokenType.EOF
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) {
            return advance()
        }

        throw error(peek(), message)
    }

    private fun error(token: Token, message: String): ParseError {
        Klox.error(token, message)

        return ParseError()
    }

    private class ParseError : RuntimeException()
}