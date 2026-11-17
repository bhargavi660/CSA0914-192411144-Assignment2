# CSA09-[FILL IN]-Assignment2

- Student name: [FILL IN]
- Register number: [FILL IN]
- Repository name: CSA09-<RegisterNumber>-Assignment2
- Java version: 26.0.2.1 (detected with `java -version`)
- GitHub repository link: [FILL IN]

## Project structure

- Task01/ — String concatenation benchmark and analysis
- Task02/ — ArrayList/LinkedList/ArrayDeque benchmark suite
- Task03/ — HashSet equals/hashCode contract and Attendance fix
- Task04/ — HashMap/TreeMap/LinkedHashMap inventory benchmark
- Task05/ — Generic Leaderboard and Team ranking
- Task06/ — Priority queue triage simulation
- Task07/ — StringBuilder redesign validation
- report/Assignment2-Report.md — full analytical report with measured evidence

## Compile and run

From the project root:

```powershell
javac Task01/*.java
javac Task02/*.java
javac Task03/*.java
javac Task04/*.java
javac Task05/*.java
javac Task06/*.java
javac Task07/*.java
```

### Individual tasks

```powershell
java -cp Task01 Main
java -cp Task02 Main
java -cp Task03 Attendance
java -cp Task04 Main
java -cp Task05 Main
java -cp Task06 Main
java -cp Task07 Main
```

### Full suite

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\compile_all.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\run_all.ps1
```

## Brief result notes

- Task 01: Benchmarks compare `+`, `StringBuilder`, and `StringBuffer`, verify identical output, and save raw CSV results.
- Task 02: Comparable collection benchmark results are generated for add/insert/remove/access/iteration operations.
- Task 03: The corrected set implementation produces two unique students and the `Attendance` output matches the required value.
- Task 04: Lookup and report-order benchmarks are generated for `HashMap`, `TreeMap`, and `LinkedHashMap`.
- Task 05: Leaderboard ordering correctly ranks teams by points, goal difference, and name.
- Task 06: Triage admissions produce the expected treatment order.
- Task 07: A focused redesign is measured against the baseline and the report records whether the improvement is observed on this machine.

## Manual items to fill in

- Student name
- Register number
- GitHub repository URL
- Optional machine details, if the user wants them in the report

## Evidence collection

The project writes raw CSV files under each task folder as part of the benchmark runs. Keep the raw files and do not overwrite them with averages only.
