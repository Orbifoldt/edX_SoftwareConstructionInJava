package warmup;

import java.util.HashSet;
import java.util.Set;

public class Quadratic {

    /**
     * Find the integer roots of a quadratic equation, ax^2 + bx + c = 0.
     * @param a coefficient of x^2
     * @param b coefficient of x
     * @param c constant term.  Requires that a, b, and c are not ALL zero.
     * @return all integers x such that ax^2 + bx + c = 0.
     */
    public static Set<Integer> roots(int a, int b, int c) {
        Set<Integer> roots = new HashSet<>();

        // non constant linear polynomial:
        if (a == 0 && b != 0) {
            int zero = -c/b;
            if (b * zero + c == 0) {
                roots.add(zero);
                return roots;
            }
        }

        // constant linear polynomial, if this holds then c != 0, so no solutions
        if (a == 0 && b == 0) {
            return roots;
        }

        // quadratic case:
        Long longa = Long.valueOf(a);
        Long longb = Long.valueOf(b);
        Long longc = Long.valueOf(c);
        Long discriminant = longb * longb - 4L * longa * longc;
        int zero1 = (int) Math.floor((-b + Math.sqrt(discriminant)) / (2.0 * a));
        int zero2 = (int) Math.floor((-b - Math.sqrt(discriminant)) / (2.0 * a));

        // Check if rounded zeroes are true zeroes.
        if (a * zero1 * zero1 + b* zero1 +c == 0){
            roots.add(zero1);
        }
        if (a * zero2 * zero2 + b* zero2 +c == 0){
            roots.add(zero2);
        }
        return roots;
    }

    
    /**
     * Main function of program.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("For the equation x^2 - 4x + 3 = 0, the possible solutions are:");
        Set<Integer> result = roots(1, -4, 3);
        System.out.println(result);
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
