import java.math.BigInteger;

class Grains {

    BigInteger grainsOnSquare(final int square) {
        if (square < 1 || square >64)
            throw new IllegalArgumentException("square must be between 1 and 64");

        BigInteger totalGrains = BigInteger.ZERO;
        for (int i=1;i<=square;i++){
            if (totalGrains.compareTo(BigInteger.ZERO) != 0)
                totalGrains = totalGrains.multiply(BigInteger.valueOf(2));
            else
                totalGrains = totalGrains.add(BigInteger.ONE);
        }
        return totalGrains;
    }

    BigInteger grainsOnBoard() {
        BigInteger totalGrainsOnBoard = BigInteger.ZERO;
        for (int i=1;i<65;i++){
            totalGrainsOnBoard = totalGrainsOnBoard.add(grainsOnSquare(i));
        }
        return totalGrainsOnBoard;
    }

}
