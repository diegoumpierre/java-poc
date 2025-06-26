grammar Hello;

greeting : 'hello' NAME '!' ;
NAME  : [a-zA-Z]+ ;
WS  : [ \t\r\n]+ -> skip ;