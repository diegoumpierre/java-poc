grammar Lab;

start : decliration|if|ifelse;
ifelse : 'if' '(' condition ')' '{' decliration* '}' 'else' '{' decliration* '}';
if : 'if' '(' condition ')' '{' decliration* '}';
condition : (double_number|NUM|VAR) RelationalOperators (double_number|NUM|VAR);
decliration : int|double|multiDecliration;
int : INT VAR '=' (NUM|VAR) (Operation (NUM|VAR))*SEMICOLON;
double: DOUBLE VAR '=' (double_number|NUM|VAR) (Operation (double_number|NUM|VAR))* SEMICOLON;
multiDecliration: (INT|DOUBLE) VAR (',' VAR)* SEMICOLON;
WhiteSpace : (' '|'\n'| '\r'|'\t')+ -> skip;
INT : 'int';
DOUBLE : 'double';
VAR : ('a'..'z'|'A'..'Z')*;
NUM : ('0'..'9')*;
double_number : NUM '.' NUM;
SEMICOLON : ';';
Operation : '/'|'*'|'+'|'-';
RelationalOperators : '=='|'!='|'>'|'<'|'>='|'<=';