package co.uniquindio.matriz.utilidades;

import co.uniquindio.matriz.algoritmos.MatrixMultiplier;
import co.uniquindio.matriz.modelo.BenchmarkResult;
import co.uniquindio.matriz.persistencia.PersistenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class BenchmarkRunner {

    private final List<MatrixMultiplier> algorithms = new ArrayList<>();
    private final MatrixGenerator generator;
    private final PersistenceManager persistenceManager;
    private final int matrixSize;

    public BenchmarkRunner(int matrixSize) {
        this.matrixSize = matrixSize;
        this.generator = new MatrixGenerator();
        this.persistenceManager = new PersistenceManager();
    }

    public void addAlgorithm(MatrixMultiplier algorithm) {
        algorithms.add(algorithm);
    }

    public List<BenchmarkResult> run() {
        List<BenchmarkResult> results = new ArrayList<>();
        BenchmarkLogger logger = null;

        try {
            logger = new BenchmarkLogger(matrixSize);
            List<String> names = new ArrayList<>();
            for (MatrixMultiplier a : algorithms) names.add(a.getName());
            logger.writeHeader(matrixSize,
                    MatrixGenerator.MIN_VALUE, MatrixGenerator.MAX_VALUE,
                    algorithms.size(), names);

            System.out.printf("Log file: %s%n", logger.getLogFile().toAbsolutePath());
            System.out.printf("CSV file: %s%n", logger.getCsvFile().toAbsolutePath());
            System.out.printf("Generating two %d x %d matrices...%n",
                    matrixSize, matrixSize);

            long genStart = System.nanoTime();
            long[][] A = generator.generate(matrixSize);
            long[][] B = generator.generate(matrixSize);
                PersistenceManager.MatrixSaveResult savedMatrices =
                    persistenceManager.saveMatrices(A, B, matrixSize);
            long genElapsed = System.nanoTime() - genStart;
            logger.recordMatrixGeneration(genElapsed);
            System.out.printf("Matrices generated in %.3f s. Starting benchmarks...%n%n",
                    genElapsed / 1_000_000_000.0);
                System.out.printf("Matrix A saved to: %s%n", savedMatrices.matrixAFile().toAbsolutePath());
                System.out.printf("Matrix B saved to: %s%n", savedMatrices.matrixBFile().toAbsolutePath());

            int total = algorithms.size();
            for (int i = 0; i < total; i++) {
                MatrixMultiplier algo = algorithms.get(i);
                String name = algo.getName();
                String cx   = algo.getComplexityOrder();

                logger.logAlgorithmStart(i + 1, total, name, cx);
                System.out.printf("  [%2d/%d] %-40s ", i + 1, total, name);
                System.out.flush();

                long heapBefore = Runtime.getRuntime().totalMemory()
                                - Runtime.getRuntime().freeMemory();
                long start = System.nanoTime();
                try {
                    algo.multiply(A, B);
                    long elapsedNs = System.nanoTime() - start;
                    long heapAfter = Runtime.getRuntime().totalMemory()
                                   - Runtime.getRuntime().freeMemory();

                    BenchmarkResult result = BenchmarkResult.ok(
                            name, cx, matrixSize, elapsedNs, heapAfter);
                    results.add(result);
                    logger.logAlgorithmEnd(result, heapBefore);
                    System.out.printf("done in %,10.2f ms%n",
                            elapsedNs / 1_000_000.0);
                } catch (Throwable t) {
                    BenchmarkResult result = BenchmarkResult.failed(
                            name, cx, matrixSize, t.getMessage());
                    results.add(result);
                    logger.logAlgorithmFailure(result, t);
                    System.out.printf("FAILED (%s)%n", t.getClass().getSimpleName());
                }
            }

            logger.writeRankingAndSummary();
            printConsoleRanking(results);
        } catch (IOException e) {
            System.err.println("Logger I/O error: " + e.getMessage());
        } finally {
            if (logger != null) {
                try { logger.close(); } catch (IOException ignored) { }
            }
        }

        return results;
    }

    private void printConsoleRanking(List<BenchmarkResult> results) {
        List<BenchmarkResult> ok = new ArrayList<>();
        for (BenchmarkResult r : results) if (r.isSuccess()) ok.add(r);
        if (ok.isEmpty()) return;
        ok.sort(Comparator.comparingLong(BenchmarkResult::getElapsedNanos));

        long best  = ok.get(0).getElapsedNanos();
        long worst = ok.get(ok.size() - 1).getElapsedNanos();

        System.out.println();
        System.out.println("=".repeat(80));
        System.out.printf("  RANKING (n=%d)  -  fastest first%n", matrixSize);
        System.out.println("-".repeat(80));
        System.out.printf("%-4s %-32s %-12s %12s %10s %10s%n",
                "Rank", "Algorithm", "Complexity", "Time", "vs Best", "vs Worst");
        System.out.println("-".repeat(80));
        for (int i = 0; i < ok.size(); i++) {
            BenchmarkResult r = ok.get(i);
            double vsBest  = r.getElapsedNanos() / (double) best;
            double vsWorst = worst / (double) r.getElapsedNanos();
            System.out.printf(Locale.ROOT,
                    "%-4d %-32s %-12s %,10.2f ms   %7.2fx   %7.2fx%n",
                    i + 1, r.getAlgorithmName(), r.getComplexityOrder(),
                    r.getElapsedNanos() / 1_000_000.0, vsBest, vsWorst);
        }
        System.out.println("=".repeat(80));
    }
}
