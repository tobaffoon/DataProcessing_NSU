package write_people_strict_task2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="gender")
@XmlEnum(String.class)
public enum Gender {
    @XmlEnumValue("M")
    Male,
    @XmlEnumValue("F")
    Female
}
