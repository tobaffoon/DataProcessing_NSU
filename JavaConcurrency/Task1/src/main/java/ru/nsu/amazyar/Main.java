package Task1.src.main.java.ru.nsu.amazyar;
public class Main {
    public static void main(String[] args) {
        Thread printer = new Thread(() -> printLines(), "Child Thread");
        printer.start();
        printLines();
    }

    public static void printLines(){
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName() + " step " + i);
        }
    }
}