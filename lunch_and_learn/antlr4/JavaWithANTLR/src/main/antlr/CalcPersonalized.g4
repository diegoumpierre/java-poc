grammar CalcPersonalized;

prog:   stat+ ;

stat:   ID '=' expr    #Assign
    |   expr           #ExprOnly
    ;

expr:   expr '+' expr  #AddExpr
    |   expr '*' expr  #MulExpr
    |   expr '$$' expr  #Umpierre
    |   INT            #IntExpr
    |   ID             #VarExpr
    |   '(' expr ')'   #ParenExpr
    ;

ID  :   [a-zA-Z]+ ;
INT :   [0-9]+ ;
WS  :   [ \t\r\n]+ -> skip ;

//"x = 4 + 5 \n y = x * 2 \n z = x $$ y \n z"