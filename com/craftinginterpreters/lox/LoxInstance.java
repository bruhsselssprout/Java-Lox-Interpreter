package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class LoxInstance {
    private LoxClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        LoxFunction method = klass.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(name,
            "Undefined property '" + name.lexeme + "'.");
    }

    // Challenge 13.2 - Replace super with BETA-style inner functions
    // Find a method in the class after the specified declaring class.
    LoxFunction findMethodAfter(String name, LoxClass declaringClass) {
        return klass.findMethodAfter(name, declaringClass);
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
