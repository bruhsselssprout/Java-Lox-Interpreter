package com.craftinginterpreters.lox;

// Challenge 12.2 - Extend Lox to support getter methods
// Collections util to create empty argument lists for getter methods
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class LoxInstance {
    // Challenge 12.1 - Implement "static" methods handling with metaclasses
    // Protected, not private, so that LoxClass can access it for metaclass handling
    protected LoxClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    // Challenge 12.2 - Extend Lox to support getter methods
    // Added Interpreter parameter to handle getter method calls
    Object get(Token name, Interpreter interpreter) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        LoxFunction method = klass.findMethod(name.lexeme);
        // Challenge 12.2 - Extend Lox to support getter methods
        // If the method is a getter, call it with an empty argument list
        if (method != null) {
            LoxFunction bound = method.bind(this);
            if (bound.isGetter()) {
                return bound.call(interpreter, Collections.emptyList());
            }
            return bound;
        }

        throw new RuntimeError(name,
            "Undefined property '" + name.lexeme + "'.");
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
