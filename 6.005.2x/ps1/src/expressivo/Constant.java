package expressivo;

import java.util.Map;

/**
 * Immutable
 */
public class Constant implements Expression {

    // rep
    private final double value;

    // Abstraction
    //      Represents a positive real number
    // Rep inv
    //      value >= 0
    // Safety from rep exposure
    //      value is private final  and immutable field

    /**
     * immutable
     * @param value is required to be non-negative inside range of double
     */
    public Constant(double value) {
        if (value > Double.MAX_VALUE) {
            throw new IllegalArgumentException("Value was too large");
        }
        this.value = value;
        checkRep();
    }

    private void checkRep(){
        assert value >= 0;
    }


    public double getValue() {
        return (Double) value;
    }

    /**
     * Take the derivative of a constant, which is always zero
     * @param x the variable with respect to which we take the derivative
     * @return the derivative of the expression, is always zero
     */
    public Expression differentiate(Variable x) {
        return new Constant(0);
    }

    @Override
    public Expression differentiate(String stringVariable) {
        return differentiate(new Variable(stringVariable));
    }

    /**
     * A constant cannot be simplified any further
     * @return the constant itself
     */
    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean isProduct() {
        return false;
    }

    /**
     * A constant evaluated in any environment always stays the same
     * @param environment
     * @return this constant
     */
    @Override
    public Expression simplify(Map<String, Double> environment) {
        return this;
    }

    /**
     * Return the string containing the same value as the constant
     * @return a string only containing a number, either integer if the constant equals itself as integer, or a double
     *          if the input missed some zeroes, like .5, these are added to give 0.5, but 5. becomes the int 5
     */
    @Override
    public String toString() {
        if (value - (int) value == 0) return Integer.toString((int) value);
        return Double.toString(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Constant)) return false;
        return ((Double) value).equals(((Constant) obj).getValue());
    }

    @Override
    public int hashCode() {
        long valLongBits = Double.doubleToLongBits(value);
        int hashValue =(int)(valLongBits ^ (valLongBits >>> 32));
        return 37 + hashValue;
    }
}
