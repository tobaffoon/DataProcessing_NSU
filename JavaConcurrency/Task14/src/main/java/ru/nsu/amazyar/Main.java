package Task14.src.main.java.ru.nsu.amazyar;

import java.util.concurrent.Semaphore;

public class Main {
    private static Semaphore semaphoreA = new Semaphore(0);
    private static Semaphore semaphoreB = new Semaphore(0);  
    private static Semaphore semaphoreAB = new Semaphore(0);  
    private static Semaphore semaphoreC = new Semaphore(0);   

    private static Object syncSout = new Object();

    public static void main(String[] args) {
        Thread manufacturerA = new Thread(() -> {
            while(true){
                // create piece-A
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                semaphoreA.release();

                synchronized(syncSout){
                    System.out.println("Piece-A created.");
                }
            }
            
        }, "Thread-A");

        Thread manufacturerB = new Thread(() -> {
            while(true){
                // create piece-B
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
                semaphoreB.release();

                synchronized(syncSout){
                    System.out.println("Piece-B created.");
                }
            }
            
        }, "Thread-B");

        Thread manufacturerC = new Thread(() -> {
            while(true){
                // create piece-C
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {}
                semaphoreC.release();

                synchronized(syncSout){
                    System.out.println("Piece-C created.");
                }
            }
            
        }, "Thread-C");
        
        Thread widgetBuilder = new Thread(() -> {
            while(true){
                try {
                    // create A-B module
                    semaphoreA.acquire();
                    semaphoreB.acquire();
                    semaphoreAB.release();

                    // create widget
                    semaphoreC.acquire();
                    semaphoreAB.acquire();
                } catch (InterruptedException ignored) {}


                synchronized(syncSout){
                    System.out.println("Widget created!");
                }
            }
            
        }, "Thread-A");

        manufacturerA.start();
        manufacturerB.start();
        manufacturerC.start();
        widgetBuilder.start();
    }
}