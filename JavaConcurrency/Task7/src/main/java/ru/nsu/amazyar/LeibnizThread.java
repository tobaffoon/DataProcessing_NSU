package Task7.src.main.java.ru.nsu.amazyar;

public class LeibnizThread extends Thread{
    private final long startingIndex, endingIndex;
    private double result = 0;
    private boolean threadStarted = false;
    public LeibnizThread(long startingIndex, long endingIndex) {
        this.startingIndex = startingIndex;
        this.endingIndex = endingIndex;
    }

    @Override
    public void run() {
        threadStarted = true;
        double currentDenominatorAbsolute = 1 + 2 * (startingIndex);
        boolean currentDenominatorIsPositive = true;
        if(startingIndex % 2 == 1) currentDenominatorIsPositive = false;
        for (long i = startingIndex; i < endingIndex; i++) {
            if(currentDenominatorIsPositive){
                result += 1 / currentDenominatorAbsolute;
                currentDenominatorIsPositive = false;
            }
            else{
                result -= 1 / currentDenominatorAbsolute;
                currentDenominatorIsPositive = true;
            }
            currentDenominatorAbsolute += 2;
        }
    }

    public double getResult(){
        if(!threadStarted){
            throw new RuntimeException("Start thread before asking for result");
        }
        return result;
    }
}
