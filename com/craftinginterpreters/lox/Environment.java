package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();
    private final List<Object> locals = new ArrayList<>();

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

    // Retrieves the value of a variable with the given name.
    // If the variable is not found in the current environment, it checks the enclosing environments recursively.
    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
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
        if (enclosing == null) {
            values.put(name, value);
        } else {
            locals.add(value);
        }
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

    Object getAt(int distance, int index) {
        return ancestor(distance).locals.get(index);
    }

    void assignAt(int distance, int index, Object value) {
        ancestor(distance).locals.set(index, value);
    }
}
