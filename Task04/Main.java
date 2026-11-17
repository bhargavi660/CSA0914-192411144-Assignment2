import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Main {
    private static final int[] SIZES = {500, 5000, 50000};
    private static final int LOOKUP_BATCH = 100_000;
    private static final int WARMUPS = 3;
    private static final int MEASUREMENTS = 8;
    private static final String CSV_ROOT_PATH = "Task04/map-benchmark.csv";
    private static final String CSV_LOCAL_PATH = "map-benchmark.csv";

    public static void main(String[] args) throws IOException {
        List<String> csv = new ArrayList<>();
        csv.add("mapType,size,operation,repetition,elapsed_ns");

        for (int size : SIZES) {
            List<Medicine> inventory = generateInventory(size);
            List<String> lookupCodes = generateLookupCodes(inventory, 1_000_000L);

            Map<String, Medicine> hashMap = buildMap("HashMap", inventory);
            Map<String, Medicine> treeMap = buildMap("TreeMap", inventory);
            Map<String, Medicine> linkedHashMap = buildMap("LinkedHashMap", inventory);

            csv.addAll(measureMapLookups("HashMap", size, hashMap, lookupCodes));
            csv.addAll(measureMapLookups("TreeMap", size, treeMap, lookupCodes));
            csv.addAll(measureMapLookups("LinkedHashMap", size, linkedHashMap, lookupCodes));

            csv.addAll(measureReportSort("HashMap", size, hashMap));
            csv.addAll(measureReportSort("TreeMap", size, treeMap));
            csv.addAll(measureReportSort("LinkedHashMap", size, linkedHashMap));

            csv.addAll(measureReportInsertionOrder("HashMap", size, hashMap));
            csv.addAll(measureReportInsertionOrder("TreeMap", size, treeMap));
            csv.addAll(measureReportInsertionOrder("LinkedHashMap", size, linkedHashMap));

            List<String> insertionOrder = new ArrayList<>();
            for (Medicine medicine : inventory) {
                insertionOrder.add(medicine.code);
            }
            printOrderChecks("HashMap", hashMap, insertionOrder);
            printOrderChecks("TreeMap", treeMap, insertionOrder);
            printOrderChecks("LinkedHashMap", linkedHashMap, insertionOrder);
        }

        Path csvFile = resolveCsvPath();
        Path parent = csvFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(csvFile, String.join(System.lineSeparator(), csv) + System.lineSeparator());
        System.out.println("Task 4 complete. CSV saved to " + csvFile);
    }

    private static Path resolveCsvPath() {
        Path rootCandidate = Path.of(CSV_ROOT_PATH);
        if (Files.exists(rootCandidate.getParent())) {
            return rootCandidate;
        }
        return Path.of(CSV_LOCAL_PATH);
    }

    private static List<Medicine> generateInventory(int size) {
        List<Medicine> medicines = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String code = "M" + String.format("%05d", i + 1);
            medicines.add(new Medicine(code, "Drug" + i, 50 + (i % 200), 9.5 + (i % 11) * 0.75));
        }
        return medicines;
    }

    private static List<String> generateLookupCodes(List<Medicine> inventory, long seed) {
        List<String> lookupCodes = new ArrayList<>();
        Random random = new Random(seed);
        for (int i = 0; i < LOOKUP_BATCH; i++) {
            Medicine medicine = inventory.get(random.nextInt(inventory.size()));
            lookupCodes.add(medicine.code);
        }
        return lookupCodes;
    }

    private static Map<String, Medicine> buildMap(String mapType, List<Medicine> inventory) {
        if ("HashMap".equals(mapType)) {
            Map<String, Medicine> map = new HashMap<>();
            for (Medicine medicine : inventory) {
                map.put(medicine.code, medicine);
            }
            return map;
        }
        if ("TreeMap".equals(mapType)) {
            Map<String, Medicine> map = new TreeMap<>();
            for (Medicine medicine : inventory) {
                map.put(medicine.code, medicine);
            }
            return map;
        }
        if ("LinkedHashMap".equals(mapType)) {
            Map<String, Medicine> map = new LinkedHashMap<>();
            for (Medicine medicine : inventory) {
                map.put(medicine.code, medicine);
            }
            return map;
        }
        throw new IllegalArgumentException("Unknown map type: " + mapType);
    }

    private static List<String> measureMapLookups(String mapType, int size, Map<String, Medicine> map, List<String> lookups) {
        List<String> rows = new ArrayList<>();
        for (int warm = 0; warm < WARMUPS; warm++) {
            for (String code : lookups) {
                map.get(code);
            }
        }
        for (int rep = 0; rep < MEASUREMENTS; rep++) {
            long start = System.nanoTime();
            for (String code : lookups) {
                map.get(code);
            }
            long elapsed = System.nanoTime() - start;
            rows.add(mapType + "," + size + ",lookup," + rep + "," + elapsed);
        }
        return rows;
    }

    private static List<String> measureReportSort(String mapType, int size, Map<String, Medicine> map) {
        List<String> rows = new ArrayList<>();
        for (int warm = 0; warm < WARMUPS; warm++) {
            sortByCode(map);
        }
        for (int rep = 0; rep < MEASUREMENTS; rep++) {
            long start = System.nanoTime();
            List<String> report = sortByCode(map);
            long elapsed = System.nanoTime() - start;
            if (report.isEmpty()) {
                throw new IllegalStateException("Report is empty");
            }
            rows.add(mapType + "," + size + ",sorted-report," + rep + "," + elapsed);
        }
        return rows;
    }

    private static List<String> measureReportInsertionOrder(String mapType, int size, Map<String, Medicine> map) {
        List<String> rows = new ArrayList<>();
        for (int warm = 0; warm < WARMUPS; warm++) {
            insertionOrderReport(map);
        }
        for (int rep = 0; rep < MEASUREMENTS; rep++) {
            long start = System.nanoTime();
            List<String> report = insertionOrderReport(map);
            long elapsed = System.nanoTime() - start;
            if (report.isEmpty()) {
                throw new IllegalStateException("Insertion report is empty");
            }
            rows.add(mapType + "," + size + ",insertion-order-report," + rep + "," + elapsed);
        }
        return rows;
    }

    private static List<String> sortByCode(Map<String, Medicine> map) {
        List<String> sortedKeys = new ArrayList<>(map.keySet());
        sortedKeys.sort(String::compareTo);
        List<String> report = new ArrayList<>();
        for (String key : sortedKeys) {
            report.add(key + "=" + map.get(key).name);
        }
        return report;
    }

    private static List<String> insertionOrderReport(Map<String, Medicine> map) {
        List<String> report = new ArrayList<>();
        for (Map.Entry<String, Medicine> entry : map.entrySet()) {
            report.add(entry.getKey() + "=" + entry.getValue().name);
        }
        return report;
    }

    private static void printOrderChecks(String mapType, Map<String, Medicine> map, List<String> insertionOrder) {
        List<String> actualKeys = new ArrayList<>(map.keySet());
        List<String> sortedKeys = new ArrayList<>(actualKeys);
        sortedKeys.sort(String::compareTo);
        boolean sortedByCode = actualKeys.equals(sortedKeys) && map instanceof TreeMap;
        boolean insertionRequirementMet = actualKeys.equals(insertionOrder) && map instanceof LinkedHashMap;

        System.out.println(mapType + " sorted-by-code requirement met: " + sortedByCode);
        System.out.println(mapType + " insertion-order requirement met: " + insertionRequirementMet);
    }

    private static final class Medicine {
        final String code;
        final String name;
        final int stock;
        final double price;

        Medicine(String code, String name, int stock, double price) {
            this.code = code;
            this.name = name;
            this.stock = stock;
            this.price = price;
        }
    }
}
