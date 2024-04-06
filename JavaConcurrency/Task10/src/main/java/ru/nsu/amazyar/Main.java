package Task10.src.main.java.ru.nsu.amazyar;
public class Main {
    private static Object soutSynchronizer = new Object();
    private static boolean mainPrinting = true;

    private static final int ROUNDS = 10;

    public static void main(String[] args) {
        Thread printer = new Thread(() -> {
            for (int i = 0; i < ROUNDS; i++) {
            synchronized(soutSynchronizer){
                while(mainPrinting){
                    try {
                        soutSynchronizer.wait();
                    } catch (InterruptedException ignored) {}
                }

                System.out.println(Thread.currentThread().getName() + " step " + i);
                mainPrinting = true;
                soutSynchronizer.notifyAll();
            }
        }
        }, "Child Thread");
        printer.start();
        
        for (int i = 0; i < ROUNDS; i++) {
            synchronized(soutSynchronizer){
                while(!mainPrinting){
                    try {
                        soutSynchronizer.wait();
                    } catch (InterruptedException ignored) {}
                }

                System.out.println(Thread.currentThread().getName() + " step " + i);
                mainPrinting = false;
                soutSynchronizer.notifyAll();
            }
        }
    }
}