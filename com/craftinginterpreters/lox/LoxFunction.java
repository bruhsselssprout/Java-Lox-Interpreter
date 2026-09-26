package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
    private final List<Token> params;
    private final List<Stmt> body;
    private final String name;
    private final Environment closure;


    // Challenge 10.2 - Implement anonymous function syntax
    // Modified constructor to handle named functions.
    LoxFunction(Stmt.Function declaration, Environment closure) {
        this(declaration.name.lexeme, declaration.params, declaration.body, closure);
    }

    // Challenge 10.2 - Implement anonymous function syntax
    // This constructor is used for anonymous functions, which do not have a name.
    LoxFunction(Expr.Function declaration, Environment closure) {
        this(null, declaration.params, declaration.body, closure);
    }

    // Challenge 10.2 - Implement anonymous function syntax
    // Private constructor used by both named and anonymous functions.
    private LoxFunction(String name, List<Token> params,
                        List<Stmt> body, Environment closure) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.closure = closure;
    }

    @Override 
    public String toString() {
        return name == null ? "<fn>" : "<fn " + name + ">";
    }
    
    @Override 
    public int arity() {
        return params.size();
    }

    @Override 
    public Object call(Interpreter interpreter, 
                        List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < params.size(); i++) {
            environment.define(params.get(i).lexeme,
                arguments.get(i));
        }

        try {
            interpreter.executeBlock(body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        return null;
    }
}
    
