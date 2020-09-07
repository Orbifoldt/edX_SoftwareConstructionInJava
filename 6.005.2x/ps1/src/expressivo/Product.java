package expressivo;

import java.util.Map;

public class Product implements Expression {

    // Rep: A product is represented by its lhs and rhs expressions
    private final Expression left;
    private final Expression right;

    // Abstraction
    //      Represents the product of two expressions, namely left * right
    // Rep invariant
    //      none, since this is an non-terminal
    // Safety from rep exposure
    //      the fields are final and private

    /**
     * Constructor for the product of two expressions
     * @param left the left-hand side of the product
     * @param right the right-hand side of the product
     */
    public Product(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    /**
     * @return the left part of this
     */
    public Expression getLeft(){
        return left;
    }

    /**
     * @return the right part of this
     */
    public Expression getRight(){
        return right;
    }

    /**
     * Take the derivative of a product, this implements the Leibniz rule, i.e. d(u*v)/dx = du/dx * v + u * dv/dx
     * @param x the variable with respect to which we take the derivative
     * @return the derivative of the expression
     */
    public Expression differentiate(Variable x) {
        return (new Sum(new Product(left.differentiate(x.getVariable()), right),
                        new Product(left, right.differentiate(x.getVariable())))).simplify();
    }

    @Override
    public Expression differentiate(String stringVariable) {
        return differentiate(new Variable(stringVariable));
    }

    /**
     * Given a product of two expressions this method simplifies it, i.e. products with the number 0 are put to zero
     * and a product with the number 1 removes the * 1 or 1 * from the expression
     * Furthermore this orders the product such that all constants appear on the left of the variables.
     * It also simplifies the product of constants to a single constant
     * @return a simplified expression
     */
    @Override
    public Expression simplify() {
        final Expression zero = new Constant(0.0);
        final Expression one = new Constant(1.0);
        Expression left = this.left.simplify();
        Expression right = this.right.simplify();

        // 0 * x = x * 0 = 0
        if (left.equals(zero) || right.equals(zero)) return new Constant(0.0);

        Expression simplified;
        // 1 * x = x
        if (left.equals(one)) simplified = right;
        // x * 1 = 1
        else if (right.equals(one)) simplified = left;
        // the product of two constants is a single constant
        else if (right.isConstant() && left.isConstant()) {
            return new Constant(((Constant) right).getValue() * ((Constant) left).getValue());
        }
        // order a product of an expression and number such that the number is on the left of the expression
        // by the previous else if we know that the left term is not a constant (else this would give infinite loop)
        else if (right.isConstant()) simplified = (new Product(right, left)).simplify();
        // if none of the above cases apply return the product in which both left and right are simplified
        else return new Product(left, right);

        return simplified.simplify();
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean isProduct() {
        return true;
    }

    /**
     * Evaluate both terms of the product in the points specified by the environment and the simplify the expression
     * Given a product of two expressions this method simplifies it, i.e. products with the number 0 are put to zero
     * and a product with the number 1 removes the * 1 or 1 * from the expression
     * Furthermore this orders the product such that all constants appear on the left of the variables.
     * It also simplifies the product of constants to a single constant
     * @param environment contains variables as keys and constants as values, all the variables are substituted for
     *                    the corresponding constants. All keys are required to be non-negative
     * @return a simplified product of both the left and right term evaluated according to the environment
     */
    @Override
    public Expression simplify(Map<String, Double> environment) {
        Expression left = this.left.simplify(environment);
        Expression right = this.right.simplify(environment);
        return (new Product(left, right)).simplify();
    }

    @Override
    public String toString() {
        return left.toString() + "*" + right.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Product)) return false;
        return (left.equals(((Product) obj).getLeft()) && right.equals(((Product) obj).getRight()));
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 37 * result + left.hashCode();
        return 37 * result + right.hashCode();
    }
}
