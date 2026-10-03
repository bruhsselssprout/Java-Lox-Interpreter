package com.craftinginterpreters.lox;

// Challenge 13.2 - Replace super with BETA-style inner functions
// Collections util for empty lists used in callInner method
import java.util.Collections;
import java.util.List;

class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    
    // Challenge 13.2 - Replace super with BETA-style inner functions
    // The declaring class of the function, used for resolving inner functions.
    private LoxClass declaringClass;

    private final boolean isInitializer;

    LoxFunction(Stmt.Function declaration, Environment closure,
                boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.closure = closure;
        this.declaration = declaration;
    }

    LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        
        // Challenge 13.2 - Replace super with BETA-style inner functions
        // Set the declaring class for the bound function to the same as this function's declaring class.
        LoxFunction bound = new LoxFunction(declaration, environment,
            isInitializer);
        bound.declaringClass = declaringClass;
        return bound;
    }

    // Challenge 13.2 - Replace super with BETA-style inner functions
    // Set the declaring class for this function.
    void setDeclaringClass(LoxClass declaringClass) {
        this.declaringClass = declaringClass;
    }

    // Challenge 13.2 - Replace super with BETA-style inner functions
    // Call the inner function logic of the currently executing function.
    Object callInner(Interpreter interpreter) {
        LoxInstance instance = (LoxInstance)closure.getAt(0, "this");
        LoxFunction method = instance.findMethodAfter(
            declaration.name.lexeme, declaringClass);
        if (method != null) {
            method.bind(instance).call(interpreter, Collections.emptyList());
        }
        return null;
    }

    @Override 
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
    
    @Override 
    public int arity() {
        return declaration.params.size();
    }

    @Override 
    public Object call(Interpreter interpreter, 
                        List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, 
                arguments.get(i));
        }

        // Challenge 13.2 - Replace super with BETA-style inner functions
        LoxFunction enclosingFunction = interpreter.currentFunction;
        interpreter.currentFunction = this;
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) return closure.getAt(0, "this");

            return returnValue.value;
        } finally {
            interpreter.currentFunction = enclosingFunction;
        }

        if (isInitializer) return closure.getAt(0, "this");
        return null;
    }
}
    
