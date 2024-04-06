package Task8Inf.src.main.java.ru.nsu.amazyar;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class LeibnizThread extends Thread{
    private final CyclicBarrier barrier;

    private double currentDenominator;
    private final long iterStep;
    private final boolean signAlternates;
    private boolean threadStarted;
    private double result = 0;

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
        
        int i = 0;
        while (true) {
            result += 1 / currentDenominator; // evaluate one value in block 
            
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

            try {
                this.barrier.await();   // wait for current block to evaluate
                System.out.println(++i + " completed step with " + result);
            } catch (InterruptedException e) {
                break;  // if interrupted - stop calculating
            } catch (BrokenBarrierException ignored) {}
        }
    }

    public double getResult(){
        if(!threadStarted){
            throw new RuntimeException("Start thread before asking for result");
        }
        return result;
    }
}
