package write_people_strict_task2.StrictPeople;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import write_people_strict_task2.Gender;
import write_people_strict_task2.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlType(name = "person")
public class StrictPerson {
    @XmlAttribute(name="id")
    public String id;
    @XmlElement(name="gender")
    public Gender gender;
    @XmlElement(name="firstname")
    public String firstname;
    @XmlElement(name="surname")
    public String surname;
    @XmlElement(name="wife")
    public StrictRelative wife;
    @XmlElement(name="husband")
    public StrictRelative husband;
    @XmlElement(name="mother")
    public StrictRelative mother;
    @XmlElement(name="father")
    public StrictRelative father;

    @XmlElement(name="childrenNumber")
    public int childrenNumber;
    @XmlElementWrapper(name="children")
    @XmlElements({
        @XmlElement(name = "son", type = StrictSon.class, required = true),
        @XmlElement(name = "daughter", type = StrictDaughter.class, required = true)
    })
    public List<StrictRelative> children;

    @XmlElement(name="siblingsNumber")
    public int siblingsNumber;
    @XmlElementWrapper(name="siblings")
    @XmlElements({
        @XmlElement(name = "brother", type = StrictBrother.class, required = true),
        @XmlElement(name = "sister", type = StrictSister.class, required = true)
    })
    public List<StrictRelative> siblings;

    public StrictPerson(){

    }
    public StrictPerson(Person complexInfo, Map<Integer, Person> idMap){
        this.id = "P" + complexInfo.id;
        this.gender = complexInfo.gender;
        this.firstname = complexInfo.firstName;
        this.surname = complexInfo.familyName;

        // spouce
        Person spouce = idMap.get(complexInfo.spouceId);
        if(spouce != null){
            if(spouce.gender == Gender.Male){
                this.husband = new StrictRelative(complexInfo.spouceId);
            }
            else{
                this.wife = new StrictRelative(complexInfo.spouceId);
            }
        }

        // parents
        for (Integer id : complexInfo.parentsIds) {
            Person parent = idMap.get(id);
            if(parent.gender == Gender.Male){
                this.father = new StrictRelative(id);
            }
            else{
                this.mother = new StrictRelative(id); 
            }
        }

        this.childrenNumber = complexInfo.childrenCheckNumber==-1?0:complexInfo.childrenCheckNumber;
        if(childrenNumber != 0){
            this.children = new ArrayList<>(childrenNumber);
            for (int chilId : complexInfo.childrenIds) {
                if(idMap.get(chilId).gender == Gender.Male){
                    this.children.add(new StrictSon(chilId));
                }
                else{
                    this.children.add(new StrictDaughter(chilId));
                }
            }
        }

        this.siblingsNumber = complexInfo.siblingsCheckNumber==-1?0:complexInfo.siblingsCheckNumber;
        if(siblingsNumber != 0){
            this.siblings = new ArrayList<>(siblingsNumber);
            for (int sibId : complexInfo.siblingsIds) {
                if(idMap.get(sibId).gender == Gender.Male){
                    this.siblings.add(new StrictBrother(sibId));
                }
                else{
                    this.siblings.add(new StrictSister(sibId));
                }
            }
        }
    }
}
