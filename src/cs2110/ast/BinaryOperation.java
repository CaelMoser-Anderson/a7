package cs2110.ast;

import javax.naming.ldap.UnsolicitedNotification;
import java.util.function.BiFunction;

/**
 * An operation performed on two arithmetic expressions, `left` and `right`, that is represented by
 * the given `symbol` and modeled by the `BiFunction op`.
 */
public record BinaryOperation(Expression left, Expression right, char symbol,
                              BiFunction<Integer, Integer, Integer> op) implements Expression {

    /**
     * Returns the index of the rootIndex of the String excluding its leading and trailing spaces.
     * Used to determine where to position the parent connection for the subtree root.
     */
    private static int rootIndex(String s) {
        int i = 0;
        /* Loop invariant: s[..i) = ' ' */
        while (i < s.length() && s.charAt(i) == ' ') {
            i++;
        }
        return i;
    }

    /**
     * Return the infix form of the Binary Operation.The infixString() of a BinaryOperation is
     * always parenthesized and consists of the infix String representation of its left operand,
     * followed by its symbol, followed by its infix String representation of its right operand.
     * There is a single space character on each side of the symbol.
     */
    @Override
    public String infixString() {
        return "(" + left.infixString() + " " + symbol + " " + right.infixString() + ")";
    }

    /**
     * Returns the value of the expression if it contains no variables
     * Throws UnassignedVariable error if the expression contains a variable.
     */
    @Override
    public int evaluate() throws UnassignedVariable {
        return op.apply(left.evaluate(),right.evaluate());
    }

    /**
     * Takes in a char argument variable and an Expression argument expr.
     * Returns a new binary operation with all instances of variable replaced with expr.
     */
    @Override
    public Expression substitute(char variable, Expression expr) {
        Expression newLeft = left.substitute(variable,expr);
        Expression newRight = right.substitute(variable,expr);
        return new BinaryOperation(newLeft,newRight,symbol,op);
    }

    /**
     * Returns a simplified version of the binary operation. If both children are solely made
     * up of constants, their value is returned. If there is a variable, it returns a new binary
     * operation with the same symbol, op, and simplify() called on each of the children.
     */
    @Override
    public Expression simplify() {
        Expression simplifiedLeft = left.simplify();
        Expression simplifiedRight = right.simplify();
        try {
            return new Constant(op.apply(simplifiedLeft.evaluate(),simplifiedRight.evaluate()));
        } catch (UnassignedVariable e) {
            return new BinaryOperation(simplifiedLeft,simplifiedRight,symbol,op);
        }
    }

    /**
     * Returns a mathematically equivalent expression where any addition or subtraction operations nested inside
     * a multiplication operation get expanded so that all the direct children of all multiplication operations
     * are constants
     * REQUIRES: no part of the input expression can have a UnaryOperation
     */
    @Override
    public Expression expand() {
        BiFunction<Integer, Integer, Integer> ADDITION = (x, y) -> x + y;
        BiFunction<Integer, Integer, Integer> SUBTRACTION = (x, y) -> x - y;
        if (symbol == '*') {
            Expression leftChild = left.expand();
            Expression rightChild = right.expand();
            if (leftChild instanceof BinaryOperation) {
                BinaryOperation binLeftChild = (BinaryOperation) leftChild;
                if (binLeftChild.symbol() == '+' || binLeftChild.symbol() == '-') {
                    Expression newLeft = new BinaryOperation(binLeftChild.left(), rightChild, '*', op);
                    Expression newRight = new BinaryOperation(binLeftChild.right(), rightChild, '*', op);
                    Expression finalLeft = newLeft.expand();
                    Expression finalRight = newRight.expand();
                    // covers addition and subtraction
                    return new BinaryOperation(finalLeft, finalRight, binLeftChild.symbol(),binLeftChild.op());
                }
            }
            if (rightChild instanceof BinaryOperation ) {
                BinaryOperation binRightChild = (BinaryOperation) rightChild;
                if (binRightChild.symbol() == '+' || binRightChild.symbol() == '-') {
                    Expression newLeft = new BinaryOperation(leftChild, binRightChild.left(), '*', op);
                    Expression newRight = new BinaryOperation(leftChild, binRightChild.right(), '*', op);
                    Expression finalLeft = newLeft.expand();
                    Expression finalRight = newRight.expand();
                    return new BinaryOperation(finalLeft, finalRight, binRightChild.symbol(), binRightChild.op());
                }
            }


            // when the symbol == '*' and the children are constants/variables
            return new BinaryOperation(leftChild,rightChild,symbol,op);
        }
        else if (symbol == '+') {
            Expression newLeft = left.expand();
            Expression newRight = right.expand();
            return new BinaryOperation(newLeft,newRight,symbol,op);
        }
        // symbol == '-'
        else {
            Expression newLeft = left.expand();
            Expression newRight = right.expand();
            return new BinaryOperation(newLeft,newRight,symbol,op);
        }
    }

    @Override
    public String[] treeStringLinesRecursive() {
        String[] leftLines = left.treeStringLinesRecursive();   // left subtree String
        String[] rightLines = right.treeStringLinesRecursive(); // right subtree String
        int rows = 2 + Math.max(leftLines.length, rightLines.length);
        StringBuilder[] sb = new StringBuilder[rows]; // we'll use StringBuilder to make repeated concatenations more performant
        for (int i = 0; i < rows; i++) {
            sb[i] = new StringBuilder(); // all rows start as empty strings
        }

        sb[1].append(" ".repeat(rootIndex(leftLines[0]))); // padding for left half of left subtree
        sb[1].append("┌"); // branch down to left subtree
        sb[1].append(
                "─".repeat(leftLines[0].length() - rootIndex(leftLines[0]) - 1)); // horizontal bar
        sb[1].append("┴"); // branch up to root, both subtrees
        sb[0].append(" ".repeat(sb[1].length() - 1)); // left padding
        sb[0].append(symbol);
        for (int i = 0; i < leftLines.length; i++) {
            sb[i + 2].append(leftLines[i]); // left subtree's rows
            sb[i + 2].append(" "); // right padding
        }
        for (int j = leftLines.length + 2; j < rows; j++) {
            sb[j].append(" ".repeat(sb[0].length()));
        }

        sb[1].append("─".repeat(rootIndex(rightLines[0]))); // horizontal bar
        sb[1].append("┐"); // branch down to right subtree
        sb[1].append(
                " ".repeat(rightLines[0].length() - rootIndex(rightLines[0]) - 1)); // right padding
        sb[0].append(" ".repeat(sb[1].length() - sb[0].length())); // right padding
        for (int i = 0; i < rightLines.length; i++) {
            sb[i + 2].append(rightLines[i]); // right subtree's rows
        }
        for (int j = rightLines.length + 2; j < rows; j++) {
            sb[j].append(" ".repeat(rightLines[0].length()));
        }

        // convert StringBuilder[] array to String[] array
        String[] lines = new String[rows];
        int length = sb[0].toString().length();
        for (int i = 0; i < rows; i++) {
            lines[i] = sb[i].toString();
            assert length == lines[i].length();
        }
        return lines;
    }
}