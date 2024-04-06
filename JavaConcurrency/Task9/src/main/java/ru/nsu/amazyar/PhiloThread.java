package Task9.src.main.java.ru.nsu.amazyar;

import java.util.List;
import java.util.Random;

public class PhiloThread extends Thread{
    private static final long THINK_MILLIS_MAX = 6;
    private static final long EAT_MILLIS_MAX = 6;
    private static final long THINK_MILLIS_MIN = 5;
    private static final long EAT_MILLIS_MIN = 5;

    private final int index;
    private final List<Object> forks;
    public PhiloThread(int index, List<Object> forks){
        this.index = index;
        this.forks = forks;
    }
    
    public void run(){
        Object leftFork, rightFork;
        Random random = new Random(threadId());

        //distribute forks
        if(index == 0){
            leftFork = forks.getLast();
            rightFork = forks.getFirst();
        }
        else{
            leftFork = forks.get(index-1);
            rightFork = forks.get(index);
        }

        while(true){
            // Think phase
            try {
                System.out.println("Philosopher " + index + " is thinking now");
                Thread.sleep(random.nextLong(THINK_MILLIS_MIN, THINK_MILLIS_MAX));
            } catch (InterruptedException ignored) {}

            // Get fork phase
            System.out.println("Philosopher " + index + " stopped thinking for now");

            // one solution to dead-locks - one philosohper is right-handed. No dead-locks, but maybe starvation
            
            if (index == 0){
                synchronized(rightFork){
                    System.out.println("Philosopher " + index + " got " + index + "th fork");
                    synchronized(leftFork){
                        System.out.println("Philosopher " + index + " got " + (index-1) + "th fork");
                        // Eat phase
                        try {
                            System.out.println("Philosopher " + index + " eats now");
                            Thread.sleep(random.nextLong(EAT_MILLIS_MIN, EAT_MILLIS_MAX));
                        } catch (InterruptedException ignored) {}
                        
                        System.out.println("Philosopher " + index + " stopped eating for now");
                    }
                }
            }
            
             else {
            synchronized(leftFork){
                System.out.println("Philosopher " + index + " got " + (index-1) + "th fork");
                synchronized(rightFork){
                    System.out.println("Philosopher " + index + " got " + index + "th fork");
                    // Eat phase
                    try {
                        System.out.println("Philosopher " + index + " eats now");
                        Thread.sleep(random.nextLong(EAT_MILLIS_MIN, EAT_MILLIS_MAX));
                    } catch (InterruptedException ignored) {}
                    
                    System.out.println("Philosopher " + index + " stopped eating for now");
                }
            }
         }
        }
    }
}
