import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest {

    @Test
    public void testFailureMissingParanteses() {
        Solution solution = new Solution();
        var text = "(testing";
        String output = solution.solve(text);
        assertEquals("line 1 column 8", output);
    }

    @Test
    public void testFailureMissingParantesesSecondLine() {
        Solution solution = new Solution();
        var text2 = """
                (testing
                    (test)""";
        assertEquals("line 2 column 10", solution.solve(text2));
    }

    @Test
    public void testFailureMissingParantesesSecondLineColumn() {
        Solution solution = new Solution();
        var textSquareBrackets = """
                (testing
                    (test[ ))""";
        assertEquals("line 2 column 11", solution.solve(textSquareBrackets));
    }


    @Test
    public void testOk(){
        var text = "(testing)";
        var solution = new Solution();
        var output = solution.solve(text);
        assertEquals("ok", output);
    }
}
