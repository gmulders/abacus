Abacus
======

Introduction
------------
Abacus is a small library that parses and evaluates expressions.

Language definition
-------------------
We will define the language in two parts. The first part is a list of tokens the lexer recognises. The second part is a
Backus-Naur Form*ish* description using the tokens.

### Tokens

The lexer recognises the following tokens:

	END_OF_INPUT      = (Not really a character, just the end of the input. \0 if you like.)
	END_OF_EXPRESSION = ";"
	WHITE_SPACE       = " " | "\t" | "\n" | "\r"
	NEW_LINE          = "\n" | "\r\n"
	COMMA             = ","
	IDENTIFIER        = [a-zA-Z][a-zA-Z0-9]*
	LEFT_PARENTHESIS  = "("
	RIGHT_PARENTHESIS = ")"
	STRING            = \'(\\.|[^\\'])*\'  (escaping of special characters is possible)
	FLOAT             = ([0-9]+ \. [0-9]* | \. [0-9]+)
	INTEGER           = 0 | [1-9][0-9]*
	BOOLEAN_AND       = "&&"
	BOOLEAN_OR        = "||"
	PLUS              = "+"
	MINUS             = "-"
	MULTIPLY          = "*"
	DIVIDE            = "/"
	NEQ               = "!="
	NOT               = "!"
	LEQ               = "<="
	LT                = "<"
	GEQ               = ">="
	GT                = ">"
	EQ                = "=="
	IF                = "?"
	COLON             = ":"
	PERCENT           = "%"
	POWER             = "^"
	ASSIGNMENT        = "="
	NULL              = "null"

### Definition
	<statement-list>   := <statement> | <statement> <statement-list>
	<statement>        := <assignment> <eos>
	<assignment>       := <expression> | <expression> ASSIGNMENT <assignment>
	<expression>       := <conditional>
	<conditional>      := <booleanoperation> | <booleanoperation> IF <expression> COLON <expression>
	<booleanoperation> := <comparison> | <comparison> ( BOOLEAN_AND | BOOLEAN_OR ) <booleanoperation>
	<comparison>       := <addition> | <addition> ( EQ | NEQ | LT | LEQ | GEQ | GT ) <comparison>
	<addition>         := <term> | <term> ( PLUS | MINUS ) <comparison>
	<term>             := <power> | <power> ( MULTIPLY | DIVIDE | PERCENT ) <term>
	<power>            := <unary> | <unary> ( POWER ) <power>
	<unary>            := ( epsilon | PLUS | MINUS | NOT ) <factor>
	<factor>           := FLOAT | INTEGER | STRING | ( LEFT_PARENTHESIS <expression> RIGHT_PARENTHESIS ) 
	                         | IDENTIFIER ( epsilon | LEFT_PARENTHESIS <expression-list> RIGHT_PARENTHESIS )
	<expression-list>  := <expression> | <expression> <expression-list>
	
	<eos> := ; | end of input