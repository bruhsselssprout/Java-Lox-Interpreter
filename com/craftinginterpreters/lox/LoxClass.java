package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Map;

class LoxClass implements LoxCallable {
    final String name;
    final LoxClass superclass;
    private final Map<String, LoxFunction> methods;

    LoxClass(String name, LoxClass superclass, 
            Map<String, LoxFunction> methods) {
        this.superclass = superclass;
        this.name = name;
        this.methods = methods;

        // Challenge 13.2 - Replace super with BETA-style inner functions
        // Set the declaring class for each method to this class.
        for (LoxFunction method : methods.values()) {
            method.setDeclaringClass(this);
        }
    }

    LoxFunction findMethod(String name) {
        if (superclass != null) {
            LoxFunction method = superclass.findMethod(name);
            if (method != null) return method;
        }

        return methods.get(name);
    }

    // Challenge 13.2 - Replace super with BETA-style inner functions
    // Find a method in the class hierarchy after the specified declaring class.
    LoxFunction findMethodAfter(String name, LoxClass declaringClass) {
        if (this == declaringClass) return null;

        LoxFunction method = superclass == null ? null :
            superclass.findMethodAfter(name, declaringClass);
        if (method != null) return method;

        return methods.get(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override 
    public Object call(Interpreter interpreter, 
                        List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override 
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }
}
