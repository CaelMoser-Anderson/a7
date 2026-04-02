package cs2110;

import static cs2110.ExpressionParser.ADDITION;
import static cs2110.ExpressionParser.MULTIPLICATION;
import static org.junit.jupiter.api.Assertions.assertEquals;

import cs2110.ast.BinaryOperation;
import cs2110.ast.Constant;
import cs2110.ast.Expression;
import cs2110.ast.Variable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Contains tests for the `ExpressionParser.parse()` method.
 */
public class ParserTest {

    /**
     * Helper method to return a BinaryOperation representing the addition of the given `left` and
     * `right` operands.
     */
    public Expression addExpr(Expression left, Expression right) {
        return new BinaryOperation(left, right, '+', ADDITION);
    }

    /**
     * Helper method to return a BinaryOperation representing the multiplication of the given `left`
     * and `right` operands.
     */
    public Expression multExpr(Expression left, Expression right) {
        return new BinaryOperation(left, right, '*', MULTIPLICATION);
    }

    @DisplayName("WHEN an expression consists of a just a single-digit, THEN it is parsed "
            + "correctly to a Constant expression with the correct value.")
    @Test
    void testDigit() throws MalformedExpression {
        Expression expected = new Constant(0);
        Expression actual = ExpressionParser.parse("0");
        assertEquals(expected, actual);

        expected = new Constant(1);
        actual = ExpressionParser.parse("1");
        assertEquals(expected, actual);

        expected = new Constant(5);
        actual = ExpressionParser.parse("5");
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN we parse an expression containing one addition operation applied to two "
            + "single-digit operands, THEN a BinaryOperation expression is returned that contains "
            + "the correct symbol, operation BiFunction, and operands (in the correct order).")
    @Test
    void testAddConstants() throws MalformedExpression {
        Expression expected = addExpr(new Constant(2), new Constant(3));
        Expression actual = ExpressionParser.parse("2+3");
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN we parse an expression containing one multiplication operation applied to "
            + "two single-digit operands, THEN a BinaryOperation expression is returned that "
            + "contains the correct symbol, operation BiFunction, and operands (in the correct "
            + "order).")
    @Test
    void testMultiplyConstants() throws MalformedExpression {
        Expression expected = multExpr(new Constant(2), new Constant(3));
        Expression actual = ExpressionParser.parse("2*3");
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN we parse an expression containing addition followed by multiplication, THEN "
            + "the multiplication sub-expression appears as the right operand of the addition "
            + "operation.")
    @Test
    void testMultiplicationAfterAddition() throws MalformedExpression {
        Expression expected = addExpr(new Constant(1), multExpr(new Constant(2), new Constant(3)));
        Expression actual = ExpressionParser.parse("1+2*3");
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN we parse the expression \"2*(3+4)\", THEN the addition BinaryOperation "
            + "appears the right operand of the multiplication BinaryOperation.")
    @Test
    void testAddParensAfterMult() throws MalformedExpression {
        Expression expected = multExpr(new Constant(2), addExpr(new Constant(3), new Constant(4)));
        Expression actual = ExpressionParser.parse("2*(3+4)");
        assertEquals(expected, actual);
    }

    /*
     * TODO 3.2B-3.7B: Add additional tests to this method to improve coverage of the starter
     *  version features (correct handling of parentheses, addition, and multiplication)
     *  and provide full coverage of the features that you added (exception handling,
     *  variables, multi-digit constants, whitespace handling, subtraction, and negation).
     */
    @DisplayName("WHEN we parse the expression \"252*(7+41)\", THEN the addition BinaryOperation "
            + "appears the right operand of the multiplication BinaryOperation.")
    @Test
    void testMultipleDigits() throws MalformedExpression {
        Expression expected = multExpr(new Constant(252),
                addExpr(new Constant(7), new Constant(41)));
        Expression actual = ExpressionParser.parse("252*(7+41)");
        assertEquals(expected, actual);
    }

    @DisplayName("WHEN we parse the expression \"a*(b+21)\", THEN the addition BinaryOperation "
            + "appears the right operand of the multiplication BinaryOperation.")
    @Test
    void testVariableUsage() throws MalformedExpression {
        Expression expected = multExpr(new Variable('a'),
                addExpr(new Variable('b'), new Constant(21)));
        Expression actual = ExpressionParser.parse("a*(b+21)");
        assertEquals(expected, actual);
    }

}
