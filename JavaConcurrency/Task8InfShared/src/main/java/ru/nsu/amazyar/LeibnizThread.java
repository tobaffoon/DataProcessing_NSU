package Task8InfShared.src.main.java.ru.nsu.amazyar;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class LeibnizThread extends Thread{
    private CyclicBarrier barrier;

    private double currentDenominator;
    private final long iterStep;
    private final boolean signAlternates;
    private boolean threadStarted;
    private double result = 0.0;

    private int blocksCounted = 0;
    private Object blocksCountedSync = new Object();

    /**
     * Constructor.
     * @param blockId thread's index in the block
     * @param blockSize number of threads calculating the series
     */
    public LeibnizThread(int blockId, int blockSize, CyclicBarrier barrier) {
        this.currentDenominator = 1 + 2 * blockId;  // 0 -> 1; 1 -> 3; 2 -> 5...
        if(blockSize % 2 == 0){
            this.signAlternates = false;
        }
        else{
            this.signAlternates = true;
        }

        // every second memeber is negative
        if(blockId % 2 == 1){
            this.currentDenominator *= -1;
        }
        this.iterStep = 2 * blockSize;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        threadStarted = true;
        
        while (true) {
            if(Thread.interrupted()){
                handleInterrupt();
            }

            // Make sure that getblocksCounted does not get the wrong number
            synchronized(blocksCountedSync){                
                calculateNextMember();
                blocksCounted++;
            }
        }
    }

    private void calculateNextMember(){
        result += 1.0 / currentDenominator; // evaluate one value in block
        
        // prepare for the next block
        if(signAlternates){
            currentDenominator *= -1;
        }

        if(currentDenominator < 0){
            currentDenominator -= iterStep;
        }
        else{
            currentDenominator += iterStep;
        }
    }

    public double getResultAsync(){
        if(!threadStarted){
            throw new RuntimeException("Start thread before asking for result");
        }
        return result;
    }

    public int getBlocksCounted(){
        synchronized(blocksCountedSync){
            return blocksCounted;
        }
    }

    private void handleInterrupt(){
        // wait for max blocksCounted among threads to be calculated
        try {
            this.barrier.await();
        } catch (InterruptedException | BrokenBarrierException ignored) {}

        int maxBlockIdx = Main.getMaxBlockCounted();
        while(blocksCounted < maxBlockIdx){
            calculateNextMember();
            blocksCounted++;
        }
        // wait for all threads to finish calculating
        try {
            this.barrier.await();
        } catch (InterruptedException | BrokenBarrierException ignored) {}
        
        System.exit(0);
    }
}
