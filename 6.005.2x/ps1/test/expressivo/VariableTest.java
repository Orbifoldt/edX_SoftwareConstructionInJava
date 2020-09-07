package expressivo;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class VariableTest {
    @Test
    public void testGetVariable() throws Exception {
        Variable var1 = new Variable("x");
        Variable var2 = new Variable("var");
        Variable var3 = new Variable("vAr");
        assertTrue(var1.getVariable().equals("x"));
        assertTrue(var2.getVariable().equals("var"));
        assertTrue(var3.getVariable().equals("vAr"));
    }

    @Test
    public void testDifferentiate() throws Exception {
        Variable var1 = new Variable("x");
        assertEquals(var1.differentiate(new Variable("x")), new Constant(1));
        assertEquals(var1.differentiate(new Variable("y")), new Constant(0));
        assertEquals(var1.differentiate("x"), new Constant(1));
        assertEquals(var1.differentiate("y"), new Constant(0));
    }

    @Test
    public void testSimplify() throws Exception {
        Variable var1 = new Variable("x");
        assertTrue(var1.simplify().equals(new Variable("x")));

        Variable var2 = new Variable("y");
        Map<String, Double> map = new HashMap<>();
        map.put("x", 18.0);
        assertEquals(var1.simplify(map), new Constant(18.0));
        assertEquals(var2.simplify(map), new Variable("y"));
    }

    @Test
    public void testIsConstant() throws Exception {
        Variable var1 = new Variable("x");
        assertFalse(var1.isConstant());
    }

    @Test
    public void testIsVariable() throws Exception {
        Variable var1 = new Variable("x");
        assertTrue(var1.isVariable());
    }

    @Test
    public void testIsProduct() throws Exception {
        Variable var1 = new Variable("x");
        assertFalse(var1.isProduct());
    }


    // number of spaces in toString are not specified
    @Test
    public void testToString() throws Exception {
        Variable var1 = new Variable("x");
        Variable var2 = new Variable("vAr");
        assertTrue(var1.toString().contains("x"));
        assertTrue(var2.toString().contains("vAr"));

    }

    @Test
    public void testEqualsAndHash(){
        Variable variable1 = new Variable("x");
        Variable variable2 = new Variable("x");
        Variable variable3 = new Variable("x");

        // Equiv relation:
        assertEquals(variable1, variable1);
        assertEquals(variable1, variable2);
        assertEquals(variable2, variable1);
        assertEquals(variable2, variable3);
        assertEquals(variable1, variable3);

        // Equal => same hash
        assertEquals(variable1.hashCode(), variable2.hashCode());

        // Null
        assertFalse(variable1.equals(null));

        // Different objects
        Variable variable4 = new Variable("X");
        Variable variable5 = new Variable("y");
        assertFalse(variable4.equals(variable1));
        assertFalse(variable5.equals(variable1));
    }
}