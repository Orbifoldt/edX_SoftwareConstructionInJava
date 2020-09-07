/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for the static methods of Commands.
 */
public class CommandsTest {

    // Testing strategy
    //   Same as ExpressionTest, same partitions and all the tests for simplify and differentiate
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    Constant cst = new Constant(2.5);
    Variable x = new Variable("x");
    Variable y = new Variable("y");
    String cstString = "2.5";
    String xString = "x";
    String yString = "y";

    @Test
    public void testSimpleInput(){
        assertEquals(Commands.differentiate(cstString, xString), "0");
        assertEquals(Commands.differentiate(xString, xString), "1");
        assertEquals(Commands.differentiate(xString, yString), "0");
    }

    /**
     * With the current Expression interface it's trivial that the commands work as intended, assuming that the
     * methods in Main are correct, i.e. that they recognize the correct commands.
     * At this time it's a waste of time, for me, to basically repeat the same tests but now check the results as strings
     */


    // TODO tests for Commands.differentiate() and Commands.simplify()
    
    
}
