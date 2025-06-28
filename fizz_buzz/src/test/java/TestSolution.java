import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSolution {

    @Test
    public void test1(){
        String result = Solution.solve(1);
        assertEquals("1",result);
    }

    @Test
    public void test2() {
        String result = Solution.solve(2);
        assertEquals("2", result);
    }

    @Test
    public void test3(){
        String result = Solution.solve(3);
        assertEquals("fizz",result);
    }

    @Test
    public void test4(){
        String result = Solution.solve(4);
        assertEquals("4",result);
    }

    @Test
    public void testMod3(){
        Boolean result = Solution.mod(3, 3);
        assertEquals(true, result);
    }

    @Test
    public void testMod5(){
        Boolean result = Solution.mod(5, 5);
        assertEquals(true, result);
    }

    @Test
    public void testBuzz(){
        String result = Solution.solve(5);
        assertEquals("buzz", result);
    }
}
