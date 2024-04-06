package Task13.src.main.java.ru.nsu.amazyar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final int NUMBER_OF_PHILOS = 5; 
    private static final List<Object> forks = new ArrayList<>(NUMBER_OF_PHILOS);
    private static final List<Thread> philos = new ArrayList<>(NUMBER_OF_PHILOS);
    private static final boolean[] forkIsAvailable = new boolean[NUMBER_OF_PHILOS];

    // Lists' and array's initialisation 
    static{
        for (int i = 0; i < NUMBER_OF_PHILOS; i++) {
            forks.add(new Object());
        }

        for (int i = 0; i < NUMBER_OF_PHILOS; i++) {
            philos.add(new PhiloThread(i, forks, forkIsAvailable));
        }

        Arrays.fill(forkIsAvailable, true);
    }
    
    public static void main(String[] args) {
        for (Thread phioloThread : philos) {
            phioloThread.start();
        }
    }    
}