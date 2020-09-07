package expressivo;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ConstantTest {
    @Test
    public void testGetValue() throws Exception {
        // new constant with double-input
        Constant constant = new Constant(5.5);
        assertTrue(constant.getValue()==5.5);
        // new constant with integer-input
        Constant constant2 = new Constant(5);
        assertTrue(constant2.getValue()==5.0);
        // different input for double (no zeroes)
        Constant constant3 = new Constant(.5);
        assertTrue(constant3.getValue()==0.5);
        Constant constant4 = new Constant(5.);
        assertTrue(constant4.getValue()==5.0);
    }

    @Test
    public void testDifferentiate() throws Exception {
        Constant constant = new Constant(5.0);
        assertEquals(constant.differentiate(new Variable("x")), new Constant(0));
        assertEquals(constant.differentiate("x"), new Constant(0));
    }

    @Test
    public void testSimplify() throws Exception {
        Constant constant = new Constant(5.0*6+2.5*(5*8.3+7));
        assertEquals(constant.simplify(), new Constant(151.25));

        Constant constant2 = new Constant(5.0);
        Map<String, Double> map = new HashMap<>();
        map.put("x", 18.0);
        assertEquals(constant2.simplify(map), new Constant(5.0));
    }

    @Test
    public void testIsConstant() throws Exception {
        Constant constant = new Constant(5.0);
        assertTrue(constant.isConstant());
    }

    @Test
    public void testIsVariable() throws Exception {
        Constant constant = new Constant(5.0);
        assertFalse(constant.isVariable());
    }

    @Test
    public void testIsProduct() throws Exception {
        Constant constant = new Constant(5.0);
        assertFalse(constant.isProduct());
    }


    @Test
    public void testToString() throws Exception {
        // new constant with double-input
        Constant constant = new Constant(5.5);
        assertTrue(constant.toString().contains("5.5"));
        // new constant with integer-input
        Constant constant2 = new Constant(5);
        assertTrue(constant2.toString().contains("5"));
        assertFalse(constant2.toString().contains("."));
        // different input for double (no zeroes)
        Constant constant3 = new Constant(.5);
        assertTrue(constant3.toString().contains("0.5"));
        Constant constant4 = new Constant(5.);
        assertTrue(constant4.toString().contains("5"));
    }

    @Test
    public void testEqualsAndHash(){
        Constant constant1 = new Constant(5.5);
        Constant constant2 = new Constant(5.5);
        Constant constant3 = new Constant(5.5);

        // Equiv relation:
        assertEquals(constant1, constant1);
        assertEquals(constant1, constant2);
        assertEquals(constant2, constant1);
        assertEquals(constant2, constant3);
        assertEquals(constant1, constant3);

        // Equal => same hash
        assertEquals(constant1.hashCode(), constant2.hashCode());

        // Null
        assertFalse(constant1.equals(null));

        // Different objects
        Constant constant4 = new Constant(6.5);
        assertFalse(constant4.equals(constant1));
    }


}