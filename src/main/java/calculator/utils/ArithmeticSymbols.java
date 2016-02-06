package calculator.utils;

/**
 * Enum for arithmetic symbols
 * 1. Arithmetic Functions: add, sub, multi, div
 * 2. Parenthesis: ( )
 * 3. Comma: ,
 * 4. "let" operator
 * 5. Negative Sign for integers: -
 */
public enum ArithmeticSymbols {

    ADD_FUNCTION("add"),
    SUB_FUNCTION("sub"),
    MULTI_FUNCTION("multi"),
    DIV_FUNCTION("div"),
    LET_OPERATOR("let"),
    OPEN_PARENTHESIS("("),
    CLOSE_PARENTHESIS(")"),
    COMMA(","),
    NEGATIVE_SIGN("-");

    private final String symbolName;

    /**
     * Constructor
     * @param symbolName
     */
    private ArithmeticSymbols(String symbolName) {
        this.symbolName = symbolName;
    }

    /**
     * Method to check symbol name equality
     * @param nameToCheck
     * @return
     */
    public boolean equalsName(String nameToCheck) {
        String funcNameToCheckLowerCase = nameToCheck.toLowerCase();
        return symbolName.equals(funcNameToCheckLowerCase);
    }

    /**
     * Returns string value for Enum
     * @return
     */
    public String toString() {
        return this.symbolName;
    }

    /**
     * Check if given function name is Arithmetic Function
     * @param funcNameToCheck
     * @return
     */
    public static boolean isArithmeticFunction(String funcNameToCheck) {
        if(ADD_FUNCTION.equalsName(funcNameToCheck))
            return true;
        else if(SUB_FUNCTION.equalsName(funcNameToCheck))
            return true;
        else if(MULTI_FUNCTION.equalsName(funcNameToCheck))
            return true;
        else if(DIV_FUNCTION.equalsName(funcNameToCheck))
            return true;
        else
            return false;
    }

    /**
     * Check if Add Function
     * @param funcNameToCheck
     * @return
     */
    public static boolean isAddFunction(String funcNameToCheck){
        return ADD_FUNCTION.equalsName(funcNameToCheck);
    }

    /**
     * Check Subtract Function
     * @param funcNameToCheck
     * @return
     */
    public static boolean isSubFunction(String funcNameToCheck){
        return SUB_FUNCTION.equalsName(funcNameToCheck);
    }

    /**
     * Check Multiply Function
     * @param funcNameToCheck
     * @return
     */
    public static boolean isMultiFunction(String funcNameToCheck){
        return MULTI_FUNCTION.equalsName(funcNameToCheck);
    }

    /**
     * Check Divide Function
     * @param funcNameToCheck
     * @return
     */
    public static boolean isDivFunction(String funcNameToCheck){
        return DIV_FUNCTION.equalsName(funcNameToCheck);
    }

    /**
     * Check if given expression contains Arithmetic Function
     * @param expression
     * @return
     */
    public static boolean checkExpressionContainsArithmeticFunction(String expression){
        String exprLowerCase = expression.toLowerCase();
        if(exprLowerCase.indexOf(ADD_FUNCTION.toString()) != -1)
            return true;
        else if(exprLowerCase.indexOf(SUB_FUNCTION.toString()) != -1)
            return true;
        else if(exprLowerCase.indexOf(MULTI_FUNCTION.toString()) != -1)
            return true;
        else if(exprLowerCase.indexOf(DIV_FUNCTION.toString()) != -1)
            return true;
        else return false;
    }

    /**
     * Check if given string is Open Parenthesis
     * @param parenthesisStr
     * @return
     */
    public static boolean isOpenParenthesis(String parenthesisStr){
        return OPEN_PARENTHESIS.equalsName(parenthesisStr);
    }

    /**
     * Check if given string is Close Parenthesis
     * @param parenthesisStr
     * @return
     */
    public static boolean isCloseParenthesis(String parenthesisStr){
        return CLOSE_PARENTHESIS.equalsName(parenthesisStr);
    }

    /**
     * Check is given string is Negative Sign
     * @param negativeSignStr
     * @return
     */
    public static boolean isNegativeSign(String negativeSignStr){
        return NEGATIVE_SIGN.equalsName(negativeSignStr);
    }

    /**
     * Check if given string is Comma
     * @param commaStr
     * @return
     */
    public static boolean isComma(String commaStr){
        return COMMA.equalsName(commaStr);
    }

    /**
     * Check if given string is "let" operator
     * @param letOperatorStr
     * @return
     */
    public static boolean isLetOperator(String letOperatorStr){
        return LET_OPERATOR.equalsName(letOperatorStr.toLowerCase());
    }

    /**
     * Method to check given character is Parenthesis or Comma
     * @param character
     * @return
     */
    public static boolean isParenthesisOrComma(char character){
        String chString = String.valueOf(character);
        if(isOpenParenthesis(chString) || isCloseParenthesis(chString) || isComma(chString))
            return true;
        return false;
    }


}
