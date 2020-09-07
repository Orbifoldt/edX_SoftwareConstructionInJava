package expressivo;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * These tests assume that the Constant and Variable classes are fully implemented as specified by their specs
 */

public class SumTest {
    final Expression expr1 = new Variable("x");
    final Expression expr2 = new Constant(72.5);
    final Sum sum1 = new Sum(expr1,expr2);

    @Test
    public void testGetLeft() throws Exception {
        assertEquals(sum1.getLeft(), expr1);
    }

    @Test
    public void testGetRight() throws Exception {
        assertEquals(sum1.getRight(), expr2);
    }

    @Test
    public void testDifferentiate() throws Exception {
        assertEquals(sum1.differentiate(new Variable("x")), new Constant(1));
        assertEquals(sum1.differentiate(new Variable("y")), new Constant(0));
        assertEquals(sum1.differentiate("x"), new Constant(1));
        assertEquals(sum1.differentiate("y"), new Constant(0));
    }

    @Test
    public void testSimplify() throws Exception {
        Sum sum2 = new Sum(new Constant(5.4), new Constant(7));
        assertEquals(sum2.simplify(), new Constant(12.4));
        // x + 0 = x = 0 + x
        Sum sum3 = new Sum(expr1, new Constant(0));
        Sum sum4 = new Sum(new Constant(0), expr1);
        assertTrue(sum3.simplify().equals(expr1));
        assertTrue(sum4.simplify().equals(expr1));
        // x+x becomes 2*x is tested in ExpressionTest

        Map<String, Double> map = new HashMap<>();
        map.put("x", 18.0);
        assertEquals(sum1.simplify(map), new Constant(90.5));
        Map<String, Double> map2 = new HashMap<>();
        map.put("y", 18.0);
        assertEquals(sum1.simplify(map2), new Sum(expr1, expr2));
    }

    @Test
    public void testIsConstant() throws Exception {
        assertFalse(sum1.isConstant());
    }

    @Test
    public void testIsVariable() throws Exception {
        assertFalse(sum1.isVariable());

    }

    @Test
    public void testIsProduct() throws Exception {
        assertFalse(sum1.isProduct());
    }

    @Test
    public void testToString() throws Exception {
        assertTrue(sum1.toString().contains("x"));
        assertTrue(sum1.toString().contains("+"));
        assertTrue(sum1.toString().contains("72.5"));
    }

    @Test
    public void testEqualsAndHash(){
        Sum sum2 = new Sum(expr1,expr2);
        Sum sum3 = new Sum(expr1,expr2);

        // Equiv relation:
        assertEquals(sum1, sum1);
        assertEquals(sum1, sum2);
        assertEquals(sum2, sum1);
        assertEquals(sum2, sum3);
        assertEquals(sum1, sum3);

        // Equal => same hash
        assertEquals(sum1.hashCode(), sum2.hashCode());

        // Null
        assertFalse(sum1.equals(null));

        // Different objects (order of sum matters)
        Sum sum4 = new Sum(expr2,expr1);
        Sum sum5 = new Sum(expr1,expr1);
        assertFalse(sum4.equals(sum1));
        assertFalse(sum5.equals(sum1));
    }
}