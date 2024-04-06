package Task7.src.main.java.ru.nsu.amazyar;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final long LEIBNIZ_ITERATIONS = 10000000000L;
    public static void main(String[] args) {
        long threadsNumber;
        if(args.length == 0){
            System.err.println("Pass a number of desired threads");
            return;
        }
        try{
            threadsNumber = Long.parseLong(args[0]);
        }catch(NumberFormatException e){
            System.err.println("Cannot parse a number");
            return;
        }
        if(threadsNumber <= 0){
            System.err.println("Pass a number greater than 0");
            return;
        }
        if(threadsNumber > LEIBNIZ_ITERATIONS){
            System.out.println("Number of threads too big, it will be shortened to " + LEIBNIZ_ITERATIONS);
            threadsNumber = LEIBNIZ_ITERATIONS;
        }

        List<LeibnizThread> leibnizThreads = new ArrayList<>();

        // LEIBNIZ_ITERATIONS = sectionStep * threadsNumber + extraStepsNumber
        long sectionStep = LEIBNIZ_ITERATIONS / threadsNumber;
        long extraStepsNumber = LEIBNIZ_ITERATIONS % threadsNumber;

        // sections with sectionStep+1 steps
        long endingExtraIndex = extraStepsNumber * (sectionStep+1);
        long index = 0;
        for (; index < endingExtraIndex; index += sectionStep+1) {
            LeibnizThread thread = new LeibnizThread(index, index + sectionStep+1);
            leibnizThreads.add(thread);
            thread.start();
        }

        // sections with sectionStep steps
        for (; index < LEIBNIZ_ITERATIONS; index += sectionStep) {
            LeibnizThread thread = new LeibnizThread(index, index + sectionStep);
            leibnizThreads.add(thread);
            thread.start();
        }

        double result = 0;
        for(LeibnizThread thread : leibnizThreads){
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
            
            result += thread.getResult();
        }

        System.out.println("pi ~ " + result * 4);
    }
}