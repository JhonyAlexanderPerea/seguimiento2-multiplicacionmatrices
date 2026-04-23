package co.uniquindio.matriz.utilidades;

import co.uniquindio.matriz.algoritmos.MatrixMultiplier;
import co.uniquindio.matriz.modelo.BenchmarkResult;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkRunner {

    private final List<MatrixMultiplier> algorithms = new ArrayList<>();
    private final MatrixGenerator generator;
    private final int matrixSize;

    public BenchmarkRunner(int matrixSize) {
        this.matrixSize = matrixSize;
        this.generator = new MatrixGenerator();
    }

    public void addAlgorithm(MatrixMultiplier algorithm) {
        algorithms.add(algorithm);
    }

    /**
     * Generates a fresh pair of random matrices and runs every registered
     * algorithm once, returning one BenchmarkResult per algorithm.
     */
    public List<BenchmarkResult> run() {
        System.out.printf("Generating two %d×%d matrices with 6-digit elements…%n", matrixSize, matrixSize);
        long[][] A = generator.generate(matrixSize);
        long[][] B = generator.generate(matrixSize);
        System.out.println("Matrices generated. Starting benchmarks…");
        System.out.println();

        List<BenchmarkResult> results = new ArrayList<>();

        for (MatrixMultiplier algo : algorithms) {
            System.out.printf("  Running %-30s", algo.getName() + "…");
            System.out.flush();

            long start = System.currentTimeMillis();
            algo.multiply(A, B);
            long elapsed = System.currentTimeMillis() - start;

            BenchmarkResult result = new BenchmarkResult(
                    algo.getName(), algo.getComplexityOrder(), matrixSize, elapsed);
            results.add(result);

            System.out.printf(" done in %,d ms%n", elapsed);
        }

        return results;
    }
}
