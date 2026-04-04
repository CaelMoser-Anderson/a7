package cs2110.ast;

/**
 * An expression representing a fixed int value.
 */
public record Constant(int value) implements Expression {


    /**
     * Return the infix form of the constant, which is value converted to a String.
     */
    @Override
    public String infixString() {
        // TODO 2.3A: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        return String.valueOf(value);
    }

    /**
     * Returns the value of the constant.
     */
    @Override
    public int evaluate() {
        // TODO 4.1A: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        return value;
    }

    /**
     * Takes in a char argument variable and an Expression argument expr.
     * Returns expr.
     */
    @Override
    public Expression substitute(char variable, Expression expr) {
        // TODO 4.2A: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        return new Constant(value);
    }

    /**
     * Returns an identical constant with the same value as this.
     */
    @Override
    public Expression simplify() {
        // TODO 4.3A: Complete the definition of this method. Add a Javadoc comment to this method
        //  that refines its specifications.
        return new Constant(value);
    }

    /**
     * Returns itself; a constant cannot be expanded.
     */
    @Override
    public Expression expand() {
        return this;
    }

    @Override
    public String[] treeStringLinesRecursive() {
        return new String[]{Integer.toString(value)};
    }
}


