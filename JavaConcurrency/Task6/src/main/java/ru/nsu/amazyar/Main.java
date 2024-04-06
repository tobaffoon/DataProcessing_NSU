package Task6.src.main.java.ru.nsu.amazyar;

public class Main {
    public static void main(String[] args) {
        Company company = new Company(10);
        Founder founder = new Founder(company);
        founder.start();
    }
}
