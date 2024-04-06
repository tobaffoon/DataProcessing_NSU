package Task8Inf.src.main.java.ru.nsu.amazyar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Main {    
    private static final List<LeibnizThread> leibnizThreads = new ArrayList<>();
    private static CyclicBarrier barrier;

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        Signal.handle(new Signal ("INT"), new SignalHandler() { 
            public void handle(Signal sig) {
                handleShutdown();
            }
        });

        int threadsNumber;
        if(args.length == 0){
            System.err.println("Pass a number of desired threads");
            return;
        }
        try{
            threadsNumber = Integer.parseInt(args[0]);
        }catch(NumberFormatException e){
            System.err.println("Cannot parse a number");
            return;
        }
        if(threadsNumber <= 0){
            System.err.println("Pass a number greater than 0");
            return;
        }

        barrier = new CyclicBarrier(threadsNumber);
        // barrier = new CyclicBarrier(threadsNumber + 1);
        for (int i = 0; i < threadsNumber; i ++) {
            LeibnizThread thread = new LeibnizThread(i, threadsNumber, barrier);
            leibnizThreads.add(thread);
            thread.start();
        }

        while(true){
            // Thread.sleep(1);
            // System.out.println(getCalculationsResult()*4);
            // barrier.await();
        }
    }

    private static double getCalculationsResult(){
        return leibnizThreads.stream()
            .map(LeibnizThread::getResult)    
            .reduce(Double::sum)
            .orElse(-1.0);
    }
    private static void handleShutdown(){
        for(LeibnizThread thread : leibnizThreads){
            thread.interrupt();
        }
        
        System.out.println("Calculations were stopped, here what's got calculated: pi ~ " + getCalculationsResult() * 4);
        System.exit(0);
    }
}