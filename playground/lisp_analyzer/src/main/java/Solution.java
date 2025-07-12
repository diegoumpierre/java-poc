import java.util.Stack;

public class Solution {
    public String solve(String code) {
        var bufferLine = 1;
        var bufferColumn = 0;
        var stack = new Stack<Character>();
        for(var i = 0; i < code.length(); i++) {
            var character = code.charAt(i);
            if (character == '(' || character == '[') {
                stack.add(character);
            }
            if (character == ']') {
                var lastChar = stack.pop();
                if (lastChar != '[') {
                    return "line " + bufferLine + " column " + bufferColumn;
                }
            }
            if(character == ')') {
                var lastChar = stack.pop();
                if (lastChar != '(') {
                    return "line " + bufferLine + " column " + bufferColumn;
                }
            }
            if(character == '\n') {
                bufferLine++;
                bufferColumn = 0;
            } else {
                bufferColumn ++;
            }
        }
        if (!stack.isEmpty()) {
            return "line " + bufferLine + " column " + bufferColumn;
        }
        return "ok";
    }
}
