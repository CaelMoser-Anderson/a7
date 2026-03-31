package cs2110;

import cs2110.ast.Constant;
import cs2110.ast.Expression;
import cs2110.ast.BinaryOperation;
import cs2110.lib.LinkedStack;
import cs2110.lib.Stack;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Provides a `parse()` utility method to convert expression Strings into ASTs of type `Expression`.
 */
public class ExpressionParser {

    /** Models the addition operation on integers. */
    public static BiFunction<Integer, Integer, Integer> ADDITION = (x,y) -> x + y;

    /** Models the subtraction operation on integers. */
    public static BiFunction<Integer, Integer, Integer> SUBTRACTION = (x,y) -> x - y;

    /** Models the multiplication operation on integers. */
    public static BiFunction<Integer, Integer, Integer> MULTIPLICATION = (x,y) -> x * y;

    /** Models the negation operation on integers. */
    public static Function<Integer, Integer> NEGATION = x -> -1 * x;

    /**
     * Constructs and returns an AST corresponding to the given `expr`ession String.
     * Throws `MalformedExpression` to indicate that the expression String was not well-formed.
     * A well-formed expression String will contain only digits, lowercase Latin characters,
     * whitespace characters, and the symbols '(', ')', '+', '-', and '*' in an order that gives
     * a valid mathematical expression.
     */
    public static Expression parse(String expr) throws MalformedExpression {
        // TODO 3.2A-3.7A: Update the definition of this method to support all the required
        //  features. After this, the method should conform to its specifications.

        Stack<Expression> operands = new LinkedStack<>();
        Stack<Character> operators = new LinkedStack<>(); // invariant: contains only '(', '+', and '*'
        boolean expectingOperator = false; // in infix notation, the first operand comes before an operator

        for (char c : expr.toCharArray()) {
            if (c == '(') { // when we see '(', we simply push onto operators stack
                assert !expectingOperator;
                operators.push('(');
            } else if (c == '*') {
                assert expectingOperator;
                // process earlier multiplications because of left associativity
                while (!operators.isEmpty() && operators.peek() == '*') {
                    oneStepSimplify(operands, operators);
                }
                operators.push('*');
                expectingOperator = false;
            } else if (c == '+') {
                assert expectingOperator;
                // process earlier multiplications because of higher precedence and
                // earlier additions because of left associativity
                while (!operators.isEmpty() && (operators.peek() == '*'
                        || operators.peek() == '+')) {
                    oneStepSimplify(operands, operators);
                }
                operators.push('+');
                expectingOperator = false;
            } else if (c == ')') {
                assert expectingOperator;
                assert !operators.isEmpty();
                // process operators until we find the matching '(' on the operators stack
                while (operators.peek() != '(') {
                    oneStepSimplify(operands, operators);
                    assert !operators.isEmpty();
                }
                operators.pop(); // remove '('
            } else { // c is a digit
                assert c >= '0' && c <= '9';
                assert !expectingOperator;
                operands.push(new Constant(c - '0'));
                expectingOperator = true;
            }
        }

        assert expectingOperator; // infix expressions end with an operand
        // finish simplifying until we have a single operand and no operators
        while (!operators.isEmpty()) {
            assert operators.peek() != '(';
            oneStepSimplify(operands, operators);
        }

        // If the above assertions pass, the operands stack should include exactly one value,
        // the return value. We'll include two assertions to verify this as a sanity check.
        assert !operands.isEmpty();
        Expression result = operands.pop();
        assert operands.isEmpty();
        return result;
    }

    /**
     * Helper method that partially simplifies the expression by `pop()`ping one operator from the
     * `operators` stack, `pop()`ping two operand Expressions from the `operands` stack, and
     * `push()`ing a new BinaryOperation representing the application of this operator on these
     * operands onto the `operands` stack. Requires that `operators.peek()` is '+' or '*' and
     * `operands` includes at least two elements.
     */
    private static void oneStepSimplify(Stack<Expression> operands, Stack<Character> operators) {
        char c = operators.pop();
        assert c == '+' || c == '*';

        Expression o2 = operands.pop(); // right operand is higher on stack
        Expression o1 = operands.pop();
        operands.push(new BinaryOperation(o1, o2, c, c == '+' ? ADDITION : MULTIPLICATION));
    }

    /**
     * Repeatedly queries the user for an expression String and outputs a String representation of
     * the AST for that expression.
     */
    public static void main(String[] args) throws MalformedExpression {
        try (Scanner in = new Scanner(System.in)) {
            while (true) { // repeat indefinitely
                System.out.print("Enter an expression, or enter \"q\" to quit: ");
                String expr = in.nextLine();
                if (expr.equals("q")) {
                    break; // exit loop
                }
                // TODO 3.2C: Update the definition of this method to print out the appropriate
                //  error message when a `MalformedExpression` is detected.
                System.out.println("= " + System.lineSeparator() + parse(expr).treeString());
            }
        }
    }
}
