package expressivo;

import lib6005.parser.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * An immutable data type representing a polynomial expression of:
 * + and *
 * nonnegative integers and floating-point numbers            (i.e.: 3 or 3. or 3.0 or 0.3 or .3)
 * variables (case-sensitive nonempty strings of letters)
 * <p>
 * <p>PS1 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {

    // Datatype definition
    //   Expression = Constant(value:double)
    //              + Variable(variable:String)
    //              + Sum(left:Expression, right:Expression)
    //              + Product(left:Expression, right:Expression)

    enum Grammar {
        ROOT, SUM, PRODUCT, CONSTANT, VARIABLE, INTEGER, DECIMAL, WHITESPACE,TOKEN;
    }

    /**
     * Parse an expression.
     *
     * @param input expression to parse, as defined in the PS1 handout. Also numbers formatted as ".1" or "2." are
     *              allowed inputs.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        try {
            Parser<Grammar> parser = GrammarCompiler.compile(new File("./src/expressivo/Expression.g"), Grammar.ROOT);
            ParseTree<Grammar> tree = parser.parse(input);
            return buildAST(tree);
        } catch (UnableToParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot parse the expression: " + input);
        } catch (IOException e) {
            System.out.println("Cannot open the file Expression.g");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This function builds an abstract syntax tree from the concrete syntax tree
     * @param tree: the concrete syntax tree which needs to be parsed into an Expression (the AST)
     * @return the abstract syntax tree, i.e. the expression corresponding to the input tree
     */
    public static Expression buildAST(ParseTree<Grammar> tree) {
        switch (tree.getName()) {
            case ROOT:
                /* The root has a single sum child, in addition to having potentially some whitespace. */
                return buildAST(tree.childrenByName(Grammar.SUM).get(0));//.simplify();

            case SUM:
                /* A sum can have multiple children which need to be accumulated */
                return accumulate(tree, Grammar.SUM).simplify();

            case PRODUCT:
                /* A product can have multiple children which need to be accumulated */
                return accumulate(tree, Grammar.PRODUCT).simplify();

            case TOKEN:
                /* A token only contains one child, either a constant or a variable*/
                if(tree.childrenByName(Grammar.VARIABLE).isEmpty()){
                    return buildAST(tree.childrenByName(Grammar.CONSTANT).get(0));
                }else{
                    return buildAST(tree.childrenByName(Grammar.VARIABLE).get(0));
                }

            case VARIABLE:
                /* A variable is a terminal containing a variable */
                return new Variable(tree.getContents());

            case CONSTANT:
                /* Constant is a non-terminal with two children, namely a decimal or an integer */
                if(tree.childrenByName(Grammar.DECIMAL).isEmpty()){
                    return buildAST(tree.childrenByName(Grammar.INTEGER).get(0));
                }else{
                    return buildAST(tree.childrenByName(Grammar.DECIMAL).get(0));
                }

            case DECIMAL:
                /*  A decimal is a terminal containing a constant. */
                return new Constant(Double.parseDouble(tree.getContents()));

            case INTEGER:
                /* An integer is a terminal containing a constant. */
                return new Constant(Double.parseDouble(tree.getContents()));

            case WHITESPACE:
                /* Since we are always avoiding calling buildAST with whitespace, the code should never make it here.*/
                throw new RuntimeException("You should never reach here:" + tree);
        }
        /*
         * The compiler should be smart enough to tell that this code is unreachable, but it isn't.
         */
        throw new RuntimeException("You should never reach here:" + tree);
    }

    /**
     * Accumulate and group consecutive sums or product, i.e. this extracts the expression from the tree
     * and adds brackets to this expression for certain terms (either sum or products) such that the expression
     * only contains "primitive" terms, i.e. sums and product with only two expressions
     * E.g. 3+4+5 becomes (3+4)+5, same for products (works for any constants and variables)
     * @param tree The tree of the expression that need to be accumulated
     * @param name the type of operation on which we need to accumulate, can only be Grammar.SUM or Grammar.PRODUCT
     * @return An accumulated expression, i.e. and expression where we have exactly two terms per sum/product, longer
     * sums or product will be grouped using brackets. In terms of the graph-tree, this splits each node with >2
     * children into multiple nodes with each at most 2 children.
     */
    public static Expression accumulate(ParseTree<Grammar> tree, Grammar name) {
        // check that the client follows the specs:
        assert (name.equals(Grammar.SUM) || name.equals(Grammar.PRODUCT));
        boolean first = true;
        Expression result = null;
        for(ParseTree<Grammar> child : tree.children()){
            // We go through all children until we find a non-whitespace character
            if (!child.getName().equals(Grammar.WHITESPACE)) {
                if (first) {
                    result = buildAST(child);
                    first = false;
                } else {
                    if (name.equals(Grammar.SUM)) result = new Sum(result, buildAST(child));
                    else if (name.equals(Grammar.PRODUCT)) result = new Product(result, buildAST(child));
                    else System.out.println("error");
                }
            }
        }
        if (first) {
            throw new RuntimeException("sum must have at least one non whitespace child:" + tree);
        }
        return result;
    }

    /**
     * Take the derivative of an expression with respect to a variable x
     * @param stringVariable the variable with respect to which we take the derivative
     * @return the derivative of the expression
     */
    public Expression differentiate(String stringVariable);

    /**
     * Given an expression this method simplifies it, i.e. products with the number 0 are put to zero, product with
     * the number 1 removes the * 1 or 1 * from the expression, and a sum with the number 0 removes the 0 +  or + 0.
     * Furthermore this orders the products such that all constants appear on the left of the variables.
     * It also tries to simplify the products and sums of constants to a single constant (but ((x+1)+1) != (x+2))
     * Furthermore it takes out common factors from sums
     * @return a simplified expression
     */
    public Expression simplify();

    /**
     * @return true if the expression is a constant (i.e an integer or a decimal) and false if not
     */
    public boolean isConstant();

    /**
     * @return true if the expression is a variable and false if not
     */
    public boolean isVariable();

    /**
     * @return true if the expression is a product of two expressions, and false if not
     */
    public boolean isProduct();


    /**
     * Simplify, and simplify the expression in the points specified by the environment, e.g. if the map contains the
     * entry x:3 then every variable corresponding to the string x is substituted for the constant 3
     * Given an expression this method also simplifies it, i.e. products with the number 0 are put to zero, product with
     * the number 1 removes the * 1 or 1 * from the expression, and a sum with the number 0 removes the 0 +  or + 0.
     * Furthermore this orders the products such that all constants appear on the left of the variables.
     * It also simplifies the products and sums of constants to a single constant
     * Furthermore it takes out common factors from sums
     * @param environment contains variables as keys and constants as values, all the variables are substituted for
     *                    the corresponding constants. All keys are required to be non-negative
     * @return an expression in which all variables that appear as keys in the map are substituted for their values
     */
    public Expression simplify(Map<String,Double> environment);





    /**
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override
    public String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, i.e. if and only if the following three hold
     * 1. the expressions contain the same variables, numbers, and operators;
     * 2. those variables, numbers, and operators are in the same order, read left-to-right;
     * 3. and they are grouped in the same way.
     * Furthermore for longer expressions or constant expressions we add:
     *  - For longer additive and multiplicative expressions as "(x+y)+z" the AST is not the same as for "x+(y+z)",
     *        so these will be unequal (same for + substituted by *)
     *  - For constant expressions however we do have that "(4+5)+6" equals "4+(5+6)" equals "4+5+6", since these are
     *        just numbers, again same for multiplication.
     *  - Also whenever a double is equal to its integer version then this gives equality
     *
     */
    @Override
    public boolean equals(Object thatObject);

    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     * e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();

    
    /* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires permission of course staff.
     */
}
