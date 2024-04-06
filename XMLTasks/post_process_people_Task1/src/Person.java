import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class Person{
    public int id = -1;

    private String firstName = null;
    private String familyName = null;
    public Gender gender = null;

    public int spouceId = -1;
    private String spouceName = null;

    private Set<Integer> parentsIds = new HashSet<>();
    private Set<String> parentsNames = new HashSet<>(2);

    private Set<Integer> childrenIds = new HashSet<>();
    private Set<String> childrenNames = new HashSet<>();
    public int childrenCheckNumber = -1;

    private Set<Integer> siblingsIds = new HashSet<>();
    private Set<String> siblingsNames = new HashSet<>();
    public int siblingsCheckNumber = -1;

    public void tryUpdateId(int newId) throws PersonMergeException{
        if(this.id == -1){
            this.id = newId;
            return;
        }
        
        if(this.id != newId){
            throw new PersonMergeException(this, "ID", String.valueOf(this.id), String.valueOf(newId));
        }
    }

    public void tryUpdateId(String str) throws PersonMergeException{
        tryUpdateId(Integer.parseInt(str.substring(1)));
    }

    public void tryUpdateFullname(String str) throws PersonMergeException{
        if(str == null) return;

        String[] words = str.trim().split("\\s+");
        if(words.length != 2) return;

        this.tryUpdateFirstName(words[0]);
        this.tryUpdateFamilyName(words[1]);
    }

    public void tryUpdateFirstName(String str) throws PersonMergeException{
        if(this.firstName == null){
            this.firstName = str;
            return;
        }

        if(!this.firstName.equals(str)){
            throw new PersonMergeException(this, "First name", this.firstName, str);
        }
    }

    public void tryUpdateFamilyName(String str) throws PersonMergeException{
        if(this.familyName == null){
            this.familyName = str;
            return;
        }

        if(!this.familyName.equals(str)){
            throw new PersonMergeException(this, "Family Name", this.familyName, str);
        }
    }

    public void tryUpdateGender(Gender newGenderInfo) throws PersonMergeException{
        if(this.gender == null){ 
            this.gender = newGenderInfo;
            return;
        }

        if(!this.gender.equals(newGenderInfo)){
            throw new PersonMergeException(this, "Gender", this.gender.toString(), newGenderInfo.toString());
        }
    }

    public void tryUpdateSiblingsNumber(int newSiblingsCheckNumber) throws PersonMergeException, IllegalRelativeException{
        if(this.siblingsCheckNumber == -1){
            if(this.siblingsIds.size() > newSiblingsCheckNumber) throw new IllegalRelativeException(this, "Sibling");
            
            this.siblingsCheckNumber = newSiblingsCheckNumber;
            return;
        }
        
        if(this.siblingsCheckNumber != newSiblingsCheckNumber){
            throw new PersonMergeException(this, "Siblings number", String.valueOf(this.siblingsCheckNumber), String.valueOf(newSiblingsCheckNumber));
        }
    }

    public void tryUpdateChildrenNumber(int newChildrenCheckNumber) throws PersonMergeException, IllegalRelativeException{
        if(this.childrenCheckNumber == -1){
            if(this.childrenIds.size() > newChildrenCheckNumber) throw new IllegalRelativeException(this, "Child");

            this.childrenCheckNumber = newChildrenCheckNumber;
            return;
        }
        
        if(this.childrenCheckNumber != newChildrenCheckNumber){
            throw new PersonMergeException(this, "Siblings number", String.valueOf(this.childrenCheckNumber), String.valueOf(newChildrenCheckNumber));
        }

    }

    public void tryUpdateSpouce(Person spouce) throws PersonMergeException{
        if(spouce.id != -1){
            if(this.spouceId == -1){
                this.spouceId = spouce.id;
            }
            else if(this.spouceId != spouce.id){
                throw new PersonMergeException(this, "Spouce ID", String.valueOf(this.spouceId), String.valueOf(spouce.id));
            } 
        }
        if(spouce.fullname() != null){
            if(this.spouceName == null){
                this.spouceName = spouce.fullname();
            }
            else if(!this.spouceName.equals(spouce.fullname())){
                throw new PersonMergeException(this, "Spouce ID", String.valueOf(this.spouceId), String.valueOf(spouce.id));
            } 
        }
    }

    public void tryUpdateParentList(Person parent){
        if(parent.id != -1) this.parentsIds.add(parent.id);
        if(parent.fullname() != null) this.parentsNames.add(parent.fullname());
    }
    public void tryUpdateChildList(Person child){
        if(child.id != -1) this.childrenIds.add(child.id);
        if(child.fullname() != null) this.childrenNames.add(child.fullname());
    }
    public void tryUpdateSiblingList(Person sibling){
        if(sibling.id != -1) this.siblingsIds.add(sibling.id);
        if(sibling.fullname() != null) this.siblingsNames.add(sibling.fullname());
    }

    public void tryMerge(Person newPerson) throws PersonMergeException, IllegalRelativeException{ 
        if(newPerson.id != -1){
            this.tryUpdateId(newPerson.id);

            // support namesakes among relatives. They can't have the same id though
            if(newPerson.spouceId != -1 && newPerson.spouceId == this.id) { 
                throw new PersonMergeException(this, newPerson.spouceId);
            }
            for (Integer parentId : newPerson.parentsIds) {
                if(parentId == this.id) throw new PersonMergeException(this, parentId);
            }
            for (Integer childId : newPerson.childrenIds) {
                if(childId == this.id) throw new PersonMergeException(this, childId);
            }
            for (Integer siblingId : newPerson.siblingsIds) {
                if(siblingId == this.id) throw new PersonMergeException(this, siblingId);
            }
        }

        if(newPerson.firstName != null){
            this.tryUpdateFirstName(newPerson.firstName);
        }

        if(newPerson.familyName != null){
            this.tryUpdateFamilyName(newPerson.familyName);
        }

        if(newPerson.gender != null){
            this.tryUpdateGender(newPerson.gender);
        }

        if(newPerson.childrenCheckNumber != -1){
            this.tryUpdateChildrenNumber(newPerson.childrenCheckNumber);
        }
        
        if(newPerson.siblingsCheckNumber != -1){
            this.tryUpdateSiblingsNumber(newPerson.siblingsCheckNumber);
        }

        if(parentsOverflow(parentsIds, newPerson.parentsIds)){
            throw new IllegalRelativeException(this, "Parent");
        }
        parentsIds.addAll(newPerson.parentsIds);
        parentsNames.addAll(newPerson.parentsNames);

        if(childrenOverflow(childrenIds, newPerson.childrenIds)){
            throw new IllegalRelativeException(this, "Child");
        }
        childrenIds.addAll(newPerson.childrenIds);
        childrenNames.addAll(newPerson.childrenNames);
        
        if(siblingOverflow(siblingsIds, newPerson.siblingsIds)){
            throw new IllegalRelativeException(this, "Sibling");
        }
        siblingsIds.addAll(newPerson.siblingsIds);
        siblingsNames.addAll(newPerson.siblingsNames);

        Person spouce = new Person();
        spouce.tryUpdateId(newPerson.spouceId);
        spouce.tryUpdateFullname(newPerson.spouceName);
        tryUpdateSpouce(spouce);
    }

    private boolean childrenOverflow(Set<Integer> oldList, Set<Integer> newList){
        if(this.childrenCheckNumber == -1) return false;

        Set<Integer> combination = new HashSet<>(oldList);
        combination.addAll(newList);
        return combination.size() > this.childrenCheckNumber;
    }
    private boolean siblingOverflow(Set<Integer> oldList, Set<Integer> newList){
        if(this.siblingsCheckNumber == -1) return false;

        Set<Integer> combination = new HashSet<>(oldList);
        combination.addAll(newList);
        return combination.size() > this.siblingsCheckNumber;
    }
    private boolean parentsOverflow(Set<Integer> oldList, Set<Integer> newList){
        Set<Integer> combination = new HashSet<>(oldList);
        combination.addAll(newList);
        return combination.size() > 2;
    }

    public String fullname(){
        if(firstName == null || familyName == null) return null;
        return firstName + " " + familyName;
    }

    public String toStructuredInformtaion(Map<Integer, Person> idMap){
        StringBuilder mainBuilder = new StringBuilder("==========\n" + this);
        StringBuilder missingRealtives = new StringBuilder("\nRelatives with no information:");
        mainBuilder.append("\nGender: ").append(this.gender);

        // Spouce
        if(this.spouceId != -1){
            Person spouce = idMap.get(this.spouceId);
            if(spouce.gender == null) {
                mainBuilder.append("\nSpouce:\n");
            }
            else {
                switch (spouce.gender) {
                    case Gender.Female:
                        mainBuilder.append("\nWife:\n");
                        break;
                    case Gender.Male:
                        mainBuilder.append("\nHusband:\n");
                        break;
                    default:
                        break;
                }
            }
            mainBuilder.append("\t" + spouce + "\n");
        }
        else missingRealtives.append("\n\tSpouce");

        // Parents
        for (int parentId : this.parentsIds) {
            Person parent = idMap.get(parentId);
            if(parent.gender == null){
                mainBuilder.append("\nParent:");
            } 
            else{
                switch (parent.gender) {
                    case Gender.Female:
                        mainBuilder.append("\nMother:");
                        break;
                    case Gender.Male:
                        mainBuilder.append("\nFather:");
                        break;
                    default:
                        break;
                }
            }
            
            mainBuilder.append("\n\t" + parent + "\n");
        }
        if(this.parentsIds.size() != 2) missingRealtives.append("\n\tParents");
        

        // Children
        for (int childId : this.childrenIds) {
            Person child = idMap.get(childId);
            if(child.gender == null) {
                mainBuilder.append("\nChild:");
            }
            else{
                switch (child.gender) {
                    case Gender.Female:
                        mainBuilder.append("\nDaughter:");
                        break;
                    case Gender.Male:
                        mainBuilder.append("\nSon:");
                        break;
                    default:
                        break;
                }
            }

            mainBuilder.append("\n\t" + child + "\n");
        }
        
        for (int siblingId : this.siblingsIds) {
            Person sibling = idMap.get(siblingId);
            if(sibling.gender == null){ 
                mainBuilder.append("\nSibling:");
            }
            else{
                switch (sibling.gender) {
                    case Gender.Female:
                        mainBuilder.append("\nSister:");
                        break;
                    case Gender.Male:
                        mainBuilder.append("\nBrother:");
                        break;
                    default:
                        break;
                }
            }
            
            mainBuilder.append("\n\t" + sibling + "\n");
        }
        mainBuilder.append("\nChildren Number: " + childrenCheckNumber);
        mainBuilder.append("\nSiblings Number: " + siblingsCheckNumber + "\n");

        mainBuilder.append(missingRealtives + "\n==========\n");

        return mainBuilder.toString();
    }
    
    public boolean equals(Person anotherPerson){
        return this.id != -1 && anotherPerson.id != -1 && this.id == anotherPerson.id;
    }
    
    public String toString(){
        if(this.fullname() != null) return "[" + id + "]: " + fullname();
        else  return "[" + id + "]";
    }
}
