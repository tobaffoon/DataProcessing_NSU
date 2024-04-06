public class PersonMergeException extends Exception {
    public PersonMergeException(Person personInfo, String fieldName, String oldValue, String newValue){
        super("Failed to merge " + personInfo + " with new info \nBecause [" + fieldName + "] oldValue:" + oldValue + " != newValue:" + newValue);
    }
    public PersonMergeException(Person personInfo, Person mergingInfo, String fieldName, String oldValue, String newValue){
        super("Failed to merge\n" + personInfo + "\nwith new info\n" + mergingInfo + "\nBecause [" + fieldName + "] oldValue:" + oldValue + " != newValue:" + newValue);
    }
    public PersonMergeException(Person personInfo, int relativeId){
        super("Failed to merge\n" + personInfo + "\nwith new info\n" + "\nBecause thier [ID]: " + personInfo.id + " got set to their relative's [ID]:" + relativeId);
    }
}
