import com.dojo.pascal.PascalTriangle;
import org.junit.Assert;
import org.junit.Test;

public class PascalTriangleTest {
    /*
     * 1 - Receive row values
     * 2 - loop for each row
     * 3 - calc and save row value in an array
     *
     * 4 - return array
     * */

    @Test
    public void checkPascalTriangleTest(){

        int [][] result = PascalTriangle.pascalTriangle(1);
        int [][] expected = new int[1][];
        expected[0] = new int[1];
        expected[0][0]=1;

        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void checkPascalTriangleThreeTest() {
        int [][] result = PascalTriangle.pascalTriangle(3);
        int [][] expected = new int[3][];
        expected[0] = new int[1];
        expected[1] = new int[2];
        expected[2] = new int[3];
        expected[0][0] = 1;
        expected[1][0] = 1;
        expected[1][1] = 1;
        expected[2][0] = 1;
        expected[2][1] = 2;
        expected[2][2] = 1;
        Assert.assertArrayEquals(expected, result);
    }


    @Test
    public void checkPascalTriangleFourTest() {
        int [][] result = PascalTriangle.pascalTriangle(4);
        int [][] expected = new int[4][];
        expected[0] = new int[1];
        expected[1] = new int[2];
        expected[2] = new int[3];
        expected[3] = new int[4];
        expected[0][0] = 1;

        expected[1][0] = 1;
        expected[1][1] = 1;

        expected[2][0] = 1;
        expected[2][1] = 2;
        expected[2][2] = 1;

        expected[3][0] = 1;
        expected[3][1] = 3;
        expected[3][2] = 3;
        expected[3][3] = 1;
        Assert.assertArrayEquals(expected, result);
    }



}
