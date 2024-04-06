package Task12.src.main.java.ru.nsu.amazyar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class Main {
    private static LinkedList<String> strings = new LinkedList<>();
    public static void main(String[] args) throws IOException {
        BufferedReader buffReader = new BufferedReader(new InputStreamReader(System.in));

        Thread sorter = createSorterThread();
        sorter.start();
        while(true){
            String nextLine = buffReader.readLine();
            if(nextLine.equals("")){
                synchronized(strings){
                    System.out.println(strings);
                }
                continue;
            }

            while(nextLine.length() > 80){
                synchronized(strings){
                    strings.add(nextLine.substring(0, 80));
                }
                nextLine = nextLine.substring(80);
            }

            synchronized(strings){
                strings.add(nextLine);
            }
        }
    }

    private static Thread createSorterThread(){
        return new Thread(() -> {  
            boolean swapped;

            while(true){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
                
                synchronized(strings){
                    System.out.println("HALT: the list is being sorted");
                    for (int i = 0; i < strings.size() - 1; i++) {
                        swapped = false;
                        for(int j = 0; j < strings.size() - 1 - i; j++){
                            if(strings.get(j).compareTo(strings.get(j+1)) > 0){
                                String temp = strings.get(j);
                                strings.set(j, strings.get(j+1));
                                strings.set(j+1, temp);
                                swapped = true;
                            }
                        }
                        
                        if(swapped == false){
                            break;
                        }
                    }
                }
            }

        });
    }
}