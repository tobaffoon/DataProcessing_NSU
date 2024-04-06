public class IllegalRelativeException extends Exception {
    public IllegalRelativeException(Person personInfo, String fieldName){
        super("Failed to add new " + fieldName + " to " + personInfo + " " + fieldName + "s");
    }
}
