Abacus
======

Intro
-----
Abacus is a small library 

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
	IDENTIFIER        = 
	LEFT_PARENTHESIS  = "("
	RIGHT_PARENTHESIS = ")"
	STRING            = 
	FLOAT             =
	INTEGER           =
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
	ELSE              = ":"
	PERCENT           = "%"
	POWER             = "^"
	ASSIGNMENT        = "="
	NULL              = "null"

### Definition
	<statement-list>   := <statment> | <statement> <statement-list>
	<statement>        := <assignment> <eos> | <expression> <eos>
	<assignment>       := <variable> ASSIGNMENT <assignment> | <variable> ASSIGNMENT <expression>
	<expression>       := <conditional>
	<conditional>      := <booleanoperation> | <booleanoperation> IF <expression> ELSE <expression>
	<booleanoperation> := <comparison> | <comparison> ( BOOLEAN_AND | BOOLEAN_OR ) <booleanoperation>
	<comparison>       := <addition> | <addition> ( EQ | NEQ | LT | LEQ | GEQ | GT ) <comparison>
	<addition>         := <term> | <term> ( PLUS | MINUS ) <comparison>
	<term>             := <power> | <power> ( MULTIPLY | DIVIDE | PERCENT ) <term>
	<power>            := <unary> | <unary> ( POWER ) <power>
	<unary>            := ( epsilon | PLUS | MINUS | NOT ) <factor>
	<factor>           := FLOAT | INTEGER | STRING | ( LEFT_PARENTHESIS <expression> RIGHT_PARENTHESIS ) 
	                         | IDENTIFIER (epsilon | LEFT_PARENTHESIS <expression-list> RIGHT_PARENTHESIS)
	<expression-list>  := <expression> | <expression> <expression-list>
	
	<eos> := ; | end of input