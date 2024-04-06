package Task6.src.main.java.ru.nsu.amazyar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public final class Founder {
    private final CyclicBarrier barrier;

    private final List<Runnable> workers;
    private final Company company;

    public Founder(final Company company) {
        this.company = company;
        this.workers = new ArrayList<>(company.getDepartmentsCount());
        this.barrier = new CyclicBarrier(company.getDepartmentsCount() + 1); // +1 is for the printing in method start()

        //prepare workers
        for(int i = 0; i < company.getDepartmentsCount(); i++){
            Department workersDepartment = this.company.getFreeDepartment(i);
            workers.add(() -> {
                workersDepartment.performCalculations();
                try {
                    this.barrier.await();
                } catch (InterruptedException | BrokenBarrierException ignored) {}
            }
            );
        }
    }

    public void start() {
        for (final Runnable worker : workers) {
            new Thread(worker).start();
        }
        
        try {
            this.barrier.await();
        } catch (InterruptedException | BrokenBarrierException ignored) {}

        this.company.showCollaborativeResult();
    }
}
