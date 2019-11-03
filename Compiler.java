import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Compiler {

    private char look = '\0';

    private Reader streamReader;


    //if the error flag is triggered it wont write 
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

    public boolean isAlpha(char s)
    {
        return Character.isAlphabetic(s);
    }

    private char getLook()
    {
        return this.look;
    }

    private void writeToOutput(String s)
    {
        if(!errorFlag)
        {
            System.out.println(s);
        }
    }


    public char getName()
    {
        if(!isAlpha(look))
        {
            writeExpected("Name");

        }
        char upperCaseLook = Character.toUpperCase(look);
        getChar();
        return upperCaseLook;

    }

    public char getNumber()
    {
        if(!Character.isDigit(look))
        {
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
            while(next == ' ')
            {
                next = this.streamReader.read();
            }
            this.look = (char) next;
            return this.look;
        } catch (Exception e) {

            halt("End of file");
        }
        return '\0';

    }


    public void expression()
    {
    

        term();
        writeToOutput("MOVE D0, D1");

        switch(look)
        {
            case('+'):{add(); break;}
            case('-'):{subtract(); break;}
            default:{writeExpected("Add Operation");}
        }
    }

    public void add()
    {

        matchChar('+');
        term();
        writeToOutput("ADD D1,D0");


    }
    public void subtract()
    {

        matchChar('-');
        term();
        writeToOutput("SUBTRACT D1,D0");

    }

    public void term()
    {
        writeToOutput(
            "Move# " + getNumber() + ",D0"
        );
    }



    public static void main(String[] args) throws Exception {
        Compiler c = new Compiler();
        
       
        c.expression();

    }

}