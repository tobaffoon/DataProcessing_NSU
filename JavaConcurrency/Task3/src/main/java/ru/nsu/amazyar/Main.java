package Task3.src.main.java.ru.nsu.amazyar;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        Thread printerThread1 = createPrintListThread(strings);
        Thread printerThread2 = createPrintListThread(strings);
        Thread printerThread3 = createPrintListThread(strings);
        Thread printerThread4 = createPrintListThread(strings);

        // first thread
        strings.add("0");
        strings.add("0");
        strings.add("1");
        printerThread1.start();

        try {
            Thread.sleep(1);
        } catch (InterruptedException ignored) {}

        // second thread 
        strings.set(1, "1");
        strings.set(2, "0");
        printerThread2.start();

        try {
            Thread.sleep(1);
        } catch (InterruptedException ignored) {}

        // third thread
        strings.set(2, "1");
        printerThread3.start();
        
        try {
            Thread.sleep(1);
        } catch (InterruptedException ignored) {}

        // forth thread
        strings.set(0, "1");
        strings.set(1, "0");
        strings.set(2, "0");
        printerThread4.start();
    }

    public static Thread createPrintListThread(List<String> sList){
        List<String> localList = List.copyOf(sList);
        return new Thread(() -> {
                    System.out.print(Thread.currentThread().getName() + " printed ");
                for (String string : localList) {
                    System.out.print(string);
                }
                System.out.println();
            });
    }
}