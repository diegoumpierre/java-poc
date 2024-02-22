class DifferenceOfSquaresCalculator {

    int computeSquareOfSumTo(int input) {
        int sum = 0;
        for (int i=0; i <= input;i++){
            sum +=i;
        }
        return (int) Math.pow(sum,2);
    }

    int computeSumOfSquaresTo(int input) {

        int sum = 0;
        for (int i=0; i <= input;i++){
            sum +=(int) Math.pow(i,2);
        }
        return sum;
    }

    int computeDifferenceOfSquares(int input) {
        return (computeSquareOfSumTo(input) - computeSumOfSquaresTo(input));
    }

}