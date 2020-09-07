package expressivo;

import java.util.Map;

public class Sum implements Expression {

    // Rep: A sum is represented by its lhs and rhs expressions
    private final Expression left;
    private final Expression right;

    // Abstraction
    //      This represents the sum of two expressions, namely left + right
    // Rep invariant
    //      none, since this is an non-terminal
    // Safety from rep exposure
    //      The fields are all final, private

    /**
     * Constructor for the sum of two expressions
     * @param left the left-hand side of the sum
     * @param right the right-hand side of the sum
     */
    public Sum(Expression left, Expression right) {
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
     * The derivative of a sum of two expression is the sum of their derivatives
     * @param x the variable with respect to which we take the derivative
     * @return the derivative of the expression
     */
    public Expression differentiate(Variable x) {
        return (new Sum(left.differentiate(x.getVariable()), right.differentiate(x.getVariable()))).simplify();
    }

    @Override
    public Expression differentiate(String stringVariable) {
        return differentiate(new Variable(stringVariable));
    }

    /**
     * Given a sum of two expressions this method simplifies it, a sum with the number 0 removes the 0 +  or + 0.
     * It also simplifies the sum of constants to a single constant
     * Furthermore it takes out common factors from sums
     * @return a simplified expression
     */
    @Override
    public Expression simplify() {
        final Expression zero = new Constant(0);
        Expression left = this.left.simplify();
        Expression right = this.right.simplify();

        // sum of two constants is a single constant
        if (right.isConstant() && left.isConstant()) {
            return new Constant(((Constant) right).getValue() + ((Constant) left).getValue());
        }
        // 0 + x = x
        else if (left.equals(zero)) return right;
        // x + 0 = x
        else if (right.equals(zero)) return left;

        // take out common factors
        Expression reduced = new Sum(left,right);
        if (left.isProduct() && right.isProduct()) {
            Product leftProduct = (Product) left;
            Product rightProduct = (Product) right;
            // x * a + x * b = x * (a + b)
            if (leftProduct.getLeft().equals(rightProduct.getLeft())){
                reduced = new Product(leftProduct.getLeft(), (new Sum(leftProduct.getRight(),
                                                                      rightProduct.getRight())).simplify());
            }
            // x * a + b * x = x * (a + b)
            else if (leftProduct.getLeft().equals(rightProduct.getRight())){
                reduced = new Product(leftProduct.getLeft(), (new Sum(leftProduct.getRight(),
                                                                      rightProduct.getLeft())).simplify());
            }
            // a * x + x * b = x * (a + b)
            else if (leftProduct.getRight().equals(rightProduct.getLeft())){
                reduced = new Product(leftProduct.getRight(), (new Sum(leftProduct.getLeft(),
                                                                       rightProduct.getRight())).simplify());
            }
            // a * x + b * x = x * (a + b)
            else if (leftProduct.getRight().equals(rightProduct.getRight())){
                reduced = new Product(leftProduct.getRight(), (new Sum(leftProduct.getLeft(),
                                                                       rightProduct.getLeft())).simplify());
            }
            return reduced;
        }
        else if (left.isProduct()) {
            Product leftProduct = (Product) left;
            // x * a + x = x * (a + 1)
            if (leftProduct.getLeft().equals(right)) {
                reduced = new Product(leftProduct.getLeft(),(new Sum(leftProduct.getRight(),
                                                                     new Constant(1))).simplify());
            }
            // a * x + x = x * (a + 1)
            else if (leftProduct.getRight().equals(right)) {
                reduced = new Product(leftProduct.getRight(),(new Sum(leftProduct.getLeft(),
                                                                      new Constant(1))).simplify());
            }
            return reduced;
        }
        else if (right.isProduct()) {
            Product rightProduct = (Product) right;
            // x + x * a = x * (a + 1)
            if (rightProduct.getLeft().equals(left)) {
                reduced = new Product(rightProduct.getLeft(),(new Sum(rightProduct.getRight(),
                                                                      new Constant(1))).simplify());
            }
            // x + a * x = x * (a + 1)
            else if (rightProduct.getRight().equals(left)) {
                reduced = new Product(rightProduct.getRight(),(new Sum(rightProduct.getLeft(),
                                                                       new Constant(1))).simplify());
            }
            return reduced;
        }
        // x + x = 2 * x
        else if (right.equals(left)) {
            reduced = new Product(new Constant(2), left);
            return reduced.simplify();
        }
        // if none of the above cases apply return the sum in which both left and right are simplified
        return new Sum(left,right);

        // x+x*x+x gave stack overflow, x+y+x, x+y
        // x*(x+x*(y+x)*x+(y*x+z)*x+x) gave stack overflow,  x+x*(y+x)*x,  x+x*y*x
        // x*x*x*X*X*X*X*XxX*X*Xx*X*x*XX*X*XX*x, and then !d/dx gave stack overflow
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
        return false;
    }

    /**
     * Evaluate both terms of the sum in the points specified by the environment and then simplify the expression:
     * Given a sum of two expressions this method simplifies it, a sum with the number 0 removes the 0 +  or + 0.
     * It also simplifies the sum of constants to a single constant
     * Furthermore it takes out common factors from sums
     * @param environment contains variables as keys and constants as values, all the variables are substituted for
     *                    the corresponding constants. All keys are required to be non-negative
     * @return a simplified sum of both the left and right term evaluated according to the environment
     */
    @Override
    public Expression simplify(Map<String, Double> environment) {
        Expression left = this.left.simplify(environment);
        Expression right = this.right.simplify(environment);
        return (new Sum(left, right)).simplify();
    }

    @Override
    public String toString() {
        return "(" + left.toString() + "+" + right.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sum)) return false;
        return (left.equals(((Sum) obj).getLeft()) && right.equals(((Sum) obj).getRight()));
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 37 * result + left.hashCode();
        return 37 * result + right.hashCode();
    }
}
