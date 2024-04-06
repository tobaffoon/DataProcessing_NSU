package Task8.src.main.java.ru.nsu.amazyar;

import java.util.ArrayList;
import java.util.List;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Main {
    public static final long LEIBNIZ_ITERATIONS = 3676L;
    
    private static final List<LeibnizThread> leibnizThreads = new ArrayList<>();

    private static Object interruptSync = new Object();

    public static void main(String[] args) {
        Signal.handle(new Signal ("INT"), new SignalHandler() { 
            public void handle(Signal sig) {
                handleShutdown();
            }
        });

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

        joinLeibniz();
        synchronized(interruptSync){
            System.out.println("pi ~ " + getCalculationsResult() * 4);
            return;
        }
    }

    private static void joinLeibniz(){
        for(LeibnizThread thread : leibnizThreads){
            try {
                thread.join();
            } catch (InterruptedException e) {}
        }
    }

    private static double getCalculationsResult(){
        return leibnizThreads.stream()
            .map(LeibnizThread::getResult)    
            .reduce(Double::sum)
            .orElse(-1.0);
    }

    private static void handleShutdown(){
        synchronized(interruptSync){
            for(LeibnizThread thread : leibnizThreads){
                thread.interrupt();
            }

            System.out.println("Calculations were stopped, here what's got calculated: pi ~ " + getCalculationsResult() * 4);
            System.exit(0);
        }
    }
}