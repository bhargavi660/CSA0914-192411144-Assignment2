import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Main {
    private static final int[] SIZES = {1000, 10000, 100000};
    private static final int WARMUP_RUNS = 3;
    private static final int MEASUREMENT_RUNS = 6;
    private static final String CSV_ROOT_PATH = "Task02/collection-benchmark.csv";
    private static final String CSV_LOCAL_PATH = "collection-benchmark.csv";

    public static void main(String[] args) throws IOException {
        List<String> csv = new ArrayList<>();
        csv.add("collection,operation,size,repetition,elapsed_ns");

        for (int size : SIZES) {
            List<Integer> values = generateValues(size);
            csv.addAll(runCollectionCase("ArrayList", size, values, false));
            csv.addAll(runCollectionCase("LinkedList", size, values, false));
            csv.addAll(runCollectionCase("ArrayDeque", size, values, true));
        }

        Path csvFile = resolveCsvPath();
        Path parent = csvFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(csvFile, String.join(System.lineSeparator(), csv) + System.lineSeparator());
        System.out.println("Saved raw benchmark data to " + csvFile);
    }

    private static Path resolveCsvPath() {
        Path rootCandidate = Path.of(CSV_ROOT_PATH);
        if (Files.exists(rootCandidate.getParent())) {
            return rootCandidate;
        }
        return Path.of(CSV_LOCAL_PATH);
    }

    private static List<Integer> generateValues(int size) {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            values.add(i * 17 + 3);
        }
        return values;
    }

    private static List<String> runCollectionCase(String collectionName, int size, List<Integer> baseValues, boolean dequeCase) {
        List<String> rows = new ArrayList<>();
        String[] operations = {"add-end", "insert-front", "insert-middle", "remove-front", "remove-middle", "access-kth", "iterate-full"};

        for (String operation : operations) {
            if (dequeCase && ("insert-middle".equals(operation) || "remove-middle".equals(operation))) {
                continue;
            }
            for (int rep = 0; rep < WARMUP_RUNS + MEASUREMENT_RUNS; rep++) {
                long start = System.nanoTime();
                if ("add-end".equals(operation)) {
                    measureAddEnd(collectionName, baseValues);
                } else if ("insert-front".equals(operation)) {
                    measureInsertFront(collectionName, baseValues);
                } else if ("insert-middle".equals(operation)) {
                    measureInsertMiddle(collectionName, baseValues);
                } else if ("remove-front".equals(operation)) {
                    measureRemoveFront(collectionName, baseValues);
                } else if ("remove-middle".equals(operation)) {
                    measureRemoveMiddle(collectionName, baseValues);
                } else if ("access-kth".equals(operation)) {
                    measureAccessKth(collectionName, baseValues);
                } else if ("iterate-full".equals(operation)) {
                    measureIterate(collectionName, baseValues);
                }
                long elapsed = System.nanoTime() - start;
                if (rep >= WARMUP_RUNS) {
                    rows.add(collectionName + "," + operation + "," + size + "," + (rep - WARMUP_RUNS) + "," + elapsed);
                }
            }
        }
        return rows;
    }

    private static void measureAddEnd(String collectionName, List<Integer> values) {
        if ("ArrayList".equals(collectionName)) {
            ArrayList<Integer> list = new ArrayList<>();
            list.addAll(values);
        } else if ("LinkedList".equals(collectionName)) {
            LinkedList<Integer> list = new LinkedList<>();
            list.addAll(values);
        } else if ("ArrayDeque".equals(collectionName)) {
            Deque<Integer> dq = new ArrayDeque<>();
            for (Integer value : values) {
                dq.addLast(value);
            }
        }
    }

    private static void measureInsertFront(String collectionName, List<Integer> values) {
        if ("ArrayList".equals(collectionName)) {
            ArrayList<Integer> list = new ArrayList<>();
            for (Integer value : values) {
                list.add(0, value);
            }
        } else if ("LinkedList".equals(collectionName)) {
            LinkedList<Integer> list = new LinkedList<>();
            for (Integer value : values) {
                list.addFirst(value);
            }
        } else if ("ArrayDeque".equals(collectionName)) {
            ArrayDeque<Integer> dq = new ArrayDeque<>();
            for (Integer value : values) {
                dq.addFirst(value);
            }
        }
    }

    private static void measureInsertMiddle(String collectionName, List<Integer> values) {
        if ("ArrayList".equals(collectionName)) {
            ArrayList<Integer> list = new ArrayList<>();
            for (Integer value : values) {
                list.add(list.size() / 2, value);
            }
        } else if ("LinkedList".equals(collectionName)) {
            LinkedList<Integer> list = new LinkedList<>();
            for (Integer value : values) {
                list.add(list.size() / 2, value);
            }
        }
    }

    private static void measureRemoveFront(String collectionName, List<Integer> values) {
        if ("ArrayList".equals(collectionName)) {
            ArrayList<Integer> list = new ArrayList<>(values);
            while (!list.isEmpty()) {
                list.remove(0);
            }
        } else if ("LinkedList".equals(collectionName)) {
            LinkedList<Integer> list = new LinkedList<>(values);
            while (!list.isEmpty()) {
                list.removeFirst();
            }
        } else if ("ArrayDeque".equals(collectionName)) {
            ArrayDeque<Integer> dq = new ArrayDeque<>(values);
            while (!dq.isEmpty()) {
                dq.removeFirst();
            }
        }
    }

    private static void measureRemoveMiddle(String collectionName, List<Integer> values) {
        if ("ArrayList".equals(collectionName)) {
            ArrayList<Integer> list = new ArrayList<>(values);
            while (!list.isEmpty()) {
                list.remove(list.size() / 2);
            }
        } else if ("LinkedList".equals(collectionName)) {
            LinkedList<Integer> list = new LinkedList<>(values);
            while (!list.isEmpty()) {
                list.remove(list.size() / 2);
            }
        }
    }

    private static void measureAccessKth(String collectionName, List<Integer> values) {
        if ("ArrayList".equals(collectionName)) {
            ArrayList<Integer> list = new ArrayList<>(values);
            int target = list.size() / 2;
            int total = 0;
            for (int i = 0; i < 100; i++) {
                total += list.get(target);
            }
            if (total == 0) {
                throw new IllegalStateException("Dummy check failed");
            }
        } else if ("LinkedList".equals(collectionName)) {
            LinkedList<Integer> list = new LinkedList<>(values);
            int target = list.size() / 2;
            int total = 0;
            for (int i = 0; i < 100; i++) {
                total += list.get(target);
            }
            if (total == 0) {
                throw new IllegalStateException("Dummy check failed");
            }
        } else if ("ArrayDeque".equals(collectionName)) {
            ArrayDeque<Integer> dq = new ArrayDeque<>(values);
            int total = 0;
            int idx = 0;
            for (Integer value : dq) {
                if (idx++ == dq.size() / 2) {
                    total += value;
                    break;
                }
            }
            if (total == 0) {
                throw new IllegalStateException("Dummy check failed");
            }
        }
    }

    private static void measureIterate(String collectionName, List<Integer> values) {
        if ("ArrayList".equals(collectionName)) {
            ArrayList<Integer> list = new ArrayList<>(values);
            int total = 0;
            for (int value : list) {
                total += value;
            }
            if (total == 0) {
                throw new IllegalStateException("Dummy check failed");
            }
        } else if ("LinkedList".equals(collectionName)) {
            LinkedList<Integer> list = new LinkedList<>(values);
            int total = 0;
            for (int value : list) {
                total += value;
            }
            if (total == 0) {
                throw new IllegalStateException("Dummy check failed");
            }
        } else if ("ArrayDeque".equals(collectionName)) {
            ArrayDeque<Integer> dq = new ArrayDeque<>(values);
            int total = 0;
            for (int value : dq) {
                total += value;
            }
            if (total == 0) {
                throw new IllegalStateException("Dummy check failed");
            }
        }
    }
}
