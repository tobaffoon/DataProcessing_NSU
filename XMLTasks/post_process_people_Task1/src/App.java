import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
        Map<Integer, Person> idMap = PeopleXMLParser.parse(new File("resources\\people.xml"));
        List<Person> people = new ArrayList<>(idMap.values());
        BufferedWriter writer = new BufferedWriter(new FileWriter("resources\\out.txt"));
        for(Person person : people){
            writer.write(person.toStructuredInformtaion(idMap));
        }
        writer.close();
    }
}
