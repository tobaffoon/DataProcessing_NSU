package Task11.src.main.java.ru.nsu.amazyar;

import java.util.concurrent.Semaphore;

public class Main {
    private static Semaphore firstSemaphore = new Semaphore(0);
    private static Semaphore secondSemaphore = new Semaphore(0);    

    private static final int ROUNDS = 10;

    public static void main(String[] args) {
        Thread printer = new Thread(() -> {
            for (int i = 0; i < ROUNDS; i++) {
                try {
                    firstSemaphore.acquire();
                } catch (InterruptedException ignored) {}

            System.out.println(Thread.currentThread().getName() + " step " + i);
            secondSemaphore.release();
        }
        }, "Child Thread");
        printer.start();
        
        for (int i = 0; i < ROUNDS; i++) {
            System.out.println(Thread.currentThread().getName() + " step " + i);
            try {
                firstSemaphore.release();
                secondSemaphore.acquire();
            } catch (InterruptedException ignored) {}
        }
    }
}