package expressivo;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variable implements Expression {

    // rep
    private final String variable; // case-sensitive

    // Abstraction:
    //      a string that represents an algebraic variable
    // Rep invariant:
    //      the variable is always a non-empty sequence of letters
    // Safety from rep exposure
    //      the field is final and immutable

    /**
     * Create a variable from a string. Spaces in the string do matter!!
     * @param variable the name of the variable
     */
    public Variable(String variable) {
        this.variable = variable;
        checkRep();
    }

    private void checkRep(){
        assert variable.matches("[a-zA-Z]+");
    }

    public String getVariable() {
        return variable;
    }

    /**
     * The derivative of a variable wrt x is 1 when the variable equals x, and 0 otherwise
     * @param x the variable with respect to which we take the derivative
     * @return the derivative of the expression
     */
    public Expression differentiate(Variable x) {
        if (this.equals(x)) return new Constant(1);
        else return new Constant(0);
    }

    @Override
    public Expression differentiate(String stringVariable) {
        return differentiate(new Variable(stringVariable));
    }

    /**
     * A variable cannot be simplified any further
     * @return the variable itself
     */
    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean isProduct() {
        return false;
    }

    /**
     * Evaluate this variable in the point specified by the environment if this variable is contained in the
     * environment, else nothing changes
     * @param environment the keys represent variables and their values the constants, requires every value non-negative
     * @return a constant if this variable appeared in the environment or else this variable itself
     */
    @Override
    public Expression simplify(Map<String, Double> environment) {
        if (environment.containsKey(variable)) {
            assert environment.get(variable) > 0;
            return new Constant(environment.get(variable));
        } else return this;
    }

    @Override
    public String toString() {
        return variable;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Variable)) return false;
        return variable.equals(((Variable) obj).getVariable());
    }

    @Override
    public int hashCode() {
        return 37 + variable.hashCode();
    }
}
