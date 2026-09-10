package com.craftinginterpreters.lox;

class RPNAstPrinter implements Expr.Visitor<String> {
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return postfix(expr.left, expr.right, expr.operator.lexeme);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return expr.expression.accept(this);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return postfix(expr.right, expr.operator.lexeme);
    }

    private String postfix(Expr left, Expr right, String operator) {
        return left.accept(this) + " " + right.accept(this) + " " + operator;
    }

    private String postfix(Expr expression, String operator) {
        return expression.accept(this) + " " + operator;
    }

    // For printing examples in the main method.
    private static void printExample(String regular, Expr expression) {
        RPNAstPrinter printer = new RPNAstPrinter();
        System.out.println("Regular: " + regular);
        System.out.println("RPN:     " + printer.print(expression));
        System.out.println();
    }
    
    public static void main(String[] args) {
        Expr first = new Expr.Binary(
            new Expr.Unary(
                new Token(TokenType.MINUS, "-", null, 1),
                new Expr.Literal(123)),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Grouping(
                new Expr.Literal(45.67)));

        printExample("(-123) * 45.67", first);

        Expr second = new Expr.Binary(
            new Expr.Grouping(
                new Expr.Binary(
                    new Expr.Literal(1),
                    new Token(TokenType.PLUS, "+", null, 1),
                    new Expr.Literal(2))),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Grouping(
                new Expr.Binary(
                    new Expr.Literal(4),
                    new Token(TokenType.MINUS, "-", null, 1),
                    new Expr.Literal(3))));
        
        printExample("(1 + 2) * (4 - 3)", second);

        Expr third = new Expr.Unary(
            new Token(TokenType.MINUS, "-", null, 1),
            new Expr.Grouping(
                new Expr.Binary(
                    new Expr.Literal(5),
                    new Token(TokenType.PLUS, "+", null, 1),
                    new Expr.Literal(4))));
        
        printExample("-(5 + 4)", third);

        Expr fourth = new Expr.Binary(
            new Expr.Literal(1),
            new Token(TokenType.PLUS, "+", null, 1),
            new Expr.Binary(
                new Expr.Literal(2),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Literal(3)));
        
        printExample("1 + (2 * 3)", fourth);
    }
}
