package calculator;

import calculator.exception.CalculatorException;
import calculator.utils.ArithmeticSymbols;
import calculator.utils.Helper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Calculator class with tree implementation
 */
public class MainTree {

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
     * Stores inputExpressionString as a Tree
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
    private ExpressionTree inputExpressionTree;

    /**
     * Default Constructor
     */
    public MainTree(){
        LOGGER.setLevel(DEFAULT_LEVEL);
    }

    public static void main(String[] args) throws CalculatorException {
        LOGGER.debug("In main method, printing arguments..." + Arrays.toString(args));
        //Initialise main
        MainTree mainTree = new MainTree();

        //Check command line arguments not empty
        LOGGER.info("Validating command line args....");
        if(args == null || args.length == 0) {
            mainTree.throwCalculatorException(CalculatorException.INPUT_EXPRESSION_MISSING);
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
        mainTree.setLoggingLevel(inputLoggerLevel);

        LOGGER.info("Computing input expression....");
        //Calculate expression
        String outputResult = mainTree.calculate(inputExprStr);
        if(Helper.isNullOrEmptyString(outputResult))
            mainTree.throwCalculatorException(CalculatorException.ERROR_ENCOUNTERED_IN_CALCULATION);

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
        LOGGER.setLevel(logLevel);
        LOGGER.debug("Setting log level..." + logLevel);
    }

    /**
     * Method to evaluate input expression
     * @param inputExprStr
     * @return
     */
    public String calculate(String inputExprStr) throws CalculatorException {
        LOGGER.debug("In calculate method, printing argument..." + inputExprStr);
        Integer result = null;
        //Reset Expression Tree
        inputExpressionTree = null;

        //Step 1: Check expression input string is valid
        if(isValidInputExprString(inputExprStr)) {
            //Step 2: Convert inputExpression String to Tree
            buildInputExpressionTree(inputExpressionTree, inputExprStr);
            LOGGER.debug("Post call to buildInputExpressionTree method, printing result..." + inputExpressionTree.toString());

            //Step 3: Evaluate let Operator in inputExpressionList using Stack
            while(inputExpressionTree != null && !inputExpressionTree.isEmpty() && isValidExpressionTree(inputExpressionTree)) {
                inputExpressionTree = processInputExpressionTree(inputExpressionTree);
                if(Helper.isInteger(inputExpressionTree.getExpression()))
                    break;
            }
            LOGGER.debug("Post call to processInputExpressionTree method, printing result..." + inputExpressionTree.toString());
        }

        try{
            result = Integer.parseInt(inputExpressionTree.getExpression());
        }catch(NumberFormatException e){
            LOGGER.error(e.getMessage());
            throwCalculatorException(CalculatorException.ERROR_ENCOUNTERED_IN_CALCULATION);
        }
        LOGGER.debug("In calculate method, printing result..." + result);
        return result.toString();
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
     * Recursive function to check expression tree is valid
     * @param expressionTree
     * @return
     */
    private boolean isValidExpressionTree(ExpressionTree expressionTree) throws CalculatorException {
        LOGGER.debug("In isValidExpressionTree method, printing argument..." + expressionTree.toString());
        String expression = expressionTree.getExpression();
        if(!expressionTree.isLeafNode()) {
            List<ExpressionTree> children = expressionTree.getChildren();
            //1. Parenthesis Mismatch
            if(!ArithmeticSymbols.isOpenParenthesis(children.get(0).getExpression())
                    || !ArithmeticSymbols.isCloseParenthesis(children.get(children.size() - 1).getExpression()))
                throwCalculatorException(CalculatorException.PARENTHESIS_MISMATCH);
            if (ArithmeticSymbols.isArithmeticFunction(expression)) {
                //2. Invalid Arguments
                if(children.size() != 5 || !ArithmeticSymbols.isComma(children.get(2).getExpression()))
                    throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);

            } else if (ArithmeticSymbols.isLetOperator(expression)) {
                //2. Invalid Arguments
                if(children.size() != 7 || !ArithmeticSymbols.isComma(children.get(2).getExpression()) ||
                        !ArithmeticSymbols.isComma(children.get(4).getExpression()))
                    throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);

            }
            for(ExpressionTree child: children) {
                if(!isValidExpressionTree(child)){
                    return false;
                }
            }
        } else {
            if(ArithmeticSymbols.isOpenParenthesis(expression) ||
                    ArithmeticSymbols.isCloseParenthesis(expression) ||
                    ArithmeticSymbols.isComma(expression))
                return true;
            else
                return isValidVariableNameOrValue(expression);
        }
        return true;
    }


    /**
     * Recursive function that parses through input expression string
     * and stores each expression and parenthesis and comma as an entry in an input expression tree
     * @param inputExprStr
     */
    private String buildInputExpressionTree(ExpressionTree expressionTree, String inputExprStr) throws CalculatorException {
        LOGGER.debug("In buildInputExpressionTree method, printing arguments..." + inputExprStr);
        //Remove all whitespaces from input expression
        inputExprStr = inputExprStr.toLowerCase().replaceAll("\\s","");

        StringBuilder arbExpression = new StringBuilder();
        int i= 0;
        while (i < inputExprStr.length()) {
            char exprChar = inputExprStr.charAt(i);
            String tempString = String.valueOf(exprChar);
            if (Helper.isLetterOrDigit(exprChar) || ArithmeticSymbols.isNegativeSign(tempString)) {
                arbExpression.append(exprChar);
                String arbExpressionStr = arbExpression.toString();
                if (!Helper.isNullOrEmptyString(arbExpressionStr)) {
                    //If next character is not a letter or digit
                    if (!Helper.isLetterOrDigit(inputExprStr.charAt(i + 1))) {
                        if (!isValidArbitraryExpression(arbExpressionStr)) {
                            throwCalculatorException(CalculatorException.INPUT_EXPRESSION_INVALID);
                        }

                        // if string is Function or Let operator
                        if (ArithmeticSymbols.isArithmeticFunction(arbExpressionStr)
                                || ArithmeticSymbols.isLetOperator(arbExpressionStr)) {
                            ExpressionTree parent = null;
                            if (expressionTree == null) {
                                //This indicates root node / start of expression tree building
                                inputExpressionTree = new ExpressionTree(arbExpressionStr);
                                parent = inputExpressionTree;
                            } else {
                                parent = expressionTree.addChild(arbExpressionStr);
                            }
                            arbExpression.delete(0, arbExpression.length());
                            inputExprStr = buildInputExpressionTree(parent, inputExprStr.substring(i + 1));
                            if(!Helper.isNullOrEmptyString(inputExprStr)) {
                                i = 0;
                                continue;
                            }
                        } else {
                            expressionTree.addChild(arbExpressionStr);
                            arbExpression.delete(0, arbExpression.length());
                        }
                    }
                }
            } else if (ArithmeticSymbols.isParenthesisOrComma(exprChar)) {
                    expressionTree.addChild(tempString);
            } else {
                throwCalculatorException(CalculatorException.INPUT_EXPRESSION_INVALID);
            }
            if(expressionTree!= null && expressionTree.isChildrenCountReached()
                    && !Helper.isNullOrEmptyString(inputExprStr) && (i + 1)!= inputExprStr.length())
                return inputExprStr.substring(i + 1);
            i++;
        }
        return "";
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
            return isValidVariableNameOrValue(arbExpression);
        }
    }

    /**
     * Function to check is variable name or value is valid
     * 1. value should be Integer [0-9 and - sign]
     * 2. variable should contain only Alphabets [a-z,A-Z]
     * @param expression
     * @return
     */
    private boolean isValidVariableNameOrValue(String expression){
        LOGGER.debug("In isValidVariableNameOrValue method, printing argument..." + expression);
        //Check if arbitrary expression contains digit [0-9]
        boolean containsDigit = expression.matches(".*\\d+.*");
        if (containsDigit) {
            //If contains true, then check arbitrary expression is a valid integer
            //Example: 55 is valid
            //Example: 5a or a5 is invalid
            return Helper.isInteger(expression);
        } else {
            //Example: aaa is valid
            //If contains false, then arbitrary expression valid string
            return true;
        }
    }

    /**
     * Function to process expression tree and return result
     * @param expressionTree
     * @return
     * @throws calculator.exception.CalculatorException
     */
    private ExpressionTree processInputExpressionTree(ExpressionTree expressionTree) throws CalculatorException {
        LOGGER.debug("In processInputExpressionTree method, printing argument..." + expressionTree.toString());
        String currentExpression = expressionTree.getExpression();
        if (!expressionTree.isLeafNode()){
            List<ExpressionTree> expressionTreeChildren = expressionTree.getChildren();
            for (int i = (expressionTreeChildren.size() - 1); i >= 0; i--) {
                ExpressionTree child = expressionTreeChildren.get(i);
                if (!child.isLeafNode()) {
                    ExpressionTree newChild = processInputExpressionTree(child);
                    expressionTreeChildren.remove(i);
                    expressionTreeChildren.add(i, newChild);
                }
            }
            if(ArithmeticSymbols.isLetOperator(currentExpression)) {
                //Perform "let" operation
                ExpressionTree oldExpression = expressionTreeChildren.get(1);
                ExpressionTree newExpression = expressionTreeChildren.get(3);
                ExpressionTree newChild = expressionTreeChildren.get(5);
                if(newChild.equals(oldExpression)){
                    return newExpression;
                }else{
                    newChild.recursiveReplace(oldExpression, newExpression);
                    return newChild;
                }
            }
            if(ArithmeticSymbols.isArithmeticFunction(currentExpression)) {
                //Compute arithmetic function
                String operand1Str = expressionTreeChildren.get(1).getExpression();
                String operand2Str = expressionTreeChildren.get(3).getExpression();
                // Check if variable names have been replaces with values
                //i.e. "let" operation is complete
                if(Helper.isInteger(operand1Str)&& Helper.isInteger(operand2Str)){
                    //Evaluate Arithmetic Function
                    try{
                        Integer operand1 = Integer.parseInt(operand1Str);
                        Integer operand2 = Integer.parseInt(operand2Str);
                        Integer result = compute(currentExpression, operand1, operand2);
                        return new ExpressionTree(result.toString());
                    }catch(NumberFormatException e){
                        LOGGER.error(e.getMessage());
                        throwCalculatorException(CalculatorException.INVALID_ARGUMENTS);
                    }
                }
            }
        }
        return expressionTree;
    }

    /**
     * Compute result for Arithmetic Function given input arguments
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
     * Method to throw CalculatorException and log error message.
     * @param message
     * @throws calculator.exception.CalculatorException
     */
    private void throwCalculatorException(String message) throws CalculatorException {
        LOGGER.error(message);
        throw new CalculatorException(message);
    }


}
