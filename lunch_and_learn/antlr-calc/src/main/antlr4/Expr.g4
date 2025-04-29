grammar Expr;

// A regra principal
expr: expr op=('*'|'/') expr     # MulDiv
    | expr op=('+'|'-') expr     # AddSub
    | INT                        # Int
    | '(' expr ')'               # Parens
    ;

// Tokens
INT : [0-9]+ ;
WS : [ \t\r\n]+ -> skip ;