package write_people_strict_task2;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import write_people_strict_task2.StrictPeople.MyValidationEventHandler;
import write_people_strict_task2.StrictPeople.People;
import write_people_strict_task2.StrictPeople.StrictPerson;

public class App {

    public static void main(String[] args) throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        // Map<Integer, Person> idMap = PeopleXMLParser.parse(new File(cl.getResource("people.xml").toURI()));
        // List<Person> people = new ArrayList<>(idMap.values());
        // BufferedWriter writer = new BufferedWriter(new FileWriter(new File("strict_people.xml")));
        
        JAXBContext context = JAXBContext.newInstance(People.class);
        // Marshaller marshaller = context.createMarshaller();
        // marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // People strictPeople = new People(people.stream().map(person -> new StrictPerson(person, idMap)).toList());
        // System.out.println("START MARSHALING STRICTLY");
        // marshaller.marshal(strictPeople, writer);
        // System.out.println("FINISH MARSHALING");
        // writer.close();

        System.out.println("START VALIDATION");
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File(cl.getResource("people_schema.xsd").toURI()));
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schema);
        unmarshaller.setEventHandler(new MyValidationEventHandler());
        unmarshaller.unmarshal(new File("strict_people.xml"));
        System.out.println("FINISH VALIDATION");
    }
}
