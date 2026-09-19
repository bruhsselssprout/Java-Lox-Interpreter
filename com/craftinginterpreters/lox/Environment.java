package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
    // Challenge 8.2 - Sentinel marking a declared but not yet initialized variable.
    private static final Object UNINITIALIZED = new Object();

    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    // Creates a new environment with no enclosing environment (global scope).
    // This is typically used for the global scope.
    Environment() {
        enclosing = null;
    }

    // Creates a new environment with the given enclosing environment (nested scope).
    // This is typically used for local scopes within functions or blocks.
    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    // Challenge 8.2 - Modified get to check for uninitialized variables.
    // Throws a RuntimeError if the variable is uninitialized.
    // Retrieves the value of a variable with the given name.
    // If the variable is not found in the current environment, it checks the enclosing environments recursively.
    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            Object value = values.get(name.lexeme);
            if (value == UNINITIALIZED) {
                throw new RuntimeError(name,
                    "Uninitialized variable '" + name.lexeme + "'.");
            }
            return value;
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,
            "Undefined variable '" + name.lexeme + "'.");
    }

    // Assigns a new value to an existing variable with the given name.
    // If the variable is not found in the current environment, it checks the enclosing environments recursively.
    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name,
            "Undefined variable '" + name.lexeme + "'.");
    }

    // Defines a new variable in the current environment. This does not check enclosing environments.
    void define(String name, Object value) {
        values.put(name, value);
    }

    // Challenge 8.2 - Declares a new variable without a value, marking it as uninitialized until assigned.
    void declare(String name) {
        values.put(name, UNINITIALIZED);
    }
}
