package Task8InfShared.src.main.java.ru.nsu.amazyar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Main {    
    private static final List<LeibnizThread> leibnizThreads = new ArrayList<>();
    private static CyclicBarrier barrier;
    private static int maxBlockCounted = 0;

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

        barrier = new CyclicBarrier(threadsNumber + 1); // + 1 for Main thread
        for (int i = 0; i < threadsNumber; i ++) {
            LeibnizThread thread = new LeibnizThread(i, threadsNumber, barrier);
            leibnizThreads.add(thread);
            thread.start();
        }
    }

    private static void handleShutdown(){
        for(LeibnizThread thread : leibnizThreads){
            thread.interrupt();
        }
        
        maxBlockCounted = leibnizThreads.stream().map(LeibnizThread::getBlocksCounted).max(Comparator.naturalOrder()).orElse(0);
        
        // wait for threads to enter interrup handler
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ignored) {}   
    
        // wait for all threads to finish calculating
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ignored) {}

        System.out.println("Calculations were stopped, here what's got calculated: pi ~ " + String.valueOf(getCalculationsResult() * 4));
        System.exit(0);
    }

    public static int getMaxBlockCounted(){
        return maxBlockCounted;
    }

    private static double getCalculationsResult(){
        return leibnizThreads.stream()
            .map(LeibnizThread::getResultAsync)    
            .reduce(Double::sum)
            .orElse(-1.0);
    }
}