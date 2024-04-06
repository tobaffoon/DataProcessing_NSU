package write_people_strict_task2;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

public class PeopleXMLParser {
    public static Map<Integer, Person> parse(File xml) throws FileNotFoundException, XMLStreamException, PersonMergeException, IllegalRelativeException{
        XMLInputFactory streamFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = streamFactory.createXMLEventReader(new FileInputStream(xml));

        List<Person> idEntries = new ArrayList<>();
        List<Person> nameEntries = new ArrayList<>();
        Person currentPerson = new Person();

        while(reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if(event.isStartElement()){
                String elementName = event.asStartElement().getName().getLocalPart();
                Iterator<Attribute> attributes = event.asStartElement().getAttributes();

                switch (elementName) {
                    case "person":
                        Person attributesInfo = parsePersonAttributes(attributes);
                        currentPerson = attributesInfo;
                        break;

                    case "id":
                        String parsedId = parseSingleField(reader, attributes);
                        currentPerson.tryUpdateId(parsedId);
                        break;
                    
                    case "firstname":
                        String firstname = parseSingleField(reader, attributes);
                        currentPerson.tryUpdateFirstName(firstname);
                        break;
                    
                    case "surname":
                    case "family-name":
                        String surname = parseSingleField(reader, attributes);
                        currentPerson.tryUpdateFamilyName(surname);
                        break;

                    case "fullname":
                        String fullname = parseFullname(reader);
                        currentPerson.tryUpdateFullname(fullname);
                        break;

                    case "gender":
                        String gender = parseSingleField(reader, attributes);
                        if(gender.equals("female") || gender.equals("F")) currentPerson.tryUpdateGender(Gender.Female);
                        else if(gender.equals("male") || gender.equals("M")) currentPerson.tryUpdateGender(Gender.Male);
                        break;

                    case "spouce":
                        Optional<Person> possibleSpouce = parseSpouce(reader, attributes);
                        if(possibleSpouce.isEmpty()) break;
                        Person spouce = possibleSpouce.get();

                        spouce.tryUpdateSpouce(currentPerson);
                        currentPerson.tryUpdateSpouce(spouce);

                        // spouce tag only has name
                        nameEntries.add(spouce);
                        break;
                        
                    case "husband":
                    case "wife":
                        String spouceInfo = parseSingleField(reader, attributes);
                        
                        spouce = new Person();
                        spouce.tryUpdateId(spouceInfo);
                        if(elementName.equals("wife")) {spouce.tryUpdateGender(Gender.Female);}
                        if(elementName.equals("husband")) spouce.tryUpdateGender(Gender.Male);

                        spouce.tryUpdateSpouce(currentPerson);
                        currentPerson.tryUpdateSpouce(spouce);

                        // husband/wife tag has id
                        idEntries.add(spouce);
                        break;

                    case "parent":
                        String parentInfo = parseSingleField(reader, attributes);
                        if(parentInfo.equals("UNKNOWN")) break;
                        
                        Person parent = new Person();
                        parent.tryUpdateId(parentInfo);

                        parent.tryUpdateChildList(currentPerson);
                        currentPerson.tryUpdateParentList(parent);

                        // parent tag has id
                        idEntries.add(parent);
                        break;
                    
                    case "mother":
                    case "father":
                        parentInfo = parseSingleField(reader, attributes);
                        
                        parent = new Person();
                        parent.tryUpdateFullname(parentInfo);
                        if(elementName.equals("mother")) parent.tryUpdateGender(Gender.Female);
                        if(elementName.equals("father")) parent.tryUpdateGender(Gender.Male);

                        parent.tryUpdateChildList(currentPerson);
                        currentPerson.tryUpdateParentList(parent);

                        // mother/father only has name
                        nameEntries.add(parent);
                        break;

                    case "children":
                        List<Person> children = parseChildren(reader);
                        for (Person child : children) {
                            child.tryUpdateParentList(currentPerson);
                            currentPerson.tryUpdateChildList(child);

                            if(child.id == -1) nameEntries.add(child);
                            else idEntries.add(child);
                        }
                        break;

                    case "siblings":
                        List<Person> siblings = parseSiblings(reader, attributes);
                        for (Person sibling : siblings) {
                            sibling.tryUpdateSiblingList(currentPerson);
                            currentPerson.tryUpdateSiblingList(sibling);

                            if(sibling.id != -1) idEntries.add(sibling);
                            else nameEntries.add(sibling);
                        }
                        break;
                    
                    case "children-number":
                        int n_children = Integer.parseInt(parseSingleField(reader, attributes));
                        currentPerson.tryUpdateChildrenNumber(n_children);
                        break;
                    
                    case "siblings-number":
                        int n_siblings = Integer.parseInt(parseSingleField(reader, attributes));
                        currentPerson.tryUpdateSiblingsNumber(n_siblings);
                        break;
                        
                    default:
                        System.out.println("PARSING: " + elementName);
                        break;
                }
            }
            if(event.isEndElement()){
                String elementName = event.asEndElement().getName().getLocalPart();
                switch (elementName) {
                    case "person":
                        if(currentPerson.id != -1) idEntries.add(currentPerson);
                        else nameEntries.add(currentPerson);

                        break;
                    default:
                        break;
                }
            }
        }

        System.out.println("STOP PARSING");
        System.out.println("START MAPPING BY ID");
        
        Map<Integer, Person> idMap = mapPeopleById(idEntries);
        System.out.println("STOP MAPPING BY ID");

        setTraditionalGenders(idMap);

        System.out.println("START COMBINING W/O ID");
        combinePeopleByName(idMap, nameEntries);
        System.out.println("FINISH PARSING");
        // setTraditionalGenders(idMap);

        return idMap;
    }

    private static Map<Integer, Person> mapPeopleById(List<Person> peopleWithId) throws PersonMergeException, IllegalRelativeException{
        Map<Integer, Person> idMap = new HashMap<>();
        for(Person person : peopleWithId){
            mapPerson(idMap, person);
        }

        return idMap;
    }

    private static void mapPerson(Map<Integer, Person> idMap, Person person) throws PersonMergeException, IllegalRelativeException{
        Person mappedPerson = idMap.get(person.id);
        if(mappedPerson == null){
            idMap.put(person.id, person);
        }
        else{ 
            mappedPerson.tryMerge(person);
        }
    }

    // set's people's gender to the opposite of their spouce's
    private static void setTraditionalGenders(Map<Integer, Person> idMap) throws PersonMergeException{
        for(Person person : idMap.values()){
            if(person.spouceId == -1) continue;

            Person spouce = idMap.get(person.spouceId);
            if(spouce.gender == Gender.Female) person.tryUpdateGender(Gender.Male);
            else if(spouce.gender == Gender.Male) person.tryUpdateGender(Gender.Female);
        }
    }

    private static void combinePeopleByName(Map<Integer, Person> idMap, List<Person> peopleWithNames) throws PersonMergeException{
        // get map of all namesakes to iterate over People with ID only one time per name
        Map<String, List<Person>> peopleByNames = peopleWithNames.parallelStream().collect(Collectors.groupingBy(Person::fullname));
 
        for(Entry<String, List<Person>> entry : peopleByNames.entrySet()){
            mergeNamedNamesakes(idMap, entry.getKey(), entry.getValue());
        }
    }

    private static void mergeNamedNamesakes(Map<Integer, Person> idMap, String name, List<Person> namedPeople) throws PersonMergeException{
        List<Person> namesakesWithId = idMap.values().parallelStream().filter(p -> name.equals(p.fullname())).collect(Collectors.toList());        
        for(Person namedPerson : namedPeople){
            // if("Tonya Loschiavo".equals(name) && namedPerson.siblingsCheckNumber == 1){
            //         System.out.println(namedPerson.toStructuredInformtaion(idMap));
            // }
            boolean merged = false;
            for(Person namesake : namesakesWithId){
                try{
                    namesake.tryMerge(namedPerson);
                    merged = true;
                    break;
                }catch(PersonMergeException e){
                    continue;
                }
                catch(IllegalRelativeException e){
                    continue;
                }
            }

            if(merged == false) throw new RuntimeException("Couldn't merge " + namedPerson);
        }
    }

    private static List<Person> parseChildren(XMLEventReader reader) throws XMLStreamException, PersonMergeException{
        reader.nextEvent(); // skip empty characters event
        List<Person> children = new ArrayList<>();
        XMLEvent event;
        String childInfo;
        do{
            event = reader.nextEvent();
            if(event.isStartElement()){
                childInfo = parseSingleField(reader, event.asStartElement().getAttributes());
                String elementName = event.asStartElement().getName().getLocalPart();
                switch (elementName) {
                    case "child":
                        Person child = new Person();
                        child.tryUpdateFullname(childInfo);
                        children.add(child);
                        break;

                    case "daughter":
                    case "son":
                        child = new Person();
                        child.tryUpdateId(childInfo);

                        if(elementName.equals("daughter")) child.tryUpdateGender(Gender.Female);
                        if(elementName.equals("son")) child.tryUpdateGender(Gender.Male);

                        children.add(child);
                        break;
                
                    default:
                        break;
                }
            }
        }while(!event.isEndElement());
        
        return children;
    }

    private static String parseFullname(XMLEventReader reader) throws XMLStreamException, PersonMergeException{
        reader.nextEvent(); // skip empty characters event
        XMLEvent event;
        StringBuilder builder = new StringBuilder();

        // start element of first name
        event = reader.nextEvent();
        Iterator<Attribute> attributes = event.asStartElement().getAttributes();

        builder.append(parseSingleField(reader, attributes)).append(' ');
        
        // start element of family name
        do {
            event = reader.nextEvent();
        }while(!event.isStartElement());
        builder.append(parseSingleField(reader, attributes)).append(' ');

        return builder.toString();
    }

    private static List<Person> parseSiblings(XMLEventReader reader, Iterator<Attribute> attributes) throws XMLStreamException, PersonMergeException{
        List<Person> siblings = new ArrayList<>();

        // there are attributes => no character element to read
        if(attributes.hasNext()){
            Attribute attribute = attributes.next();
            String[] siblingsIds = attribute.getValue().trim().split("\\s+");
            for (String id : siblingsIds) {
                Person sibling = new Person();
                sibling.tryUpdateId(id);
                siblings.add(sibling);
            }

            return siblings;
        }

        // read child elements alternatively
        reader.nextEvent(); // skip empty characters event
        XMLEvent event;
        String siblingInfo;
        do{
            event = reader.nextEvent();
            if(event.isStartElement()){
                siblingInfo = parseSingleField(reader, event.asStartElement().getAttributes());
                String elementName = event.asStartElement().getName().getLocalPart();
                switch (elementName) {
                    case "brother":
                    case "sister":
                        Person sibling = new Person();
                        sibling.tryUpdateFullname(siblingInfo);

                        if(elementName.equals("sister")) sibling.tryUpdateGender(Gender.Female);
                        if(elementName.equals("brother")) sibling.tryUpdateGender(Gender.Male);

                        siblings.add(sibling);
                        break;
                
                    default:
                        break;
                }
            }
        }while(!event.isEndElement());
        
        return siblings;
    }

    private static Optional<Person> parseSpouce(XMLEventReader reader, Iterator<Attribute> attributes) throws XMLStreamException, PersonMergeException{
        // no attributes => no spouce
        if(!attributes.hasNext()){
            return Optional.empty();
        }

        String spouceInfo = attributes.next().getValue();
        if(spouceInfo.equals("NONE")) return Optional.empty();

        // real spouce with real name
        Person spouce = new Person();
        spouce.tryUpdateFullname(spouceInfo);

        return Optional.of(spouce);
    }

    // Retrieves string value from field with either single attribute or single charactersElement child
    private static String parseSingleField(XMLEventReader reader, Iterator<Attribute> attributes) throws XMLStreamException, PersonMergeException{
        String charactersInfo = null;

        // there are attributes => no character element to read
        if(attributes.hasNext()){
            Attribute attribute = attributes.next();
            charactersInfo = attribute.getValue().trim();
        }

        // there are no attributes => read character element
        else{
            XMLEvent event = reader.nextEvent();
            if(!event.isCharacters()){
                throw new RuntimeException("Something off with document");
            }

            charactersInfo = event.asCharacters().getData().trim();
        }

        // read EndElementEvent
        if(!reader.nextEvent().isEndElement()){ 
            throw new RuntimeException("Something off with document");
        } 

        return charactersInfo;
    }

    private static Person parsePersonAttributes(Iterator<Attribute> attributes) throws PersonMergeException{
        Person personInfo = new Person();
        while(attributes.hasNext()){
            Attribute attribute = attributes.next();

            switch (attribute.getName().getLocalPart()) {
                case "id":
                    personInfo.tryUpdateId(attribute.getValue());
                    break;
                
                case "name":
                    personInfo.tryUpdateFullname(attribute.getValue());
                    break;

                default:
                    break;
            }
        }

        return personInfo;
    }
    
}
