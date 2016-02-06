/*********** Implementation Details for Calculator Program ***************/
Project contains two solutions:
A. List and Stack Implementation (Algorithm overview) : Main.java
	- Converts and validates input expression string to List<String>
	- Evaluates list using Stack
		i. Process "let" operator first
		ii. Process arithmetic function (add, sub, multi, div) 

B. Tree Implementation (Algorithm overview) : MainTree.java
	i. Converts and validates input expression string to Tree
	ii. Recursively evaluates "let" operator and then arithmetic functions in Tree
	
Assumptions:
A. Logging Feature:
	- Default Level is OFF
    - Accepts level name from command line in case-insensitive format
    - Accepts only 3 levels DEBUG, INFO, and ERROR
    - No exception thrown for invalid level name in command line; Default value is used in this case.

B. Validation rules for input expression:
    - Expression can be one of the following:
      1. Numbers : Integer (Range: Integer.MIN_VALUE to Integer.MAX_VALUE) [Range: 0-9 and Negative sign]
      2. Variables : String of characters (Range: a-z, A-Z) 
			Example: Valid : aaa, abcd
			Example: Invalid: a5, 6a
      3. Arithmetic Functions: add, sub, multi, div
      4. "let" operator
	  List also includes following entries
      5. Parenthesis open, "(" and close, ")"
      6. Comma ","
    - Any characters besides the ones mentioned above are considered invalid.
    - Any arbitrary expression, variable name or variable value are not surrounded by parenthesis
	  Example: (a), (5), (add(5,5)) : Invalid / Not tested for such expressions
    - The program only works with () parenthesis and no other parenthesis

 Tasks completed from the assignment document:
 1. Functional Requirements
 2. Logging using Log4j
 3. Exception Handling
 4. Maven Build
 5. JUnit Tests
 6. Git Repository
 https://github.com/mazi15/Calculator.git
 7. Continuous Integration using Travis-CI
