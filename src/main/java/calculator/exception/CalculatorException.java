package calculator.exception;

/**
 * Custom Exception Class for Calculator
 */
public class CalculatorException extends Exception {

    /**
     * Error messages for Calculator Exception
     */
    public static final String INPUT_EXPRESSION_MISSING = "Input expression to be evaluated is either missing or empty";
    public static final String INPUT_EXPRESSION_INVALID = "Input expression is not valid";
    public static final String PARENTHESIS_MISMATCH = "Parenthesis mismatch";
    public static final String INVALID_ARGUMENTS = "Invalid/Missing arguments for let operator or arithmetic function";
    public static final String ERROR_ENCOUNTERED_IN_CALCULATION = "Error encountered in calculation";

    /**
     * Constructor
     * @param message
     */
    public CalculatorException(String message) {
        super(message);
    }
}
