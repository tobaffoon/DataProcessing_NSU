package write_people_strict_task2.StrictPeople;

import javax.xml.bind.annotation.XmlAttribute;

public class StrictRelative {
    @XmlAttribute(name="id")
    public String id;
    public StrictRelative(){

    }
    public StrictRelative(int id){
        this.id = "P" + id;
    }
}
