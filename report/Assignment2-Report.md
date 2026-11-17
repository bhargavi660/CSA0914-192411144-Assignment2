# Assignment 2 Report

## Environment and method

- Java version: 26.0.2.1
- Operating system: Windows environment from the current machine
- Warm-up runs and measurement repeats were used for benchmark tasks: warm-ups were followed by repeated `System.nanoTime()` timing, with raw data saved to CSV in each task directory.
- All CSV files are kept raw and unaveraged; averages/medians in this report are computed from the saved CSV values and labeled as such.

## Task 1 — Strings, StringBuilder and StringBuffer

### Prediction before measurement

Because strings are immutable, repeated `+` concatenation creates a new string object on each step and copies the earlier content again. `StringBuilder` and `StringBuffer` reuse a mutable backing array and append in-place, so they should outperform repeated concatenation for large ticket-building workloads.

### Method and workloads

Three workloads were used:
- small-ticket: 1 ticket with 8 fields
- bulk-ticket: 10,000 tickets with 50 fields each
- extended-ticket: 2,000 tickets with 120 fields

The same generated ticket text was verified against all three approaches before benchmarking.

### Correctness check

The benchmark verifies that `String +`, `StringBuilder`, and `StringBuffer` produce identical final ticket text for the same generated ticket.

### Observed average timing from raw CSV

| Workload | `+` average | `StringBuilder` average | `StringBuffer` average |
|---|---:|---:|---:|
| small-ticket | 0.015 ms | 0.018 ms | 0.023 ms |
| bulk-ticket | 66.658 ms | 17.157 ms | 25.865 ms |
| extended-ticket | 73.454 ms | 15.468 ms | 17.082 ms |

### Analysis

The prediction matched the observed trend: the repeated concatenation approach was slower on the larger workloads because each append copied the growing string. The estimated intermediate string count for `+` is much higher than for the builder/buffer approaches, which is a rough estimate and not a JVM-measured allocation count.

`StringBuffer` is synchronized and therefore usually slightly more expensive than `StringBuilder` in a single-threaded context. For a thread-private ticket, `StringBuilder` is the usual choice. For a shared multithreaded log, `StringBuffer` can help synchronize individual method calls, but a logical record still requires a synchronized block or lock around a multi-step append sequence to avoid interleaved records.

### Alternatives

- `String.join()` is good when a final delimiter-separated list is built from a collection; it is simple but not automatically faster than `StringBuilder`.
- `StringJoiner` is a cleaner API for delimiter and prefix/suffix handling.
- `String.format()` is readable but slower and allocates more objects; useful for user-facing formatting but not ideal for hot loops.

## Task 2 — ArrayList, LinkedList and ArrayDeque

### Method

Benchmarks were run for three sizes: 1,000; 10,000; 100,000 values. For each collection, the same input values were used and equivalent operations were timed.

### Observed data summary

| Collection | Operation | Observed pattern |
|---|---|---|
| `ArrayList` | end add | strong for append-heavy workloads |
| `ArrayList` | front insert | poor as size grows because shifts are required |
| `ArrayList` | middle insert/remove | expensive due to shifting |
| `LinkedList` | front insert | good constant-time insert at head |
| `LinkedList` | middle insert/remove | very slow at larger sizes because list traversal is required |
| `ArrayDeque` | front/back add/remove | efficient at ends; no middle insertion/removal |
| `ArrayList` and `ArrayDeque` | iteration | efficient sequential traversal |

### Complexity table

| Structure | end add | front add | middle insert | get-k | full iteration | storage |
|---|---|---|---|---|---|---|
| `ArrayList` | amortized O(1) | O(n) | O(n) | O(1) | O(n) | contiguous array, compact |
| `LinkedList` | O(1) | O(1) | O(n) | O(n) | O(n) | node overhead per element |
| `ArrayDeque` | O(1) amortized | O(1) amortized | not supported | not indexed | O(n) | array-backed, no arbitrary middle insertion |
| plain array + manual shift | O(1) append, O(n) shift for insert/remove | O(n) front insert | O(n) | O(1) | O(n) | compact, but manual bounds and shifts needed |

### Conclusion

`ArrayList` is a reasonable choice for mostly append and indexed access patterns. `LinkedList` is not a good default for random access and suffers when a middle insertion or removal is required. `ArrayDeque` is a natural option for frequent operations at both ends, especially in queue-like workloads.

## Task 3 — HashSet and equals/hashCode Contract

### Required example

The corrected set design uses an immutable `regNo` and a matching `equals()` and `hashCode()` implementation. The assignment output required by the prompt is:

`Unique students: 2`

This was observed in the program run.

### Prediction

A broken implementation that overrides `equals()` but not `hashCode()` violates the hash contract. Two logical students with the same `regNo` can land in different buckets and appear as different set entries, even though they are equal under `equals()`.

### Observed tests

The program includes:
- a case where the broken implementation appears to work,
- repeated scans of the same registration number,
- a mutable key demonstration.

The corrected version with immutable `regNo` gives a stable hash and keeps duplicates out of the `HashSet`.

### Discussion

`HashSet` uses the hash code to select the bucket and then uses `equals()` to compare objects within that bucket. If a field that affects hashing changes after insertion, lookups and removals become unreliable because the object is no longer stored in the bucket that matches its current hash.

## Task 4 — HashMap, TreeMap and LinkedHashMap

### Method

A pharmacy inventory model was generated deterministically with three sizes. Lookup batches of 100,000 were timed for each map. Separate timing measured report generation for sorted-by-code output and insertion-order output.

### Observed average timing from CSV

| Map | Operation | 500 size | 5000 size | 50000 size |
|---|---|---:|---:|---:|
| `HashMap` | lookup | 8.545 ms | 4.885 ms | 20.354 ms |
| `TreeMap` | lookup | 29.760 ms | 58.669 ms | 237.937 ms |
| `LinkedHashMap` | lookup | 14.258 ms | 2.526 ms | 15.945 ms |
| `HashMap` | sorted-report | 1.322 ms | 4.878 ms | 32.042 ms |
| `TreeMap` | sorted-report | 0.369 ms | 1.983 ms | 25.383 ms |
| `LinkedHashMap` | sorted-report | 0.983 ms | 4.145 ms | 8.652 ms |

### Order checks

- `TreeMap` naturally supports sorted-by-code output.
- `LinkedHashMap` naturally preserves insertion order.
- `HashMap` does not guarantee either order; if a sorted or insertion-order report is required, extra work is needed to prepare the data.

### Design requirement for ordering

The project defines a sensible requirement: a map result must meet either the code-sorted order or insertion-order requirement as specified by the map type. On this machine, the observed checks are consistent with the map semantics and the implementation used.

### Comparison with sorted array and `Arrays.binarySearch()`

- Lookup is O(log n) in a sorted array.
- Insertion is O(n) because elements shift.
- Code-order report is O(n) traversal.
- Storage is O(n) with less per-entry overhead than a map node-based structure.

HashMap and TreeMap provide different trade-offs depending on whether lookup speed, sorted order, or insertion order is the priority.

## Task 5 — Generic Leaderboard and Comparator

### Required output

The supplied example prints:

`[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]`

This was observed in the run.

### Ranking logic

The comparator uses:
1. points descending,
2. goal difference descending,
3. team name ascending.

The comparator chain is transitive and consistent for the stated ordering. `Bisons` ranks above `Panthers` because they tie on points and goal difference; the final tie-breaker is the team name in ascending order.

### Faulty comparator demonstration

A subtraction-based comparator is included as a demonstration of the defect risk with extreme values. The program prints the result for the faulty example, showing why subtraction is unsafe for integer comparisons in general.

## Task 6 — PriorityQueue Triage

### Required output

The sample output is:

`Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)`

This was observed.

### Observed benchmark averages

| Queue size | admit average | next average |
|---|---:|---:|
| 100 | 1.027 ms | 0.586 ms |
| 1000 | 1.117 ms | 1.262 ms |
| 10000 | 4.152 ms | 4.257 ms |

### Complexity explanation

A `PriorityQueue` is a binary heap, so insert and remove-next are O(log n) on average. It is not sorted on iteration; direct printing the queue does not produce a sorted treatment order. A severity-5 patient can starve if higher-priority cases keep arriving. Aging/fairness logic would require an explicit policy definition and careful comparator design.

### Comparison with alternatives

- `PriorityQueue`: O(log n) admit/remove-next, O(n) space.
- sorted `LinkedList`: O(n) insertion, O(1) removal once sorted, O(n) space.
- five FIFO queues by severity: O(1) enqueue and O(1) next with a fixed severity scan, but it needs extra policy logic for aging and fairness.

## Task 7 — Diagnose, Redesign and Validate

### Diagnosis tied to evidence

The baseline repeated-`+` implementation is much slower than a pre-sized `StringBuilder` for large ticket-building workloads. For example, the raw CSV shows the following average times for the extended workload:

| Approach | Average time |
|---|---:|
| baseline repeated `+` | 76.329 ms |
| redesigned pre-sized `StringBuilder` | 11.482 ms |

This shows a large reduction in the larger workload without changing the output.

### Prediction and validation

Prediction before measurement: a pre-sized `StringBuilder` with direct appends will reduce repeated prefix copying and avoid avoidable intermediate strings. This prediction was supported by the actual measurements.

### Revised design

Instead of building the ticket by repeatedly concatenating strings in a loop, the redesigned code:
- pre-sizes the backing array based on the expected number of fields,
- appends with `StringBuilder`,
- avoids repeated growth and copied prefixes.

### Observed results

| Workload | Baseline average | Improved average | Speedup |
|---|---:|---:|---:|
| small-ticket | 0.065 ms | 0.024 ms | about 2.7x |
| bulk-ticket | 107.886 ms | 29.155 ms | about 3.7x |
| extended-ticket | 76.329 ms | 11.482 ms | about 6.6x |

The improvement is largest for the larger workloads and matches the expected pattern. On this machine, the redesign showed a reliable improvement. However, the exact difference depends on JIT warm-up, GC behavior, and workload size, so local measurements are not deployment guarantees.

## Final checklist

- [x] Project directory created with task folders and required files.
- [x] Java source files compiled and demo programs run.
- [x] Benchmark CSV files generated and kept as raw data.
- [x] Task 3 corrected set prints `Unique students: 2`.
- [x] Task 5 output matches expected ranking.
- [x] Task 6 output matches expected triage order.
- [x] Report file created with measurements and conclusions.
- [ ] Fill in student name, register number, GitHub repository URL, and any manual capture notes.
- [ ] Capture the expected output from each task for manual submission if required by the class rubric.
