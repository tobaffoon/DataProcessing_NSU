package write_people_strict_task2.StrictPeople;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "people")
@XmlRootElement(name="people")
public class People {
    @XmlAttribute
    public int count;
    
    @XmlElement(name="person", type=StrictPerson.class)
    public List<StrictPerson> peopleList;
    public People(List<StrictPerson> peopleList){
        this.peopleList = peopleList;
        this.count = peopleList.size();
    }
    public People(){
        this.peopleList = new ArrayList<>();
    }
}
