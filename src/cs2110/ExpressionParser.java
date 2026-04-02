package cs2110;

import cs2110.ast.Constant;
import cs2110.ast.Expression;
import cs2110.ast.BinaryOperation;
import cs2110.ast.UnaryOperation;
import cs2110.ast.Variable;
import cs2110.lib.LinkedStack;
import cs2110.lib.Stack;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Provides a `parse()` utility method to convert expression Strings into ASTs of type
 * `Expression`.
 */
public class ExpressionParser {

    /**
     * Models the addition operation on integers.
     */
    public static BiFunction<Integer, Integer, Integer> ADDITION = (x, y) -> x + y;

    /**
     * Models the subtraction operation on integers.
     */
    public static BiFunction<Integer, Integer, Integer> SUBTRACTION = (x, y) -> x - y;

    /**
     * Models the multiplication operation on integers.
     */
    public static BiFunction<Integer, Integer, Integer> MULTIPLICATION = (x, y) -> x * y;

    /**
     * Models the negation operation on integers.
     */
    public static Function<Integer, Integer> NEGATION = x -> -1 * x;

    /**
     * Constructs and returns an AST corresponding to the given `expr`ession String. Throws
     * `MalformedExpression` to indicate that the expression String was not well-formed. A
     * well-formed expression String will contain only digits, lowercase Latin characters,
     * whitespace characters, and the symbols '(', ')', '+', '-', and '*' in an order that gives a
     * valid mathematical expression.
     */
    public static Expression parse(String expr) throws MalformedExpression {
        // TODO 3.2A-3.7A: Update the definition of this method to support all the required
        //  features. After this, the method should conform to its specifications.

        Stack<Expression> operands = new LinkedStack<>();
        Stack<Character> operators = new LinkedStack<>(); // invariant: contains only '(', '+', and '*'
        boolean expectingOperator = false; // in infix notation, the first operand comes before an operator
        String digits = "";
        char previous = '!';

        for (char c : expr.toCharArray()) {
            if (!digits.isEmpty()) {
                if (Character.isDigit(c)) {
                    digits = digits + c;
                } else {
                    operands.push(new Constant(
                            Integer.parseInt(digits))); //Need to return digits as an int!!!
                    expectingOperator = true;
                    digits = "";
                }
            }
            if (c == '(') { // when we see '(', we simply push onto operators stack
                if (expectingOperator) {
                    throw new MalformedExpression("We expected an operator and got '(' instead.");
                }
                operators.push('(');
            } else if (c == '*') {
                if (!expectingOperator) {
                    throw new MalformedExpression("We expected an operand and got '*' instead.");
                }
                // process earlier multiplications because of left associativity
                while (!operators.isEmpty() && operators.peek() == '*') {
                    oneStepSimplify(operands, operators);
                }
                operators.push('*');
                expectingOperator = false;
            } else if (c == '+') {
                if (!expectingOperator) {
                    throw new MalformedExpression("We expected an operand and got '+' instead.");
                }
                // process earlier multiplications because of higher precedence and
                // earlier additions because of left associativity
                while (!operators.isEmpty() && (operators.peek() == '*'
                        || operators.peek() == '+' || operators.peek() == '-')) {
                    oneStepSimplify(operands, operators);
                }
                operators.push('+');
                expectingOperator = false;
            } else if (c == '-') {
                if (previous == '!' || previous == '(') {
                    Expression o2 = operands.pop(); // right operand is higher on stack
                    Function<Integer, Integer> operation = NEGATION;
                } else {
                    if (!expectingOperator) {
                        throw new MalformedExpression(
                                "We expected an operand and got '-' instead.");
                    }
                    // process earlier multiplications because of higher precedence and
                    // earlier additions because of left associativity
                    while (!operators.isEmpty() && (operators.peek() == '*'
                            || operators.peek() == '+' || operators.peek() == '-')) {
                        oneStepSimplify(operands, operators);
                    }
                    operators.push('-');
                    expectingOperator = false;
                }
            } else if (c == ')') {
                if (!expectingOperator) {
                    throw new MalformedExpression("We expected an operand and got ')' instead.");
                }
                if (operators.isEmpty()) {
                    throw new MalformedExpression("We expect more operators, but there are none.");
                }
                // process operators until we find the matching '(' on the operators stack
                while (operators.peek() != '(') {
                    oneStepSimplify(operands, operators);
                    //assert !operators.isEmpty();
                    if (operators.isEmpty()) {
                        throw new MalformedExpression(
                                "We expect more operators before '(', but there are none.");
                    }
                }
                operators.pop(); // remove '('
            } else if (isVariable(c)) {
                operands.push(new Variable(c));
                expectingOperator = true;
            } else if (Character.isDigit(c) && digits.isEmpty()) { // c is a digit
                if (c < '0' || c > '9') {
                    throw new MalformedExpression(
                            "We expected a digit between 0 and 9.");
                }
                if (expectingOperator) {
                    throw new MalformedExpression(
                            "We expect an operand and did not get one.");
                }
                digits = digits + c;
                //operands.push(new Constant(c - '0'));
                //expectingOperator = true;
            } else {
                if (c != ' ') {
                    if (digits.isEmpty()) {
                        throw new MalformedExpression(
                                "Invalid Character"); //In theory, by the time we're here we've checked
                        // everything
                    }
                }
            }
            previous = c;
        }
        if (!digits.isEmpty()) {
            operands.push(new Constant(Integer.parseInt(digits)));
            expectingOperator = true;
        }
        // infix expressions end with an operand
        if (!expectingOperator) {
            throw new MalformedExpression(
                    "After the loop, we expect to have expectingOperator set to true");
        }
        // finish simplifying until we have a single operand and no operators
        while (!operators.isEmpty()) {
            if (operators.peek() == '(') {
                throw new MalformedExpression("We did not expect to find a '(' in operators after "
                        + "our main loop");
            }
            oneStepSimplify(operands, operators);
        }

        // If the above assertions pass, the operands stack should include exactly one value,
        // the return value. We'll include two assertions to verify this as a sanity check.
        //assert !operands.isEmpty();
        if (operands.isEmpty()) {
            throw new MalformedExpression(
                    "After the loop, we expect operands to be non Empty, but it is Empty");
        }
        Expression result = operands.pop();
        if (!operands.isEmpty()) {
            throw new MalformedExpression(
                    "After popping our result, operands should be empty and it is not.");
        }
        return result;
    }

    /**
     * Returns true if c is a variable (meaning 'a' through 'z'). Must be lowercase. Otherwise,
     * returns false.
     */
    private static boolean isVariable(char c) {
        return c >= 'a' && c <= 'z';
    }


    /**
     * Helper method that partially simplifies the expression by `pop()`ping one operator from the
     * `operators` stack, `pop()`ping two operand Expressions from the `operands` stack, and
     * `push()`ing a new BinaryOperation representing the application of this operator on these
     * operands onto the `operands` stack. Requires that `operators.peek()` is '+' or '*' and
     * `operands` includes at least two elements.
     */
    private static void oneStepSimplify(Stack<Expression> operands, Stack<Character> operators)
            throws MalformedExpression {
        char c = operators.pop();
        //assert c == '+' || c == '*';
        if (!(c == '+' || c == '*' || c == '-')) {
            throw new MalformedExpression(
                    "The operator stack does not have a proper binary operator");
        }
        Expression o2 = operands.pop(); // right operand is higher on stack
        Expression o1 = operands.pop();
        BiFunction<Integer, Integer, Integer> operation = ADDITION;
        if (c == '*') {
            operation = MULTIPLICATION;
        } else if (c == '-') {
            operation = SUBTRACTION;
        }
        operands.push(new BinaryOperation(o1, o2, c, operation));
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
                try {
                    System.out.println("= " + System.lineSeparator() + parse(expr).treeString());
                } catch (MalformedExpression e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }
}
