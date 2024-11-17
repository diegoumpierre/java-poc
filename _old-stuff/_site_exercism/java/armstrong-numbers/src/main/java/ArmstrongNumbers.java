class ArmstrongNumbers {

    boolean isArmstrongNumber(int numberToCheck) {
        int result = 0;
        String numberToCheckString = Integer.toString(numberToCheck);

        if (numberToCheckString.length() == 1) return true;

        for (int i=0;i<numberToCheckString.length();i++){
            if (Character.isDigit(numberToCheckString.charAt(i))) {
                int digit = Character.getNumericValue(numberToCheckString.charAt(i));
                result += (Math.pow(digit,numberToCheckString.length()));
            }
        }
        return result == numberToCheck;
    }
}