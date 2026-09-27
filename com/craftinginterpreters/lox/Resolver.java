package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    // Challenge 11.3 - Extend the resolver to report an error if a local variable is never used.
    // Using variable instead of just a boolean to track its state.
    private final Stack<Map<String, Variable>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION
    }

    // Challenge 11.3 - Extend the resolver to report an error if a local variable is never used.
    // Variable states for tracking usage.
    private enum VariableState {
        DECLARED,
        DEFINED,
        USED
    }

    // Challenge 11.3 - Extend the resolver to report an error if a local variable is never used.
    // Variable class for tracking name and usage state.
    private static class Variable {
        final Token name;
        VariableState state;

        Variable(Token name, VariableState state) {
            this.name = name;
            this.state = state;
        }
    }

    void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    @Override 
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override 
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override 
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override 
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    @Override 
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override 
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Can't return from top-level code.");
        }
        
        if (stmt.value != null) {
            resolve(stmt.value);
        }

        return null;
    }

    @Override 
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override 
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override 
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override 
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);

        for (Expr argument : expr.arguments) {
            resolve(argument);
        }

        return null;
    }

    @Override 
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override 
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override 
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override 
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    // Challenge 11.3 - Extend the resolver to report an error if a local variable is never used.
    // Mark variables as used when they are read.
    @Override 
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty()) {
            Variable variable = scopes.peek().get(expr.name.lexeme);
            if (variable != null && variable.state == VariableState.DECLARED) {
                Lox.error(expr.name, 
                    "Can't read local variable in its own initializer.");
            }
        }
        
        resolveLocal(expr, expr.name);

        // Mark the variable as used after it has been resolved.
        markUsed(expr.name);
        return null;
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void resolveFunction(
            Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Variable>());
    }

    // Challenge 11.3 - Extend the resolver to report an error if a local variable is never used.
    // This is handled in endScope() by checking if any variable in the scope is not used.
    private void endScope() {
        Map<String, Variable> scope = scopes.pop();
        for (Variable variable : scope.values()) {
            if (variable.state != VariableState.USED) {
                Lox.error(variable.name, "Local variable is never used.");
            }
        }
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        Map<String, Variable> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, 
                "Already a variable with this name in this scope.");
        }

        scope.put(name.lexeme, new Variable(name, VariableState.DECLARED));
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().get(name.lexeme).state = VariableState.DEFINED;
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    // Challenge 11.3 - Extend the resolver to report an error if a local variable is never used.
    // Mark variables as used when they are read.
    private void markUsed(Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Variable variable = scopes.get(i).get(name.lexeme);
            if (variable != null) {
                variable.state = VariableState.USED;
                return;
            }
        }
    }
}
