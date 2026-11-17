import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final int WARMUP_RUNS = 3;
    private static final int MEASUREMENT_RUNS = 10;
    private static final String CSV_ROOT_PATH = "Task01/ticket-benchmark.csv";
    private static final String CSV_LOCAL_PATH = "ticket-benchmark.csv";

    public static void main(String[] args) throws Exception {
        verifyResults();
        List<String> csvRows = new ArrayList<>();
        csvRows.add("workload,approach,repetition,elapsed_ns");
        runWorkload("small-ticket", 1, 8, "Short ticket with a few fields", csvRows);
        runWorkload("bulk-ticket", 10000, 50, "10,000 tickets with 50 fields each", csvRows);
        runWorkload("extended-ticket", 2000, 120, "Third workload: 2,000 tickets with 120 fields", csvRows);
        Path csvFile = resolveCsvPath();
        Path parent = csvFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(csvFile, String.join(System.lineSeparator(), csvRows) + System.lineSeparator());
        System.out.println("\nTask 1 benchmark complete. Raw CSV: " + csvFile);
    }

    private static void verifyResults() {
        List<String> sampleFields = generateTicketFields(7, 12);
        String concat = buildWithConcat(sampleFields);
        String builder = buildWithBuilder(sampleFields);
        String buffer = buildWithBuffer(sampleFields);
        if (!concat.equals(builder) || !concat.equals(buffer)) {
            throw new IllegalStateException("Ticket outputs are not identical");
        }
        System.out.println("Verification: all three approaches generated identical ticket text.");
    }

    private static void runWorkload(String workloadName, int ticketCount, int fieldCount, String description, List<String> csvRows) throws IOException {
        System.out.println("\n--- " + workloadName + " ---");
        System.out.println(description);

        List<List<String>> tickets = new ArrayList<>();
        for (int i = 0; i < ticketCount; i++) {
            tickets.add(generateTicketFields(i, fieldCount));
        }

        List<String> approaches = Arrays.asList("string-plus", "string-builder", "string-buffer");

        for (String approach : approaches) {
            long[] timings = new long[MEASUREMENT_RUNS];
            for (int warm = 0; warm < WARMUP_RUNS; warm++) {
                runApproach(approach, tickets);
            }
            for (int rep = 0; rep < MEASUREMENT_RUNS; rep++) {
                long start = System.nanoTime();
                runApproach(approach, tickets);
                long elapsed = System.nanoTime() - start;
                timings[rep] = elapsed;
                csvRows.add(workloadName + "," + approach + "," + rep + "," + elapsed);
            }
            long averageNs = average(timings);
            System.out.printf("%s average: %.3f ms%n", approach, averageNs / 1_000_000.0);
            System.out.printf("Estimated intermediate strings (rough): %d%n",
                estimatedIntermediateStrings(approach, fieldCount));
        }
    }

    private static Path resolveCsvPath() {
        Path rootCandidate = Path.of(CSV_ROOT_PATH);
        if (Files.exists(rootCandidate.getParent())) {
            return rootCandidate;
        }
        return Path.of(CSV_LOCAL_PATH);
    }

    private static long average(long[] values) {
        long total = 0;
        for (long value : values) {
            total += value;
        }
        return total / values.length;
    }

    private static int estimatedIntermediateStrings(String approach, int fieldCount) {
        if ("string-plus".equals(approach)) {
            return fieldCount * (fieldCount + 1) / 2;
        }
        return 2;
    }

    private static String runApproach(String approach, List<List<String>> tickets) {
        String result = "";
        for (List<String> ticketFields : tickets) {
            if ("string-plus".equals(approach)) {
                result = buildWithConcat(ticketFields);
            } else if ("string-builder".equals(approach)) {
                result = buildWithBuilder(ticketFields);
            } else if ("string-buffer".equals(approach)) {
                result = buildWithBuffer(ticketFields);
            } else {
                throw new IllegalArgumentException("Unknown approach: " + approach);
            }
        }
        return result;
    }

    private static List<String> generateTicketFields(int ticketNo, int fieldCount) {
        List<String> fields = new ArrayList<>();
        for (int i = 0; i < fieldCount; i++) {
            fields.add("T" + ticketNo + "|F" + i + "|V" + ((ticketNo * 97 + i * 13) % 10_000));
        }
        return fields;
    }

    private static String buildWithConcat(List<String> fields) {
        String result = "";
        for (String field : fields) {
            result = result + field + "|";
        }
        return result;
    }

    private static String buildWithBuilder(List<String> fields) {
        StringBuilder builder = new StringBuilder(fields.size() * 24);
        for (String field : fields) {
            builder.append(field).append('|');
        }
        return builder.toString();
    }

    private static String buildWithBuffer(List<String> fields) {
        StringBuffer buffer = new StringBuffer(fields.size() * 24);
        for (String field : fields) {
            buffer.append(field).append('|');
        }
        return buffer.toString();
    }
}
