import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final String CSV_ROOT_PATH = "Task07/string-redesign-benchmark.csv";
    private static final String CSV_LOCAL_PATH = "string-redesign-benchmark.csv";
    private static final int WARMUP_RUNS = 3;
    private static final int MEASUREMENT_RUNS = 8;

    public static void main(String[] args) throws IOException {
        verifyIdenticalOutput();
        List<String> csvRows = new ArrayList<>();
        csvRows.add("workload,approach,repetition,elapsed_ns");
        runWorkload("small-ticket", 1, 8, csvRows);
        runWorkload("bulk-ticket", 10000, 50, csvRows);
        runWorkload("extended-ticket", 2000, 120, csvRows);
        Path csvFile = resolveCsvPath();
        Path parent = csvFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(csvFile, String.join(System.lineSeparator(), csvRows) + System.lineSeparator());
        System.out.println("Task 7 compare baseline and redesigned builder saved to " + csvFile);
    }

    private static Path resolveCsvPath() {
        Path rootCandidate = Path.of(CSV_ROOT_PATH);
        if (Files.exists(rootCandidate.getParent())) {
            return rootCandidate;
        }
        return Path.of(CSV_LOCAL_PATH);
    }

    private static void verifyIdenticalOutput() {
        List<String> fields = generateTicketFields(5, 12);
        String baseline = buildBaseline(fields);
        String improved = buildPreSizedBuilder(fields);
        if (!baseline.equals(improved)) {
            throw new IllegalStateException("Baseline and redesigned output differ.");
        }
        System.out.println("Verification: baseline and redesigned builders produce identical output.");
    }

    private static void runWorkload(String workloadName, int ticketCount, int fieldCount, List<String> csvRows) throws IOException {
        List<List<String>> tickets = new ArrayList<>();
        for (int i = 0; i < ticketCount; i++) {
            tickets.add(generateTicketFields(i, fieldCount));
        }

        for (String approach : Arrays.asList("baseline", "improved")) {
            for (int warm = 0; warm < WARMUP_RUNS; warm++) {
                executeApproach(approach, tickets);
            }
            for (int rep = 0; rep < MEASUREMENT_RUNS; rep++) {
                long start = System.nanoTime();
                executeApproach(approach, tickets);
                long elapsed = System.nanoTime() - start;
                csvRows.add(workloadName + "," + approach + "," + rep + "," + elapsed);
            }
        }

        System.out.printf("%s summary: baseline and improved builder data saved.%n", workloadName);
    }

    private static String executeApproach(String approach, List<List<String>> tickets) {
        String combined = "";
        for (List<String> fields : tickets) {
            if ("baseline".equals(approach)) {
                combined = buildBaseline(fields);
            } else {
                combined = buildPreSizedBuilder(fields);
            }
        }
        return combined;
    }

    private static List<String> generateTicketFields(int ticketNo, int fieldCount) {
        List<String> fields = new ArrayList<>();
        for (int i = 0; i < fieldCount; i++) {
            fields.add("T" + ticketNo + "|F" + i + "|V" + ((ticketNo * 89 + i * 17) % 21_000));
        }
        return fields;
    }

    private static String buildBaseline(List<String> fields) {
        String result = "";
        for (String field : fields) {
            result += field + "|";
        }
        return result;
    }

    private static String buildPreSizedBuilder(List<String> fields) {
        StringBuilder builder = new StringBuilder(fields.size() * 24);
        for (String field : fields) {
            builder.append(field).append('|');
        }
        return builder.toString();
    }
}
