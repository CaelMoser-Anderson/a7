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

    // Evaluate Tests

    @DisplayName("Evaluate Integer Base case")
    @Test
    void intEvaluate() {
        Constant testConstant = new Constant(3);
        assertEquals(3,testConstant.evaluate());
    }


    @DisplayName("Evaluate Variable Base case")
    @Test
    void varEvaluate() throws MalformedExpression{
        Expression test = ExpressionParser.parse("x + 1");
        assertThrows(UnassignedVariable.class, () -> test.evaluate());
    }


    @DisplayName("Evaluate Multiplication case")
    @Test
    void multEvaluate() throws MalformedExpression, UnassignedVariable {
        Expression expected = ExpressionParser.parse("3*2");
        assertEquals(6,expected.evaluate());
    }


    @DisplayName("Evaluate Addition case")
    @Test
    void addEvaluate() throws MalformedExpression, UnassignedVariable {
        Expression expected = ExpressionParser.parse("3+2");
        assertEquals(5,expected.evaluate());
    }


    @DisplayName("Evaluate Unary Operation")
    @Test
    void UnaryEvaluate() throws MalformedExpression, UnassignedVariable{
        Expression expected = ExpressionParser.parse("-3");
        assertEquals(-3,expected.evaluate());
    }


    @DisplayName("EvaluateComplex Integer Expression")
    @Test
    void compIntEvaluate() throws MalformedExpression, UnassignedVariable{
        Expression expected = ExpressionParser.parse("3*2+(4-2)*3-8");
        assertEquals(4,expected.evaluate());
    }


    @DisplayName("Evaluate One Variable Expression")
    @Test
    void oneVarEvaluate() throws MalformedExpression, UnassignedVariable{
        Expression expected = ExpressionParser.parse("3*x+(4-2)*3-8");
        assertThrows(UnassignedVariable.class, () -> expected.evaluate());
    }


    @DisplayName("Evaluate Multi Variable Expression")
    @Test
    void multiVarEvaluate() throws MalformedExpression, UnassignedVariable{
        Expression expected = ExpressionParser.parse("y*x+(4-2)*z-8");
        assertThrows(UnassignedVariable.class, () -> expected.evaluate());
    }


    // Substitute Tests


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


    @DisplayName("Substitute in an Unary Operation")
    @Test
    void unaryExpressionSubstitute() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("x");
        Expression replacer = ExpressionParser.parse("-9");
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


    @DisplayName("Substitute in an Expression")
    @Test
    void expressionSubstitute() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("(2+x)-3");
        Expression replacer = ExpressionParser.parse("3*2");
        Expression Actual = ExpressionParser.parse("(2+3*2)-3");
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


    @DisplayName("Simplify Multiplication Expression")
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


    @DisplayName("Simplify Constants within an Expression")
    @Test
    void contVarSimplify() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("(3*4)+x-2*3");
        Expression Actual = ExpressionParser.parse("12+x-6");
        assertEquals(Actual,testExpression.simplify());
    }


    @DisplayName("Simplify Expression with Variables")
    @Test
    void compVarSimplify() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("3*(-2+x)+7*x");
        Expression Actual = ExpressionParser.parse("3*(-2+x)+7*x");
        assertEquals(Actual,testExpression.simplify());
    }


    @DisplayName("Multiple Nested")
    @Test
    void multiNestedSimplify() throws MalformedExpression, UnassignedVariable {
        Expression testExpression = ExpressionParser.parse("(3*(3+(3+2)))");
        Expression Actual = ExpressionParser.parse("24");
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
