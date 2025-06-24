grammar Formula;

formula: expr EOF;

expr
 : expr op=(MULT | DIV) expr               #multiplicationExpr
 | expr op=(PLUS | MINUS) expr             #additiveExpr
 | expr op=(GTEQ | LTEQ | GT | LT) expr    #relationalExpr
 | expr op=(EQ | NEQ) expr                 #equalityExpr
 | expr '&&' expr                          #andExpr
 | expr '||' expr                          #orExpr
 | <assoc=right>expr '?' expr ':' expr     #ternaryExpr
 | atom                                    #atomExpr
 ;

PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
EQ: '==';
NEQ: '!=';
GT: '>';
LT: '<';
GTEQ: '>=';
LTEQ: '<=';

atom
 : '(' expr ')'   #parenthesisAtom
 | (INT | FLOAT)  #numberAtom
 | '@' NOME       #idAtom
 ;

INT: [0-9]+;
FLOAT
 : [0-9]+ '.' [0-9]*
 | '.' [0-9]+
 ;
NOME: [a-zA-Z_] [a-zA-Z_0-9]*;
SPACE: [ \t\r\n] -> skip;