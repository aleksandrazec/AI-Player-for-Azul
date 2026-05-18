public class NotATerminalState extends Exception{
    public NotATerminalState(){}
    public NotATerminalState(String message){
        super(message);
    }
}
