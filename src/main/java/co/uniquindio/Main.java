package co.uniquindio;

import co.uniquindio.matriz.algoritmos.*;
import co.uniquindio.matriz.modelo.BenchmarkResult;
import co.uniquindio.matriz.utilidades.BenchmarkRunner;

import java.util.List;

public class Main {

    private static final int MATRIX_SIZE = 1024;

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("  Matrix Multiplication Benchmark  —  n = " + MATRIX_SIZE);
        System.out.println("  Elements: 6-digit integers [100 000, 999 999]");
        System.out.println("=".repeat(70));
        System.out.println();

        BenchmarkRunner runner = new BenchmarkRunner(MATRIX_SIZE);

        // Register every algorithm in ascending expected-performance order
        runner.addAlgorithm(new NaivOnArray());
        runner.addAlgorithm(new NaivLoopUnrollingTwo());
        runner.addAlgorithm(new NaivLoopUnrollingFour());
        runner.addAlgorithm(new WinogradOriginal());
        runner.addAlgorithm(new WinogradScaled());
        runner.addAlgorithm(new StrassenNaiv());
        runner.addAlgorithm(new StrassenWinograd());

        List<BenchmarkResult> results = runner.run();

        System.out.println();
        System.out.println("=".repeat(70));
        System.out.printf("%-30s %-12s %-10s %s%n",
                "Algorithm", "Complexity", "Size", "Time");
        System.out.println("-".repeat(70));
        for (BenchmarkResult r : results) {
            System.out.println(r);
        }
        System.out.println("=".repeat(70));
    }
}
