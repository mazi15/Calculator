package calculator;

import calculator.utils.ArithmeticSymbols;

import java.util.ArrayList;
import java.util.List;

/**
 * Binary Tree to Store Expression
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

 * Example: let(a, 5, add ( a, a))
 *         ------- let (Valid Child Count = 7) ----------------------
 *         /  /  /  |  \        \           \
 *        /  /  /   |   \        \           \
 *       (   a  ,   5   , ------ add (Valid Child Count = 5)------  )
 *                          /  /  |  \  \
 *                         /  /   |   \  \
 *                        (  a    ,   a   )
 *      Ignore the "------" around the operator or the function.
 *      It is just to indicate the children stored within the node.
 */
public class ExpressionTree{
     //Children nodes
     private List<ExpressionTree> children;


     private String expression;

    /**
     * Constructor
     * @param expression
     */
     public ExpressionTree(String expression) {
            this.expression = expression;
     }

    /**
     * Check if leaf node
     * A node is a leaf node only if it is not a "let" Operator / Arithmetic Function
     * @return
     */
     public boolean isLeafNode(){
         if(ArithmeticSymbols.isArithmeticFunction(expression) || ArithmeticSymbols.isLetOperator(expression))
             return false;
         return true;
     }

    /**
     * Add child node
     * @param childNodeExpression
     * @return
     */
     public ExpressionTree addChild(String childNodeExpression){
         if(children == null)
             children = new ArrayList<ExpressionTree>();
         ExpressionTree child = new ExpressionTree(childNodeExpression);
         children.add(child);
         return child;
     }

    /**
     * Check if node has no children
     * @return
     */
     public boolean isEmpty(){
         return !isLeafNode() && (children == null || children.isEmpty());
     }

    /**
     * Get expression stored in this node
     * @return
     */
    public String getExpression(){
        return expression;
    }

    /**
     * Check if children count reached for non-leaf nodes
     * @return
     */
    public boolean isChildrenCountReached(){
        if(!isLeafNode()){
            if(ArithmeticSymbols.isArithmeticFunction(expression))
                return children.size() == 5;
            if(ArithmeticSymbols.isLetOperator(expression))
                return children.size() == 7;
        }
        return false;
    }

    /**
     * Get children for node
     * @return
     */
    public List<ExpressionTree> getChildren(){
        return children;
    }

    /**
     * Replace oldexpression in all children with new expression
     * @param oldExpression
     * @param newExpression
     */
    public void recursiveReplace(ExpressionTree oldExpression, ExpressionTree newExpression){
        if(!isLeafNode()) {
            for (int i = 0; i < children.size(); i++) {
                ExpressionTree child = children.get(i);
                child.recursiveReplace(oldExpression, newExpression);
                if (child.equals(oldExpression)){
                    children.remove(i);
                    children.add(i, newExpression);
                }
            }
        }
    }

    /**
     * Logic to compare equality of nodes in Tree
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o){
        if(o == null || !(o instanceof ExpressionTree))
            return false;
        if(this == o)
            return true;
        ExpressionTree compareTo = (ExpressionTree)o;
        return this.expression.toLowerCase().equals(compareTo.getExpression().toLowerCase());
    }

    /**
     * Logic to print tree as string
     * @return
     */
    @Override
    public String toString(){
        StringBuilder returnString = new StringBuilder();
        returnString.append(expression);
        if(!isLeafNode()) {
            for (ExpressionTree child : children) {
                returnString.append(child.toString());
            }
        }
        return returnString.toString();
    }
}
