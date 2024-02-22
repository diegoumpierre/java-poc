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
/*
The square of the sum of the first ten natural numbers is
(1 + 2 + ... + 10)² = 55² = 3025.

The sum of the squares of the first ten natural numbers is
1² + 2² + ... + 10² = 385.

Hence the difference between the square of the sum of the first ten natural numbers and the sum of the
squares of the first ten natural numbers is 3025 - 385 = 2640.

 */