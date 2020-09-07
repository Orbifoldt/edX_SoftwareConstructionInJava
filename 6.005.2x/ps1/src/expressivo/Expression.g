/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */

// grammar Expression;

/*
 *
 * You should make sure you have one rule that describes the entire input.
 * This is the "start rule". Below, "root" is the start rule.
 *
 * For more information, see the parsers reading.
 */

 /*
  * This grammar expression represents a polynomial. (Definitions I've marked with _xxx_)
  *
  * =====DEFINITIONS=====
  * _root_ is a polynomial which is the sum of 1 or more terms, i.e. it's a sum as defined below
  * A _sum_ is sequence of one or more terms divided by the character '+' in between them
  * Each _term_ can be either just a constant, or just a variable, or it is the sum or product of terms:
  *       # if it is a sum it must always be inside brackets
  *       # if it is a product it may or may not appear inside brackets
  * A _product_ is a sequence of one or more terms divided by the character '*' in between them
  * A _constant_ is either an _integer_ (a sequence of digits) or a _decimal_ (either >= 1 digits followed by a decimal
  *     seperator followed by zero or more digits, or zero or more digits followed by a decimal seperator followed by
  *     one or more digits. The decimal seperator is standard '.'
  * A _variable_ is a case-sensitive sequence of letters
  * We ignore whitespaces.
  *
  *
  * =====EXAMPLES=====
  * With these definitions "(7)" is a polynomial, it's a product (or sum for that matter) of 1 term in between brackets,
  *     this term is a constant, it's the integer 7
  * Also "(x     + (2. * x + x*x ) )" is a polynomial, it's a sum in between brackets of the variable x and the term
  *     "(2. * x + X*x + 5)", which is a sum in between brackets of the terms "2. * x", "x*x" and "5". The first term
  *     is a product of the constant "2." (which is a decimal) and the variable "x", the second is the product of the
  *     variables "X" and "x" (which are not the same!) and the third term is the constant "5", which is an integer
  */

root ::= sum;
@skip whitespace{

/* THIS IS LEFT-RECURSIVE:
*	sum ::= term ('+' term)*;
*	term ::= constant | variable | '(' sum ')' | product | '(' product ')';
*	product ::= term ('*' term)*;
*/

/* two types:    (token | product | '(' product ')')    and   (token | '(' sum ')')
*/

	token ::= constant | variable;
	sum ::= (token | product | '(' product ')') ('+' (token | product | '(' product ')'))*;
	product ::= (token | '(' sum ')') ('*' (token | '(' sum ')'))*;

}
constant ::=  integer | decimal;
variable ::= [a-zA-Z]+;
integer ::= [0-9]+;
decimal ::= ([0-9]* '.' [0-9]+) | ([0-9]+ '.' [0-9]*);
whitespace ::= [ \t\r\n]+;
