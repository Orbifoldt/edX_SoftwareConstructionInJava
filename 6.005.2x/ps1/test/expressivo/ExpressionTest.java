/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;

import lib6005.parser.UnableToParseException;
import org.junit.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for the Expression abstract data type.
 */

    

public class ExpressionTest {

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }


    // Testing Strategy:
    /**
     * Partitions: (these hold for all the methods, but test-cases differ per method)
     * - valid, invalid input (e.g. invalid operations, unbalanced parentheses, missing operations)
     * - 0, 1 or >1 multiplications and/or additions
     * - 0, 1 or >1 variables (length 1, >1) (different capitalizations)
     * - 0, 1 or >1 constants (integer or double)
     * - nested parentheses
     * - begin, middle, end or no whitespaces
     * - Sum and product arguments switched (might not be commutative)
     *
     *
     * Plan for testing:
     * Test the parser, (we first test this since it's the main way of creating expressions, but some of the tests below
     *                  were created before the parser method, as was instructed in the problem set)
     * Test equality and hashcode (also test that equality is an equiv relation)
     * Test toString
     * Test differentiate (Leibniz rule, sum rule, etc.)
     * Test simplify (includes some Stack overflow bugs found while programming, also combined with differentiate)
     */



    /** Tests for the expression parser:
     * - Valid input expressions (also includes the weakened pre-condition for constants)
     * <p>
     * Test cases:
     * - Constant input and addition: 2, 2 + 3, 2.1, 2.1+3.1
     * - Weakened pre-condition allows for: .1, 2.
     * - Variable input and product: x, x * y, var * Var
     * - Constant products and variable products and combinations: 2*3, 2*3.1, 2.1*3, 2.1*3.1, 2.0 * x, x * 2.0
     * - Concatenation of operations and brackets: 2+3*4, (2+3)*4, (1+1+1+1+1)*x, 5+ ( ( ( x ) ) )
     * - Whitespace input  x   +   3   *   y
     * - Invalid input: (3, 3*, 3x, (3+, 3/5 (there's no divide operation yet implemented)
     */

    // Junit automatically fails the test whenever an exception is thrown
    @Test
    public void testValidInputParser() {
        // Constant input and addition
        Expression.parse("2");
        Expression.parse("2+3");
        Expression.parse("2.1");
        Expression.parse("2.1+3");
        Expression.parse("2.1+3.1");
        // Extra allowed number formatting inputs
        Expression.parse(".1");
        Expression.parse("2.");
        // Constant products
        Expression.parse("2*3");
        Expression.parse("2*3.1");
        Expression.parse("2.1*3");
        Expression.parse("2.1*3.1");
        // Variable input and products and sums
        Expression.parse("x");
        Expression.parse("2.0*x");
        Expression.parse("x*2.0");
        Expression.parse("x*y");
        Expression.parse("var");
        Expression.parse("Var");
        Expression.parse("var*Var");
        Expression.parse("x+y");
        Expression.parse("2+x");
        // Concatenation of operations
        Expression.parse("2+3*4");
        Expression.parse("(2+3)*4");
        Expression.parse("(1+1+1+1+1)*x");
        Expression.parse("5*(y)+(((x)))");
        // Whitespaces:
        Expression.parse(" 2");
        Expression.parse("2  ");
        Expression.parse("  2  ");
        Expression.parse("  2   +   3  ");
        Expression.parse("   x  +   3   *  y");
        // Large and small numbers
        Expression.parse(String.valueOf(Long.MAX_VALUE));
        Expression.parse("0.000000000000000000000000001");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalStringInputParser() {
        Expression.parse("(3");
        Expression.parse("3)");
        Expression.parse("3*");
        Expression.parse("3x");
        Expression.parse("(3+");
        Expression.parse("va r");
        Expression.parse("3/5");
        Expression.parse("3^4");
        Expression.parse("4E5");
    }


    /**
     * Test for toString()
     * - String contains same information (possibly 3.0 instead of 3 for integers is allowed)
     * - String has correct capitalization
     * - toString acts as inverse to parse-method:
     *          Expression.parse(Expression.parse(...).toString()) == Expression.parse(...)
     *          (this means that the string has the same AST as it's expression)
     *
     * Test cases:
     * - 3.1 should contain 3.1 (possibly more zeroes)
     * - 3*x+5 should contain 3, x and 5 (possibly 5.0000...)
     * - Expression e of "(5*x+y)*z+t*6" equals the expression parsed from e.toString()
     * -        same for "(x+y*(6*z+1)+5*(x+3*(z+x)))"
     */

    @Test
    public void testToString() {
        // String contains same information (possibly 3.0 instead of 3 for integers is allowed)
        assertTrue(Expression.parse("3.1").toString().contains("3.1"));
        assertTrue(Expression.parse("3*x+5").toString().contains("3"));
        assertTrue(Expression.parse("3*x+5").toString().contains("x"));
        assertTrue(Expression.parse("3*x+5").toString().contains("5"));
        // correct capitalization
        assertTrue(Expression.parse("3*X+5").toString().contains("X"));
        // Inverse: Expression.parse(Expression.parse(...).toString()) == Expression.parse(...)
        Expression e = Expression.parse("(5*x+y)*z+t*6");
        assertTrue(e.equals(Expression.parse(e.toString())));
        Expression f = Expression.parse("(x+y*(6*z+1)+5*(x+3*(z+x)))");
        assertTrue(f.equals(Expression.parse(f.toString())));
    }


    /**
     * Tests for equals() and hashCode():
     * - equals should be an equiv relation, equal objects should give same hash and equals(null) always is false
     * - observational equality (different spaces, brackets that don't give different groupings...
     * - Any expression of the same constants with the same value should be equal no matter the groupings
     * - Integers and doubles representing the same value are equal
     * - different order of expressions wrt sum give different AST so should be inequal
     * - plain different groupings (i.e. groupings that give completely different mathematical expressions) should not
     *      be equal
     * - Different groupings for addition and multiplication of variables also give different AST so are not equal,
     *      e.g. "(x+y)+z" is not equal to "x+(y+z)" and "(x*y)*z" is not equal to "x*(y*z)"
     *
     * Test cases: see the tests themselves.
     */

    @Test
    public void testEqualityIsEquivRelAndGivesConsistentHash() {
        Expression expression = Expression.parse("5*(3+x*(y+z)+t)");
        Expression expression_same = Expression.parse("5*(3+x*(y+z)+t)");
        Expression expression_same2 = Expression.parse("5*(3+x*(y+z)+t)");

        // Equality is an equivalence relation:
        // reflexivity
        assertTrue(expression.equals(expression));
        // symmetry
        assertTrue(expression.equals(expression_same));
        assertTrue(expression_same.equals(expression));
        // transitivity (isn't this trivial from symmetry?)
        assertTrue(expression_same.equals(expression_same2));
        assertTrue(expression.equals(expression_same2));

        // Equality => same hash
        assertTrue(expression.hashCode() == expression_same.hashCode());

        // e.equals(null) is false for all non-null Expressions e
        assertFalse(expression.equals(null));
    }

    @Test
    public void testStructuralEquality() {
        // Observational equality (different reps which are equal should have same abstract meaning)
        Expression expr = Expression.parse("1+x");
        // Different spaces
        assertEquals(expr, Expression.parse("  1  +  x  "));
        // Unnecessary brackets
        assertEquals(expr, Expression.parse("(1+x)"));
        assertEquals(expr, Expression.parse("(1) + (x)"));

        // Brackets don't matter for constants:
        Expression expr2 = Expression.parse("4+5+6");
        assertTrue(expr2.equals(Expression.parse("(4+5+6)")));
        assertTrue(expr2.equals(Expression.parse("4+(5+6)")));
        assertTrue(expr2.equals(Expression.parse("(4+5)+6")));
        assertTrue(Expression.parse("4+(5+6)").equals(Expression.parse("(4+5)+6")));
        Expression expr3 = Expression.parse("4*5*6");
        assertTrue(expr3.equals(Expression.parse("(4*5*6)")));
        assertTrue(expr3.equals(Expression.parse("4*(5*6)")));
        assertTrue(expr3.equals(Expression.parse("(4*5)*6")));
        assertTrue(Expression.parse("4*(5*6)").equals(Expression.parse("(4*5)*6")));

        // Equality for integer and double values for same number
        assertTrue(Expression.parse("5").equals(Expression.parse("5.0000")));
        assertTrue(Expression.parse("5*x").equals(Expression.parse("5.0000*x")));
    }

    @Test
    public void testPlainAndStructuralInequality() {
        Expression expr = Expression.parse("1+x");
        // Plain not equal expressions:
        assertFalse(expr.equals(Expression.parse("3+y")));
        // Different capitalization:
        assertFalse(expr.equals(Expression.parse("1+X")));
        // Different order is different AST, so should not give structural equality:
        assertFalse(expr.equals(Expression.parse("x+1")));

        // Different operations and brackets (also plain different)
        Expression expr2 = Expression.parse("3+x*(5+y)");
        assertFalse(expr2.equals(Expression.parse("3+(x*5+y)")));
        assertFalse(expr2.equals(Expression.parse("3+x*5+y")));
        assertFalse(expr2.equals(Expression.parse("(3+x)*5+y")));
        assertFalse(expr2.equals(Expression.parse("3+(x*5)+y")));
        assertFalse(expr2.equals(Expression.parse("3+x+(5+y)")));
        assertFalse(expr2.equals(Expression.parse("3*x*(5+y)")));

        // Different groupings addition and multiplication does give different expressions
        assertFalse(Expression.parse("(x+y)+z").equals(Expression.parse("x+(y+z)")));
        assertFalse(Expression.parse("(x*y)*z").equals(Expression.parse("x*(y*z)")));
    }


    /**
     * Tests for differentiate():
     * - test derivative constant
     * - test derivative var to itself and to other
     * - test sum rule
     * - test Leibniz rule
     */
    @Test
    public void testDifferentiate(){
        Expression constant = Expression.parse("5.0");
        assertEquals(constant.differentiate("x"), Expression.parse("0"));

        Expression var1 = Expression.parse("x");
        assertEquals(var1.differentiate("x"), Expression.parse("1"));
        assertEquals(var1.differentiate("y"), Expression.parse("0"));

        Expression expr1 = Expression.parse("X");
        Expression expr2 = Expression.parse("72.5");
        Sum sum1 = new Sum(expr1,expr2);
        assertEquals(sum1.differentiate("X"), Expression.parse("1"));
        assertEquals(sum1.differentiate("y"), Expression.parse("0"));

        Product product1 = new Product(expr1,expr2);
        assertEquals(product1.differentiate("X"), expr2);
        assertEquals(product1.differentiate("y"), Expression.parse("0"));
    }


    /**
     * Tests for simplify (both the simplify and evaluate-versions of this method)
     * - test constants and Variables
     * - test
     */

    @Test
    public void testSimplifyConstantsAndVariables() {
        // Constants
        Expression constant = Expression.parse("5.0 * 6 + 2.5 * (5 * 8.3 + 7)");
        assertEquals(constant.simplify(), Expression.parse("151.25"));
        // with map
        Expression constant2 = Expression.parse("5.0");
        Map<String, Double> map = new HashMap<>();
        map.put("x", 18.0);
        assertEquals(constant2.simplify(map), Expression.parse("5.0"));

        // Variables
        Expression var1 = Expression.parse("x");
        assertTrue(var1.simplify().equals(Expression.parse("x")));
        // with map
        Expression var2 = Expression.parse("y");
        assertEquals(var1.simplify(map), Expression.parse("18.0"));
        assertEquals(var2.simplify(map), Expression.parse("y"));
    }

    @Test
    public void testSimplifySums() {
        Expression expr1 = Expression.parse("X");
        Expression expr2 = Expression.parse("72.5");
        Sum sum1 = new Sum(expr1, expr2);
        Sum sum2 = new Sum(Expression.parse("5.4"), Expression.parse("7"));

        assertEquals(sum2.simplify(), Expression.parse("12.4"));

        // x + 0 = x = 0 + x
        Sum sum3 = new Sum(expr1, Expression.parse("0"));
        Sum sum4 = new Sum(Expression.parse("0"), expr1);
        assertTrue(sum3.simplify().equals(expr1));
        assertTrue(sum4.simplify().equals(expr1));

        // with maps
        Map<String, Double> map = new HashMap<>();
        map.put("X", 18.0);
        assertEquals(sum1.simplify(map), Expression.parse("90.5"));
        Map<String, Double> map2 = new HashMap<>();
        map.put("y", 18.0);
        assertEquals(sum1.simplify(map2), new Sum(expr1, expr2));
    }

    @Test
    public void testSimplifyProducts(){
        // numerical simplifications:
        Product product2 = new Product(Expression.parse("5.5"), Expression.parse("7"));
        assertEquals(product2.simplify(), Expression.parse("38.5"));
        // (5.5 + 2.5)*7 = 56
        Product product7 = new Product(new Sum(Expression.parse("5.5"), Expression.parse("2.5")),
                Expression.parse("7"));
        assertEquals(product7.simplify(), Expression.parse("56"));
        // 7*(5.5 + 2.5) = 56
        Product product8 = new Product(Expression.parse("7"),
                new Sum(Expression.parse("5.5"), Expression.parse("2.5")));
        assertEquals(product8.simplify(), Expression.parse("56"));

        // x*1=1*x=x
        Expression expr1 = Expression.parse("x");
        Product product3 = new Product(Expression.parse("1"), expr1);
        Product product4 = new Product(expr1, Expression.parse("1"));
        assertEquals(product3.simplify(), expr1);
        assertEquals(product4.simplify(), expr1);

        // x*0=0*x=0
        Product product5 = new Product(Expression.parse("0"), expr1);
        Product product6 = new Product(expr1, Expression.parse("0"));
        assertEquals(product5.simplify(), Expression.parse("0"));
        assertEquals(product6.simplify(), Expression.parse("0"));

        // x+x = 2*x
        assertEquals((new Sum(expr1, expr1)).simplify(), new Product(Expression.parse("2"), expr1));

        // with maps
        Expression expr2 = Expression.parse("72.5");
        Product product1 = new Product(expr1,expr2);
        Map<String, Double> map = new HashMap<>();
        map.put("x", 18.0);
        assertEquals(product1.simplify(map), Expression.parse("1305"));

        // test ordering of constants and variables
        Map<String, Double> map2 = new HashMap<>();
        map.put("y", 18.0);
        assertEquals(product1.simplify(map2), new Product(expr2,expr1));
    }

    @Test
    public void testDifferentiateAndSimplifyCombo() {
        Expression var = Expression.parse("x");
        Product product2 = new Product(var, var);
        assertEquals(product2.differentiate("x").simplify(),new Product(Expression.parse("2"),var));

        Expression expression = Expression.parse("2.5*x*x*x*y+5*x");
        Expression derivative = Expression.parse("7.5*x*x*y+5");
        Map<String, Double> map = new HashMap<>();
        // Some random values to check d/dx of expression equals derivative:
        map.put("y", 2.1);
        map.put("x", 5.5);
        assertEquals((expression.differentiate("x").simplify()).simplify(map), derivative.simplify(map));
    }

    /**
     * Here some testcases from bugs I encountered during the programming of simplify
     * Most inputs seem quite random since they were found by trying to enter pathological function into terminal
     * These bugs should be fixed,
     */
    @Test
    public void testStackOverflowBugs(){
        // x+x*x+x gave stack overflow, x+y+x, x+y
        Expression.parse("x+x*x+x").simplify();
        Expression.parse("x+y+x").simplify();
        Expression.parse("x+y").simplify();

        // x*(x+x*(y+x)*x+(y*x+z)*x+x) gave stack overflow,  x+x*(y+x)*x,  x+x*y*x
        Expression.parse("x*(x+x*(y+x)*x+(y*x+z)*x+x)").simplify();
        Expression.parse("x+x*(y+x)*x").simplify();
        Expression.parse("x+x*y*x").simplify();

        // x*x*x*X*X*X*X*z*X*Xx*X*x*Y*X*Y*x, and then d/dx gave stack overflow
        // (I was trying to simplify directly and fully after differentiating, but made a small mistake which caused
        // the stack-overflow under rare circumstances such as this function below))
        Expression.parse("x*x*x*X*X*X*X*z*X*Xx*X*x*Y*X*Y*x").differentiate("x").simplify();
    }


}
