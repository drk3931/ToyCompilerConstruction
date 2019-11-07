import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Compiler {

    private char look = '\0';

    private Reader streamReader;

    // if the error flag is triggered it wont write
    boolean errorFlag = false;

    public Compiler() throws Exception {

        this.streamReader = new InputStreamReader(new FileInputStream("source.txt"), "US-ASCII");
        getChar();
    }

    public void writeError(String err) {
        System.err.println("Error: " + err + '.');
    }

    public void halt(String err) {
        writeError(err);
        System.exit(0);
    }

    public void matchChar(char c) {
        if (look == c) {
            getChar();
        } else {
            writeExpected("Char eChar expected: " + c);
        }
    }

    public boolean isAddOperation(char s) {
        return s == '-' || s == '+';
    }

    public boolean isAlpha(char s) {
        return Character.isAlphabetic(s);
    }

    private char getLook() {
        return this.look;
    }

    private void writeToOutput(String s) {
        if (!errorFlag) {
            System.out.println('\t' + s);
        }
    }

    public char getName() {
        if (!isAlpha(look)) {
            writeExpected("Name");

        }
        char upperCaseLook = Character.toUpperCase(look);
        getChar();
        return upperCaseLook;

    }

    public char getNumber() {
        if (!Character.isDigit(look)) {
            writeExpected("Integer");
        }
        char num = look;
        getChar();
        return num;

    }

    public void writeExpected(String expected) {
        errorFlag = true;
        System.out.println("Error: expected " + expected + ',' + "recieved " + this.look);
    }

    public char getChar() {

        try {

            int next = this.streamReader.read();
            while (next == ' ') {
                next = this.streamReader.read();
            }
            this.look = (char) next;
            return this.look;
        } catch (Exception e) {

            halt("End of file");
        }
        return '\0';

    }

    // <expression> ::= <term> [<addop> <term>]*
    public void expression() {

        // to accomplish +3 or -1
        // it as 0 + 3 or 0 - 1
        // so simply set D0 to 0 and let the while loop take care of the plus/minus
        // operation as it would with a
        // standard expression
        //

        term();

        while (look == '+' || look == '-') {

            writeToOutput("MOVE D0, -(SP)");

            switch (look) {
            case ('+'): {
                add();
                break;
            }
            case ('-'): {
                subtract();
                break;
            }
            default: {
                writeExpected("Add Operation");
            }
            }

        }

    }

    public void add() {

        matchChar('+');
        term();
        writeToOutput("ADD (SP)+,D0");

    }

    /*
     * example: 1 - 2 MOVE# 1,D0 MOVE D0, D1 MOVE# 2,D0 SUBTRACT D1,D0 NEG D0
     * 
     *
     * 
     * d0 = 2 d1 = 1
     * 
     * sub d1 d0 = d0 - d1 = 2 - 1 = 1 -> stores result in d0, d0 * -1 = -1
     * 
     * 2 - 1 ends up negating -1 for a result of 1
     */
    public void subtract() {

        matchChar('-');
        term();
        writeToOutput("SUB (SP)+,D0");
        writeToOutput("NEG D0");

    }

    // ((1 + 2) + (2 + 3))/(2*2)

    public void factor() {

        if (isAddOperation(look)) {

            //this is adding an implicit 0 in the place of getnumber if you put in something like
            //-1
            writeToOutput("CLR D0");

        }
        else if (look == '(') {
            matchChar('(');
            expression();
            matchChar(')');
        } else {
            writeToOutput("MOVE #" + getNumber() + ",D0");

        }

    }

    public void multiply() {
        matchChar('*');
        factor();
        writeToOutput("MULS (SP)+,D0");

    }

    public void divide() {
        matchChar('/');
        factor();
        // why not just do
        // because of the order that division happens and where it would be stored
        // writeToOutput("DIVS (SP)+,D0");

        writeToOutput("MOVE (SP)+, D1");
        writeToOutput("DIVS D0,D1");
        writeToOutput("MOVE D1,D0");

    }

    // <term> ::= <factor> [ <mulop> <factor ]*
    // note how a term relates to an expression
    // For example 3 + 2 * 2 * 2 + 5 = 3 + (2 * 2 * 2) + 3 = <term> + <term> +
    // <term>
    // multiplication and division are "integrated in" and arent added to the
    // expression operations
    public void term() {


            factor();

            while (look == '*' || look == '/') {

                writeToOutput("MOVE D0,-(SP)");
                switch (look) {
                case '*':
                    multiply();
                    break;
                case '/':
                    divide();
                    break;
                default:
                    writeExpected("Expected MulOp");
                }

            }

        

    }

    public static void main(String[] args) throws Exception {
        Compiler c = new Compiler();

        c.expression();

    }

}