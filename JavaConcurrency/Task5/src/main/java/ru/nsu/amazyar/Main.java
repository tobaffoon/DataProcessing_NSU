package Task5.src.main.java.ru.nsu.amazyar;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Thread printer = new Thread(() -> infinitePrint());
        printer.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
        printer.interrupt();
    }

    public static void infinitePrint(){
        Random random = new Random();
        while(true){
            if(Thread.interrupted()){
                System.out.println(Thread.currentThread().getName() + " has been interrupted");
                return;
            }
            System.out.println(random.nextInt(10));
        }
    }
}