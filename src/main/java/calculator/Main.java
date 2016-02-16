package calculator;

import calculator.exception.CalculatorException;
import calculator.utils.ArithmeticSymbols;
import calculator.utils.Helper;
import org.apache.log4j.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Calculator class with list and stack implementation
 * Input expression string is first stored in an ArrayList of Strings
 * Then evaluated using Stack
 */
public class Main {

    /**
     * Implementing Logging feature
     * Assumption: Default Level is OFF
     * Assumption: Accepts level name from command line in case-insensitive format
     * Assumption: Accepts only 3 levels DEBUG, INFO, and ERROR
     * Assumption: No exception thrown for invalid level name in command line.
     */
    private static final Logger LOGGER = Logger.getLogger(Main.class);
    private static final Level DEFAULT_LEVEL = Level.OFF;

    /**
     * Stores inputExpressionString as a List
     * Expression can be one of the following:
     * 1. Numbers : Integer (Range: Integer.MIN_VALUE to Integer.MAX_VALUE)
     * 2. Variables : String of characters (Range: a-z, A-Z)
     * 3. Arithmetic Functions: add, sub, multi, div
     * 4. "let" operator
     * List also includes following entries
     * 5. Parenthesis open, "(" and close, ")"
     * 6. Comma ","
     *
     * Assumption: Any characters besides the ones mentioned above are considered invalid.
     * Assumption: Any arbitrary expression, variable name or variable value are not surrounded by parenthesis
     * Example: (a), (5), (add(5,5))
     * Assumption: The program only works with () parenthesis and no other parenthesis
     */
    private ArrayList<String> inputExpressionList = new ArrayList<String>();
    /**
     * The input expression stack
     */
    private Stack<String> inputExpressionStack = new Stack<String>();

    /**
     * Default Constructor
     */
    public Main(){
        LOGGER.setLevel(DEFAULT_LEVEL);
    }

    public static void main(String[] args) throws CalculatorException {
        LOGGER.debug("In main method, printing arguments..." + Arrays.toString(args));
        //Initialise main
        Main main = new Main();

        //Check command line arguments not empty
        LOGGER.info("Validating command line args....");
        if(args == null || args.length == 0) {
            main.throwCalculatorException(CalculatorException.INPUT_EXPRESSION_MISSING);
        }

        //Extract input values from command line args
        String inputExprStr = null;
        String inputLoggerLevel = null;
        for(int i = 0; i < args.length; i++){
            if(i == 0){ //Required input : Expression to calculate
                inputExprStr = args[i];
            }
            if(i == 1) { //Optional input : Logger Level
                inputLoggerLevel = args[i];
            }
        }

        LOGGER.info("Setting logging level....");
        //Set Logger Verbose Level
        main.setLoggingLevel(inputLoggerLevel);

        LOGGER.info("Computing input expression....");
        //Calculate expression
        String outputResult = main.calculate(inputExprStr);
        if(Helper.isNullOrEmptyString(outputResult) || !Helper.isInteger(outputResult))
            main.throwCalculatorException(CalculatorException.ERROR_ENCOUNTERED_IN_CALCULATION);

        LOGGER.debug("In main method, computing complete with result:" + outputResult + ".");
        LOGGER.info("Computing complete with result:" + outputResult + ".");
        //Print output to console
        System.out.println(outputResult);
    }

    /**
     * Setter method for Logger Level
     * @param inputLoggerLevel
     */
    public void setLoggingLevel(String inputLoggerLevel){
        Level logLevel = DEFAULT_LEVEL;
        //Convert string logger level to org.apache.log4j.Level
        if(!Helper.isNullOrEmptyString(inputLoggerLevel)) {
            String s = inputLoggerLevel.toUpperCase();
            logLevel = s.equals("DEBUG") ? Level.DEBUG :(s.equals("INFO") ? Level.INFO :( s.equals("ERROR") ? Level.ERROR :DEFAULT_LEVEL));
        }

        Logger.getRootLogger().getLoggerRepository().resetConfiguration();

        ConsoleAppender console = new ConsoleAppender(); //create appender
        //configure the appender
        String PATTERN = "%d [%p|%c|%C{1}] %m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(logLevel);
        console.activateOptions();
        Logger.getRootLogger().addAppender(console);

        FileAppender fa = new FileAppender();
        fa.setName("FileLogger");
        fa.setFile("calculator.log");
        fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        fa.setThreshold(logLevel);
        fa.setAppend(true);
        fa.activateOptions();
        Logger.getRootLogger().addAppender(fa);

        LOGGER.debug("Setting log level..." + logLevel);
    }

    /**
     * Method to evaluate input expression
     * @param inputExprStr
     * @return
     */
    public String calculate(String inputExprStr) throws CalculatorException {
        LOGGER.debug("In calculate method, printing argument..." + inputExprStr);
        String result = null;

        //Step 1: Check expression input string is valid
        if(isValidInputExprString(inputExprStr)) {
            //Step 2: Convert inputExpression String to ArrayList<String>
            buildInputExpressionList(inputExprStr);

            //Step 3: Evaluate let Operator in inputExpressionList using Stack
            if(inputExpressionList != null && inputExpressionList.size() > 0)
                processLetOperatorInInputExpressionList();

            //Step 4: Evaluate arithmetic functions in inputExpressionList using stack
            if(inputExpressionList != null && inputExpressionList.size() > 0)
                result = processArithmeticFunctionInInputExpressionList();
        }

        LOGGER.debug("In calculate method, printing result..." + result);
        return result;
    }


    /**
     * Validates input string from command line argument.
     * @param inputExprStr
     * @return
     */
    private boolean isValidInputExprString(String inputExprStr) throws CalculatorException {
        LOGGER.debug("In isValidInputExprString method...");
        //1. Check not empty / null
        if(Helper.isNullOrEmptyString(inputExprStr))
            throwCalculatorException(CalculatorException.INPUT_EXPRESSION_MISSING);
        //2. Check valid expression should contain at least one of the arithmetic expression
        if(!ArithmeticSymbols.checkExpressionContainsArithmeticFunction(inputExprStr))
            throwCalculatorException((CalculatorException.INPUT_EXPRESSION_INVALID));
        return true;
    }

    /**
     * Function that parses through input expression string
     * and stores each expression and parenthesis and comma as an entry in an input expression list
     * @param inputExprStr
     */
    private void buildInputExpressionList(String inputExprStr) throws CalculatorException {
        LOGGER.debug("In buildInputExpressionList method, printing arguments..." + inputExprStr);
        //Remove all whitespaces from input expression
        inputExprStr = inputExprStr.toLowerCase().replaceAll("\\s","");

        int exprLength = inputExprStr.length();
        StringBuilder arbExpression = new StringBuilder();
        int parenthesisCount = 0;
        for (int i = 0; i < exprLength ; i++) {
            char exprChar = inputExprStr.charAt(i);
            String tempString = String.valueOf(exprChar);
            if (ArithmeticSymbols.isParenthesisOrComma(exprChar)) {
                String arbExpressionStr = arbExpression.toString();
                if (!Helper.isNullOrEmptyString(arbExpressionStr)) {
                    if(!isValidArbitraryExpression(arbExpressionStr)){
                        throwCalculatorException(CalculatorException.INPUT_EXPRESSION_INVALID);
                    }
                    inputExpressionList.add(arbExpressionStr);
                    arbExpression.delete(0, arbExpression.length());
                }
                parenthesisCount = updateParenthesesCount(tempString, parenthesisCount);
                inputExpressionList.add(tempString);
            } else if (Helper.isLetterOrDigit(exprChar) || ArithmeticSymbols.isNegativeSign(tempString)) {
                arbExpression.append(exprChar);
            } else {
                throwCalculatorException(CalculatorException.INPUT_EXPRESSION_INVALID);
            }
        }
        if(parenthesisCount != 0)
            throwCalculatorException(CalculatorException.PARENTHESIS_MISMATCH);
        LOGGER.debug("In buildInputExpressionList method, printing result..." + inputExpressionList.toString());
    }

    /**
     * Function to check ValueExpression is valid
     * Expression can be valid only if it is one of the following:
     * 1. Numbers : Integer (Range: Integer.MIN_VALUE to Integer.MAX_VALUE)
     * 2. Variables : String of characters (Range: a-z, A-Z)
     * 3. Arithmetic Functions: add, sub, multi, div
     * 4. "let" operator
     * Examples of invalid combinations: 5a, a5
     * @param arbExpression
     * @return
     */
    private boolean isValidArbitraryExpression(String arbExpression) {
        LOGGER.debug("In isValidArbitraryExpression method, printing argument..." + arbExpression);
        //Check if arbitrary expression is arithmetic function
        if(ArithmeticSymbols.isArithmeticFunction(arbExpression))
            return true;
        else if(ArithmeticSymbols.isLetOperator(arbExpression))
            return true;
        else {
            //Check if arbitrary expression contains digit [0-9]
            boolean containsDigit = arbExpression.matches(".*\\d+.*");
            if (containsDigit) {
                //If contains true, then check arbitrary expression is a valid integer
                //Example: 55 is valid
                //Example: 5a or a5 is invalid
                return Helper.isInteger(arbExpression);
            } else {
                //Example: aaa is valid
                //If contains false, then arbitrary expression valid string
                return true;
            }
        }
    }

    /**
     * Method to evaluate "let" operator
     * @throws calculator.exception.CalculatorException
     */
    private void processLetOperatorInInputExpressionList() throws CalculatorException {
        LOGGER.debug("In processLetOperatorInInputExpressionList method, printing inputExpressionList..." + inputExpressionList.toString());
        while(inputExpressionList.contains(ArithmeticSymbols.LET_OPERATOR.toString())) {
            for (int i = (inputExpressionList.size() - 1); i >= 0; i--) {
                String currentExpression = inputExpressionList.remove(i);
                if (ArithmeticSymbols.isLetOperator(currentExpression)) {
                    String letOpeningBrace = inputExpressionStack.pop();
                    if(!ArithmeticSymbols.isOpenParenthesis(letOpeningBrace))
                        throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);
                    String variableName = inputExpressionStack.pop();
                    String letCommaSeparator1 = inputExpressionStack.pop();
                    if(!ArithmeticSymbols.isComma(letCommaSeparator1))
                        throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);
                    List<String> variableExpression = getExpressionListFromStackWithParenthesis();
                    String letCommaSeparator2 = inputExpressionStack.pop();
                    if(!ArithmeticSymbols.isComma(letCommaSeparator2))
                        throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);
                    List<String> expressionToReplace = getExpressionListFromStackWithParenthesis();
                    String letClosingBrace = inputExpressionStack.pop();
                    if(!ArithmeticSymbols.isCloseParenthesis(letClosingBrace))
                        throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);
                    while (expressionToReplace.indexOf(variableName) != -1) {
                        int variableNameIndex = expressionToReplace.indexOf(variableName);
                        expressionToReplace.remove(variableNameIndex);
                        expressionToReplace.addAll(variableNameIndex, variableExpression);
                    }
                    inputExpressionList.addAll(expressionToReplace);
                    while (!inputExpressionStack.isEmpty()) {
                        inputExpressionList.add(inputExpressionStack.pop());
                    }
                    break;
                } else {
                    inputExpressionStack.push(currentExpression);
                }
            }
        }
        if(inputExpressionList == null || inputExpressionList.isEmpty()) {
            inputExpressionList = new ArrayList<String>();
            while(!inputExpressionStack.isEmpty()){
                inputExpressionList.add(inputExpressionStack.pop());
            }
        }
        LOGGER.debug("In processLetOperatorInInputExpressionList method, printing result inputExpressionList..." + inputExpressionList.toString());
    }

    /**
     * Method to pop nested expression from Stack with parenthesis
     * @return
     * @throws calculator.exception.CalculatorException
     */
    private List getExpressionListFromStackWithParenthesis() throws CalculatorException {
        LOGGER.debug("In getExpressionListFromStackWithParenthesis method...");
        List<String> expressionList = new ArrayList<String>();
        String tempExpression = inputExpressionStack.pop();
        expressionList.add(tempExpression);
        if (ArithmeticSymbols.isArithmeticFunction(tempExpression) || ArithmeticSymbols.isLetOperator(tempExpression)) {
            expressionList.add(inputExpressionStack.pop());
            int openBracketCount = 1;
            while (openBracketCount != 0) {
                if (inputExpressionStack.isEmpty() && openBracketCount != 0) {
                    throwCalculatorException(CalculatorException.PARENTHESIS_MISMATCH);
                }
                String temp = inputExpressionStack.pop();
                openBracketCount = updateParenthesesCount(temp, openBracketCount);
                expressionList.add(temp);
            }
        }
        LOGGER.debug("In getExpressionListFromStackWithParenthesis method, printing result..." + expressionList.toString());
        return expressionList;
    }

    /**
     * Method to evaluate Arithmetic Function
     * @return
     * @throws calculator.exception.CalculatorException
     */
    private String processArithmeticFunctionInInputExpressionList() throws CalculatorException {
        LOGGER.debug("In processArithmeticFunctionInInputExpressionList method, printing inputExpressionList..." + inputExpressionList.toString());
        for(int i = (inputExpressionList.size()-1); i >= 0 ; i-- ){
            String currentExpression = inputExpressionList.remove(i);
            if(ArithmeticSymbols.isArithmeticFunction(currentExpression)) {
                String arithFuncOpeningBrace = inputExpressionStack.pop();
                if(!ArithmeticSymbols.isOpenParenthesis(arithFuncOpeningBrace))
                    throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);
                Integer integer1 = null;
                try {
                    integer1 = Integer.parseInt(inputExpressionStack.pop());
                }catch(NumberFormatException e){
                    LOGGER.error(e.getMessage());
                    throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);
                }
                String arithFuncCommaSeparator = inputExpressionStack.pop();
                if(!ArithmeticSymbols.isComma(arithFuncCommaSeparator))
                    throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);
                Integer integer2 = null;
                try {
                    integer2 = Integer.parseInt(inputExpressionStack.pop());
                }catch(NumberFormatException e){
                    LOGGER.error(e.getMessage());
                    throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);
                }
                String arithFuncClosingBrace = inputExpressionStack.pop();
                if(!ArithmeticSymbols.isCloseParenthesis(arithFuncClosingBrace))
                    throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);
                Integer result = null;
                result = compute(currentExpression, integer1, integer2);
                if(result == null)
                    throwCalculatorException(CalculatorException.ERROR_ENCOUNTERED_IN_CALCULATION);
                inputExpressionStack.push(result.toString());
            } else {
                inputExpressionStack.push(currentExpression);
            }
        }
        String result = null;
        if(inputExpressionStack.size() == 1) {
            result = inputExpressionStack.pop();
        }
        LOGGER.debug("In processArithmeticFunctionInInputExpressionList method, printing result..." + result);
        return result;
    }

    /**
     * Compute  result for Arithmetic Function given input Arguments
     * @param arithmeticFunctionName
     * @param integerOp1
     * @param integerOp2
     * @return
     */
    private Integer compute(String arithmeticFunctionName, Integer integerOp1, Integer integerOp2 ){
        LOGGER.debug("In compute method, printing args... function name:" + arithmeticFunctionName + ", intergerOperand1: " + integerOp1 + ", integerOperand2: "+ integerOp2);
        Integer result = null;
        if(ArithmeticSymbols.ADD_FUNCTION.equalsName(arithmeticFunctionName)){
            result = integerOp1 + integerOp2;
        } else if(ArithmeticSymbols.SUB_FUNCTION.equalsName(arithmeticFunctionName)){
            result = integerOp1 - integerOp2;
        } else if(ArithmeticSymbols.MULTI_FUNCTION.equalsName(arithmeticFunctionName)){
            result = integerOp1 * integerOp2;
        } else if(ArithmeticSymbols.DIV_FUNCTION.equalsName(arithmeticFunctionName)){
            result = integerOp1 / integerOp2;
        }
        LOGGER.debug("In compute method, printing result:" + result);
        return result;
    }

    /**
     * Method to update parenthesis count
     * If opening brace, increment count
     * If none of the above, no change in count
     * @param parenthesesStr
     * @param parenthesesCount
     * @return
     */
    private Integer updateParenthesesCount(String parenthesesStr, Integer parenthesesCount){
        LOGGER.debug("In updateParenthesesCount method, printing args... input string:" + parenthesesStr + ", parenthesis count: " + parenthesesCount );
        if(ArithmeticSymbols.isOpenParenthesis(parenthesesStr))
            parenthesesCount++;
        if(ArithmeticSymbols.isCloseParenthesis(parenthesesStr))
            parenthesesCount--;
        LOGGER.debug("In updateParenthesesCount method, printing result... parenthesis count: " + parenthesesCount );
        return parenthesesCount;
    }

    /**
     * Method to throw CalculatorException and log error message.
     * @param message
     * @throws calculator.exception.CalculatorException
     */
    private void throwCalculatorException(String message) throws CalculatorException {
        LOGGER.error(message);
        throw new CalculatorException(message);
    }

}
