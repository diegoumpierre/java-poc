// ANTLR4 grammar for a simple subset of SQL (SELECT statements)

grammar Sql;

// Parser rules
sql
    : selectStmt EOF
    ;

selectStmt
    : SELECT selectElements FROM tableName (WHERE whereClause)?
    ;

selectElements
    : STAR
    | columnName (COMMA columnName)*
    ;

whereClause
    : condition (AND condition)*
    ;

condition
    : columnName comparator value
    ;

comparator
    : EQUALS
    | NOTEQ
    | LT
    | GT
    | LE
    | GE
    ;

columnName
    : IDENTIFIER
    ;

tableName
    : IDENTIFIER
    ;

value
    : STRING
    | NUMBER
    ;

// Lexer rules
SELECT      : 'SELECT';
FROM        : 'FROM';
WHERE       : 'WHERE';
AND         : 'AND';
STAR        : '*';
COMMA       : ',';
EQUALS      : '=';
NOTEQ       : '!=' | '<>';
LT          : '<';
GT          : '>';
LE          : '<=';
GE          : '>=';
IDENTIFIER  : [a-zA-Z_][a-zA-Z0-9_]*;
STRING      : '\'' (~['\r\n])* '\'';
NUMBER      : [0-9]+ ('.' [0-9]+)?;
WS          : [ \t\r\n]+ -> skip;