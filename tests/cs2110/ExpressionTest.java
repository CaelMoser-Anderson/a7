package cs2110;

import cs2110.ast.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cs2110.ExpressionParser.*;
import static cs2110.ExpressionParser.NEGATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Contains tests for the `evaluate()`, `substitute()`, and `simplify()` methods
 * for all the `Expression` subclasses.
 */
public class ExpressionTest {

    /**
     * Helper method to return a BinaryOperation representing the addition of the given `left` and
     * `right` operands.
     */
    public Expression addExpr(Expression left, Expression right) {
        return new BinaryOperation(left, right, '+', ADDITION);
    }

    /**
     * Helper method to return a BinaryOperation representing the subtraction of the given `left`
     * and `right` operands.
     */
    public Expression subExpr(Expression left, Expression right) {
        return new BinaryOperation(left, right, '-', SUBTRACTION);
    }

    /**
     * Helper method to return a BinaryOperation representing the multiplication of the given `left`
     * and `right` operands.
     */
    public Expression multExpr(Expression left, Expression right) {
        return new BinaryOperation(left, right, '*', MULTIPLICATION);
    }

    /**
     * Helper method to return a UnaryOperation representing the negation of the operand.
     */
    public Expression negExpr(Expression center) {
        return new UnaryOperation(center, '-', NEGATION);
    }

    /*
     * TODO 4.1E-4.3E: Add unit tests to cover the `evaluate()`, `substitute()`, and `simplify()`
     *  definitions in the `Constant`, `Variable`, `BinaryOperation`, and `UnaryOperation`
     *  classes.
     */

    @DisplayName("Integer Base case")
    @Test
    void intExpression() {
        Constant testConstant = new Constant(3);
        assertEquals(3,testConstant.evaluate());
    }

    @DisplayName("Variable Base case")
    @Test
    void varExpression() throws MalformedExpression{
        Expression test = ExpressionParser.parse("x + 1");
        assertThrows(UnassignedVariable.class, () -> test.evaluate());
    }

    @DisplayName("Multiplication case")
    @Test
    void multExpression() throws MalformedExpression, UnassignedVariable {
        Expression expected = ExpressionParser.parse("3*2");
        assertEquals(6,expected.evaluate());
    }

    @DisplayName("Addition case")
    @Test
    void addExpression() throws MalformedExpression, UnassignedVariable {
        Expression expected = ExpressionParser.parse("3+2");
        assertEquals(5,expected.evaluate());
    }

    @DisplayName("Unary Operation")
    @Test
    void UnaryExpression() throws MalformedExpression, UnassignedVariable{
        Expression expected = ExpressionParser.parse("-3");
        assertEquals(-3,expected.evaluate());
    }

    @DisplayName("Complex Integer Expression")
    @Test
    void compIntExpression() throws MalformedExpression, UnassignedVariable{
        Expression expected = ExpressionParser.parse("3*2+(4-2)*3-8");
        assertEquals(4,expected.evaluate());
    }

    @DisplayName("One Variable Expression")
    @Test
    void oneVarExpression() throws MalformedExpression, UnassignedVariable{
        Expression expected = ExpressionParser.parse("3*x+(4-2)*3-8");
        assertThrows(UnassignedVariable.class, () -> expected.evaluate());
    }

    @DisplayName("Multi Variable Expression")
    @Test
    void multiVarExpression() throws MalformedExpression, UnassignedVariable{
        Expression expected = ExpressionParser.parse("y*x+(4-2)*z-8");
        assertThrows(UnassignedVariable.class, () -> expected.evaluate());
    }

    // Substitute Tests
    // test multiple variables. Test variable doesn't exist in og expression.
    // Test replacing with another variable.

    @DisplayName("Int Substitute Test")
    @Test
    void simpleSubstitute() throws MalformedExpression, UnassignedVariable{
        Expression testExpression = ExpressionParser.parse("x");
        Expression replacer = ExpressionParser.parse("3");
        assertEquals(replacer,testExpression.substitute('x',replacer));
    }

    @DisplayName("Substitute Int within an Expression")
    @Test
    void intExpressionSubstitute() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("2+x+3");
        Expression replacer = ExpressionParser.parse("9");
        Expression Actual = ExpressionParser.parse("2+9+3");
        assertEquals(Actual,testExpression.substitute('x',replacer));
    }

    @DisplayName("Substitute Int within Unary Operation")
    @Test
    void unarySubstitute() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("-x");
        Expression replacer = ExpressionParser.parse("9");
        Expression Actual = ExpressionParser.parse("-9");
        assertEquals(Actual,testExpression.substitute('x',replacer));
    }

    @DisplayName("Substitute variable within an Expression")
    @Test
    void varExpressionSubstitute() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("2+x+3");
        Expression replacer = ExpressionParser.parse("y");
        Expression Actual = ExpressionParser.parse("2+y+3");
        assertEquals(Actual,testExpression.substitute('x',replacer));
    }

    @DisplayName("Substituting Multiple Instances of a Variable")
    @Test
    void multiVarSubstitute() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("2+x+3*x");
        Expression replacer = ExpressionParser.parse("7");
        Expression Actual = ExpressionParser.parse("2+7+3*7");
        assertEquals(Actual,testExpression.substitute('x',replacer));
    }

    @DisplayName("Substituting a Variable That Doesn't Exist")
    @Test
    void newVarSubstitute() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("2+x+3");
        Expression replacer = ExpressionParser.parse("7");
        Expression Actual = ExpressionParser.parse("2+x+3");
        assertEquals(Actual,testExpression.substitute('y',replacer));
    }

    // Simplify Tests
    // uhh check if it works bro?

    @DisplayName("Simplify constant")
    @Test
    void constSimplify() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("2");
        Expression Actual = ExpressionParser.parse("2");
        assertEquals(Actual,testExpression.simplify());
    }

    @DisplayName("Simplify Addition Expression")
    @Test
    void addSimplify() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("2+3");
        Expression Actual = ExpressionParser.parse("5");
        assertEquals(Actual,testExpression.simplify());
    }

    @DisplayName("Simplify Multiplicaiton Expression")
    @Test
    void multSimplify() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("2*3");
        Expression Actual = ExpressionParser.parse("6");
        assertEquals(Actual,testExpression.simplify());
    }

    @DisplayName("Simplify Unary Operation")
    @Test
    void UnarySimplify() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("-2");
        Expression Actual = ExpressionParser.parse("-2");
        assertEquals(Actual,testExpression.simplify());
    }

    @DisplayName("Complex Int Expression Simplification")
    @Test
    void compIntSimplify() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("3*(-2+4)+7*5");
        Expression Actual = ExpressionParser.parse("41");
        assertEquals(Actual,testExpression.simplify());
    }

    @DisplayName("Simplify Expression with Variables ")
    @Test
    void compVarSimplify() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("3*(-2+x)+7*x");
        Expression Actual = ExpressionParser.parse("3*(-2+x)+7*x");
        assertEquals(Actual,testExpression.simplify());
    }

    @DisplayName("Nothing to simplify")
    @Test
    void noSimplify() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("2+x+3");
        Expression Actual = ExpressionParser.parse("2+x+3");
        assertEquals(Actual,testExpression.simplify());
    }
}
