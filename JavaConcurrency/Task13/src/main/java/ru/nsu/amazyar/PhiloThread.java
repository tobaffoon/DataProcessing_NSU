package Task13.src.main.java.ru.nsu.amazyar;

import java.util.List;
import java.util.Random;

public class PhiloThread extends Thread{
    private static final long THINK_MILLIS_MAX = 3000;
    private static final long EAT_MILLIS_MAX = 3000;
    private static final long THINK_MILLIS_MIN = 500;
    private static final long EAT_MILLIS_MIN = 500;

    private final int index;
    private final List<Object> forks;
    private final boolean[] forkIsAvailable;
    public PhiloThread(int index, List<Object> forks, boolean[] forkIsAvailable){
        this.index = index;
        this.forks = forks;
        this.forkIsAvailable = forkIsAvailable;
    }
    
    public void run(){
        int leftFork, rightFork;
        Random random = new Random(threadId());

        //distribute forks
        if(index == 0){
            leftFork = forks.size()-1;
            rightFork = 0;
        }
        else{
            leftFork = index-1;
            rightFork = index;
        }

        while(true){
            // Think phase
            try {
                System.out.println("Philosopher " + index + " is thinking now");
                Thread.sleep(random.nextLong(THINK_MILLIS_MIN, THINK_MILLIS_MAX));
            } catch (InterruptedException ignored) {}

            // Get fork phase
            System.out.println("Philosopher " + index + " stopped thinking for now");

            
            synchronized(forks){
                while(!forkIsAvailable[leftFork] || !forkIsAvailable[rightFork]){
                    try {
                        forks.wait();
                    } catch (InterruptedException ignored) {}
                }

                forkIsAvailable[leftFork] = false;
                forkIsAvailable[rightFork] = false;
            }

            System.out.println("Philosopher " + index + " got " + leftFork + "th and " + rightFork + "th forks");
            // Eat phase
            try {
                System.out.println("Philosopher " + index + " eats now");
                Thread.sleep(random.nextLong(EAT_MILLIS_MIN, EAT_MILLIS_MAX));
            } catch (InterruptedException ignored) {}

            System.out.println("Philosopher " + index + " stopped eating for now; he freed " + leftFork + "th and " + rightFork + "th forks");

            synchronized(forks){
                forkIsAvailable[leftFork] = true;
                forkIsAvailable[rightFork] = true;

                forks.notifyAll();
            }
        }
    }
}
