package Task9.src.main.java.ru.nsu.amazyar;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int NUMBER_OF_PHILOS = 5; 
    private static List<Object> forks;
    private static List<Thread> philos;
    
    public static void main(String[] args) {
        forks = new ArrayList<>(NUMBER_OF_PHILOS);
        for (int i = 0; i < NUMBER_OF_PHILOS; i++) {
            forks.add(new Object());
        }

        philos = new ArrayList<>(NUMBER_OF_PHILOS);
        for (int i = 0; i < NUMBER_OF_PHILOS; i++) {
            philos.add(new PhiloThread(i, forks));
        }

        for (Thread phioloThread : philos) {
            phioloThread.start();
        }
    }    
}