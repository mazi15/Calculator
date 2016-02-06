package calculator.utils;

/**
 * Helper method for common util functions
 */
public class Helper {
    /**
     * Evaluate given string is null or empty
     * @param stringToCheck
     * @return
     */
    public static boolean isNullOrEmptyString(String stringToCheck) {
        return stringToCheck == null || stringToCheck.isEmpty();
    }

    /**
     * Method to check given character is Digit[0-9]
     * @param character
     * @return
     */
    public static boolean isDigit(char character){
        return  Character.isDigit(character);
    }

    /**
     * Method to check given character is Letter[a-z,A-Z]
     * @param character
     * @return
     */
    public static boolean isLetter(char character){
        return Character.isLetter(character);
    }

    /**
     * Method to check given character is Letter[a-z,A-Z] or Digit[0-9]
     * @param character
     * @return
     */
    public static boolean isLetterOrDigit(char character){
        return Character.isLetterOrDigit(character);
    }


    /**
     * Function to check if String is valid integer
     * @param inputStr
     * @return
     */
    public static boolean isInteger(String inputStr){
        if(isNullOrEmptyString(inputStr))
            return false;
        for(int i = 0 ; i < inputStr.length() ; i++){
            Character inputCh = inputStr.charAt(i);
            if(i==0 && ArithmeticSymbols.isNegativeSign(inputCh.toString())){
                if(inputStr.length() == 1)
                    return false;
                else
                    continue;
            }
            if(!isDigit(inputCh))
                return false;
        }
        return true;

    }

}
