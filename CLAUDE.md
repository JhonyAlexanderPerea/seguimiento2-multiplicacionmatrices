# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package into executable JAR (output: target/seguimiento2-multiplicacionmatrices-1.0-SNAPSHOT-jar-with-dependencies.jar)
mvn clean package

# Run a single test class
mvn test -Dtest=MyTestClass

# Run the application
java -jar target/seguimiento2-multiplicacionmatrices-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Architecture

This is a Java 17/Maven project that implements and benchmarks several matrix multiplication algorithms. The goal is to compare theoretical complexity with practical wall-clock performance.

### Core Design

All algorithms implement `MatrixMultiplier` (Strategy pattern):
```java
int[][] multiply(int[][] A, int[][] B);
String getName();
String getComplexityOrder();
```

### Algorithm Implementations (`co.uniquindio.matriz.algoritmos`)

| Class | Algorithm | Notes |
|---|---|---|
| `NaivOnArray` | Classical O(n³) triple loop | Baseline |
| `NaivLoopUnrollingTwo` | O(n³) with 2x unrolling | Reduces loop overhead |
| `NaivLoopUnrollingFour` | O(n³) with 4x unrolling | Better cache locality |
| `WinogradOriginal` | Winograd O(n³) | ~n³/2 multiplications via row/col factor precomputation |
| `WinogradScaled` | Scaled Winograd O(n³) | Adds prescaling for numerical stability |
| `StrassenNaiv` | Strassen O(n^2.807) | Divide-and-conquer, naive base case at n≤64 |
| `StrassenWinograd` | Strassen-Winograd O(n^2.807) | Pads to power of 2 for arbitrary sizes |

### Infrastructure (partially stubbed)

- `MatrixGenerator` — generate random test matrices (stub)
- `BenchmarkRunner` — time each algorithm across matrix sizes (stub)
- `BenchmarkResult` — result data model (stub)
- `PersistenceManager` — save/load results to CSV/JSON (stub)
- `Main` — orchestration entry point (currently "Hello world!")

JFreeChart 1.5.4 is included as a dependency for generating performance charts.

### Key Implementation Details

- Strassen requires square matrices with dimensions that are powers of 2; `StrassenWinograd` handles arbitrary sizes via zero-padding.
- Loop unrolling variants assume matrix dimensions are multiples of their unrolling factor (2 or 4) — callers must pad if needed.
- Winograd precomputes row and column factor arrays before the main multiplication loop.
