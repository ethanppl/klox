package org.klox

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

private var hadError = false

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(args: Array<String>) {
    // Do not allow more than 1 argument
    // Convention from unix sysexits.h header
    // https://man.freebsd.org/cgi/man.cgi?query=sysexits&apropos=0&sektion=0&manpath=FreeBSD+4.3-RELEASE&format=html
    if (args.size > 1) {
        println("Usage: klox [script")

        exitProcess(64)
    }

    if (args.size == 1) {
        runFile(args[0])
    } else {
        runPrompt()
    }
}

fun runFile(path: String) {
    val filePath = Paths.get(path)
    val bytes = Files.readAllBytes(filePath)

    run(String(bytes, Charset.defaultCharset()))

    if (hadError) {
        exitProcess(65)
    }
}

fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
        print("> ")

        val line: String = reader.readLine()
        run(line)

        hadError = false
    }
}

fun run(source: String) {
    val scanner = Scanner(source)
    val tokens: ArrayList<Token> = scanner.scanTokens()

    val parser = Parser(tokens)
    val expression = parser.parse()

    // Stop if there was a syntax error
    if (hadError || expression == null) {
        return
    }

    println(AstPrinter().print(expression))
}

object Klox {
    fun error(line: Int, message: String) {
        reportError(line, "", message)
    }

    fun error(token: Token, message: String) {
        if (token.type == TokenType.EOF) {
            reportError(token.line, "at end", message)
        } else {
            reportError(token.line, "at '${token.lexeme}'", message)
        }
    }

    fun reportError(line: Int, location: String, message: String) {
        println("[line $line] Error $location: $message")
        hadError = true
    }
}