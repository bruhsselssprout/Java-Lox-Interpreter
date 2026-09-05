package com.craftinginterpreters.lox;

class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    // Constructs a new token with the given type, lexeme, literal value, and line number.
    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    // Returns a string representation of the token, including its type, lexeme, and literal value.
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
