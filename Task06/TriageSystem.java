import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class TriageSystem {
    private final PriorityQueue<Patient> waitingQueue;
    private int nextArrivalNumber = 1;

    public TriageSystem() {
        this.waitingQueue = new PriorityQueue<>(Comparator
                .comparingInt(Patient::severity)
                .thenComparingInt(Patient::arrivalNumber));
    }

    public void admit(String name, int severity) {
        waitingQueue.add(new Patient(name, severity, nextArrivalNumber++));
    }

    public Patient next() {
        return waitingQueue.poll();
    }

    public List<Patient> treatmentOrder() {
        PriorityQueue<Patient> snapshot = new PriorityQueue<>(waitingQueue.comparator());
        snapshot.addAll(waitingQueue);
        List<Patient> ordered = new ArrayList<>();
        while (!snapshot.isEmpty()) {
            ordered.add(snapshot.poll());
        }
        return ordered;
    }

    public static void main(String[] args) throws IOException {
        TriageSystem triage = new TriageSystem();
        triage.admit("Meena", 1);
        triage.admit("Kumar", 1);
        triage.admit("Lata", 2);
        triage.admit("Ravi", 3);
        triage.admit("John", 3);
        triage.admit("Asha", 5);

        List<String> output = new ArrayList<>();
        while (!triage.waitingQueue.isEmpty()) {
            output.add(triage.next().toString());
        }
        System.out.println(String.join(" ", output));

        benchmark();
    }

    private static final String CSV_ROOT_PATH = "Task06/triage-benchmark.csv";
    private static final String CSV_LOCAL_PATH = "triage-benchmark.csv";

    private static void benchmark() throws IOException {
        List<String> csv = new ArrayList<>();
        csv.add("queueSize,operation,repetition,elapsed_ns");
        int[] sizes = {100, 1000, 10000};
        for (int size : sizes) {
            for (int rep = 0; rep < 8; rep++) {
                TriageSystem system = new TriageSystem();
                long startAdd = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    system.admit("P" + i, (i % 5) + 1);
                }
                long addElapsed = System.nanoTime() - startAdd;
                csv.add(size + ",admit," + rep + "," + addElapsed);

                long startNext = System.nanoTime();
                int removed = 0;
                while (!system.waitingQueue.isEmpty() && removed < size) {
                    system.next();
                    removed++;
                }
                long nextElapsed = System.nanoTime() - startNext;
                csv.add(size + ",next," + rep + "," + nextElapsed);
            }
        }
        Path csvFile = resolveCsvPath();
        Path parent = csvFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(csvFile, String.join(System.lineSeparator(), csv) + System.lineSeparator());
        System.out.println("Task 06 benchmark CSV saved to " + csvFile);
    }

    private static Path resolveCsvPath() {
        Path rootCandidate = Path.of(CSV_ROOT_PATH);
        if (Files.exists(rootCandidate.getParent())) {
            return rootCandidate;
        }
        return Path.of(CSV_LOCAL_PATH);
    }

    public record Patient(String name, int severity, int arrivalNumber) {
        @Override
        public String toString() {
            return name + "(S" + severity + ")";
        }
    }
}
