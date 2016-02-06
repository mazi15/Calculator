package calculator;

import calculator.exception.CalculatorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Test Case for
 * Calculator class with stack implementation
 */
public class MainTest {

    private Main main;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp(){
        main = new Main();
    }

    @After
    public void tearDown(){
        main = null;
    }

    @Test
    public void shouldAddIntegers() throws CalculatorException {
        Integer expectedResult = Integer.MIN_VALUE + Integer.MAX_VALUE;
        testCalculate(expectedResult, "add(" + Integer.MIN_VALUE + "," + Integer.MAX_VALUE + ")");

        expectedResult = 7 + 3;
        testCalculate(expectedResult, "add(7, 3)");

        expectedResult = 7 + (-3);
        testCalculate(expectedResult, "add(7, -3)");

        expectedResult = (-7) + (-3);
        testCalculate(expectedResult, "add(-7, -3)");
    }

    @Test
    public void shouldSubtractIntegers() throws CalculatorException {
        Integer expectedResult = Integer.MIN_VALUE - Integer.MAX_VALUE;
        testCalculate(expectedResult, "sub(" + Integer.MIN_VALUE + "," + Integer.MAX_VALUE + ")");

        expectedResult = 7 - 3;
        testCalculate(expectedResult, "sub(7, 3)");

        expectedResult = 7 - (-3);
        testCalculate(expectedResult, "sub(7, -3)");

        expectedResult = (-7) - (-3);
        testCalculate(expectedResult, "sub(-7, -3)");
    }

    @Test
    public void shouldMultiIntegers() throws CalculatorException {
        Integer expectedResult = Integer.MIN_VALUE * Integer.MAX_VALUE;
        testCalculate(expectedResult, "multi(" + Integer.MIN_VALUE + "," + Integer.MAX_VALUE + ")");

        expectedResult = 7 * 3;
        testCalculate(expectedResult, "multi(7, 3)");

        expectedResult = 7 * (-3);
        testCalculate(expectedResult, "multi(7, -3)");

        expectedResult = (-7) * (-3);
        testCalculate(expectedResult, "multi(-7, -3)");
    }

    @Test
    public void shouldDivIntegers() throws CalculatorException {
        Integer expectedResult = Integer.MIN_VALUE / Integer.MAX_VALUE;
        testCalculate(expectedResult, "div(" + Integer.MIN_VALUE + "," + Integer.MAX_VALUE + ")");

        expectedResult = 7 / 3;
        testCalculate(expectedResult, "div(7, 3)");

        expectedResult = 7 / (-3);
        testCalculate(expectedResult, "div(7, -3)");

        expectedResult = (-7) / (-3);
        testCalculate(expectedResult, "div(-7, -3)");
    }

    @Test
    public void shouldReplaceVariableNameWithValueAndReturnResult() throws CalculatorException {
        testCalculate(10, "let(a, 5, add(a, a))");

        testCalculate(15, "let(a, add(5, 5), add(a , 5))");
    }

    @Test
    public void shouldCalculateToExpectedResult() throws CalculatorException {

        testCalculate(3, "add(1,2)");

        testCalculate(7, "add(1,multi(2,3))");

        testCalculate(12, "multi(add(2,2),div(9,3))");

        testCalculate(10, "let(a,5,add(a,a))");

        testCalculate(55, "let(a,5,let(b,multi(a,10),add(b,a)))");

        testCalculate(40, "let(a,let(b,10,add(b,b)),let(b,20,add(a,b)))");
    }

    @Test
    public void shouldCalculateToExpectedResult_CaseInsensitive() throws CalculatorException {
        testCalculate(40, "LET(A,LET(B,10,ADD(B,b)),LET(B,20,ADD(A,b)))");
    }

    @Test
    public void shouldCalculateToExpectedResult_RemovingSpaces() throws CalculatorException {
        testCalculate(40, " let (a , let( b,    10, add ( b , b ) ) , let ( b, 20 , add ( a , b )))");
    }

    @Test
    public void shouldThrowException_EmptyInputExpression() throws CalculatorException {
        //Empty Input Expression
        String expectedExceptionMessage = CalculatorException.INPUT_EXPRESSION_MISSING;
        testCalculateWithException(expectedExceptionMessage, "");
    }

    @Test
    public void shouldThrowException_InvalidInputExpression() throws CalculatorException {
        //Invalid Input Expression
        String expectedExceptionMessage = CalculatorException.INPUT_EXPRESSION_INVALID;
        testCalculateWithException(expectedExceptionMessage, "invalidexpression");
    }

    @Test
    public void shouldThrowException_ParenthesisMismatch() throws CalculatorException {
        //Missing Parenthesis
        String expectedExceptionMessage = CalculatorException.PARENTHESIS_MISMATCH;
        testCalculateWithException(expectedExceptionMessage, "let(a,5,add(a,a)");
    }

    @Test
    public void shouldThrowException_MissingArgsForLetOperator() throws CalculatorException {
        //Missing arguments / comma for "let" operator
        String expectedExceptionMessage = CalculatorException.INVALID_ARGUMENTS;
        testCalculateWithException(expectedExceptionMessage, "let(a,add(a,a))");
    }

    @Test
    public void shouldThrowException_MissingArgsForArithFunc() throws CalculatorException {
        //Missing arguments / comma for arithmetic function
        String expectedExceptionMessage = CalculatorException.INVALID_ARGUMENTS;
        testCalculateWithException(expectedExceptionMessage, "add(55)");
    }

    @Test
    public void shouldThrowException_InvalidVariableExpression_a5() throws CalculatorException {
        //Invalid variable name / variable value
        String expectedExceptionMessage = CalculatorException.INPUT_EXPRESSION_INVALID;
        testCalculateWithException(expectedExceptionMessage, "add(a5,5)");

    }

    @Test
    public void shouldThrowException_InvalidVariableExpression_5a() throws CalculatorException {
        //Invalid variable name / variable value
        String expectedExceptionMessage = CalculatorException.INPUT_EXPRESSION_INVALID;
        testCalculateWithException(expectedExceptionMessage, "add(5,5a)");

    }

    @Test
    public void shouldThrowException_InvalidCharacterInExpression() throws CalculatorException {
        //Invalid character in expression
        String expectedExceptionMessage = CalculatorException.INPUT_EXPRESSION_INVALID;
        testCalculateWithException(expectedExceptionMessage, "add/5,5a)");

    }

    private void testCalculate(Integer expectedResult, String expression) throws CalculatorException {
        String actualResult = null;
        actualResult = main.calculate(expression);
        assertEquals(expectedResult.toString(), actualResult);
    }

    private void testCalculateWithException(String exceptionMessage, String expression) throws CalculatorException {
        exception.expect(CalculatorException.class);
        if(exceptionMessage != null && !exceptionMessage.isEmpty())
            exception.expectMessage(exceptionMessage);
        main.calculate(expression);
    }
}
