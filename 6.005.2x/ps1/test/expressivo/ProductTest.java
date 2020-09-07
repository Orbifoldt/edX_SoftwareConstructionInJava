package expressivo;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * These tests asproducte that the Constant, Variable and Sum classes are fully implemented as specified by their specs
 */

public class ProductTest {
    final Expression expr1 = new Variable("x");
    final Expression expr2 = new Constant(72.5);
    final Product product1 = new Product(expr1,expr2);

    @Test
    public void testGetLeft() throws Exception {
        assertEquals(product1.getLeft(), expr1);
    }

    @Test
    public void testGetRight() throws Exception {
        assertEquals(product1.getRight(), expr2);
    }

    @Test
    public void testDifferentiate() throws Exception {
        assertEquals(product1.differentiate(new Variable("x")), expr2);
        assertEquals(product1.differentiate(new Variable("y")), new Constant(0));
        assertEquals(product1.differentiate("x"), expr2);
        assertEquals(product1.differentiate("y"), new Constant(0));
    }

    /**
     * simplify orders the product such that the constants appear in the front of the variables
     */
    @Test
    public void testSimplify() throws Exception {
        // numerical simplifications:
        Product product2 = new Product(new Constant(5.5), new Constant(7));
        assertEquals(product2.simplify(), new Constant(38.5));
        // (5.5 + 2.5)*7 = 56
        Product product7 = new Product(new Sum(new Constant(5.5), new Constant(2.5)),
                                       new Constant(7));
        assertEquals(product7.simplify(), new Constant(56));
        // 7*(5.5 + 2.5) = 56
        Product product8 = new Product(new Constant(7),
                                       new Sum(new Constant(5.5), new Constant(2.5)));
        assertEquals(product8.simplify(), new Constant(56));

        // x*1=1*x=x
        Product product3 = new Product(new Constant(1), expr1);
        Product product4 = new Product(expr1, new Constant(1));
        assertEquals(product3.simplify(), expr1);
        assertEquals(product4.simplify(), expr1);
        // x*0=0*x=0
        Product product5 = new Product(new Constant(0), expr1);
        Product product6 = new Product(expr1, new Constant(0));
        assertEquals(product5.simplify(), new Constant(0));
        assertEquals(product6.simplify(), new Constant(0));

        // x+x = 2*x
        assertEquals((new Sum(expr1, expr1)).simplify(), new Product(new Constant(2), expr1));

        Map<String, Double> map = new HashMap<>();
        map.put("x", 18.0);
        assertEquals(product1.simplify(map), new Constant(1305));
        // test ordering of constants and variables
        Map<String, Double> map2 = new HashMap<>();
        map.put("y", 18.0);
        assertEquals(product1.simplify(map2), new Product(expr2,expr1));
    }

    @Test
    public void testDifferentiateAndSimplifyCombo() {
        Variable var = new Variable("x");
        Product product2 = new Product(var, var);
        assertEquals(product2.differentiate(var).simplify(),new Product(new Constant(2),var));
    }

    @Test
    public void testIsConstant() throws Exception {
        assertFalse(product1.isConstant());
    }

    @Test
    public void testIsVariable() throws Exception {
        assertFalse(product1.isVariable());

    }

    @Test
    public void testIsProduct() throws Exception {
        assertTrue(product1.isProduct());
    }


    @Test
    public void testToString() throws Exception {
        assertTrue(product1.toString().contains("x"));
        assertTrue(product1.toString().contains("*"));
        assertTrue(product1.toString().contains("72.5"));
    }

    @Test
    public void testEqualsAndHash(){
        Product product2 = new Product(expr1,expr2);
        Product product3 = new Product(expr1,expr2);

        // Equiv relation:
        assertEquals(product1, product1);
        assertEquals(product1, product2);
        assertEquals(product2, product1);
        assertEquals(product2, product3);
        assertEquals(product1, product3);

        // Equal => same hash
        assertEquals(product1.hashCode(), product2.hashCode());

        // Null
        assertFalse(product1.equals(null));

        // Different objects (order of product matters)
        Product product4 = new Product(expr2,expr1);
        Product product5 = new Product(expr1,expr1);
        assertFalse(product4.equals(product1));
        assertFalse(product5.equals(product1));
    }
}