package co.uniquindio;

import co.uniquindio.matriz.algoritmos.*;
import co.uniquindio.matriz.utilidades.BenchmarkRunner;

public class Main {

    private static final int DEFAULT_MATRIX_SIZE = 1024;

    public static void main(String[] args) {
        int matrixSize = DEFAULT_MATRIX_SIZE;
        if (args.length >= 1) {
            try {
                matrixSize = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.printf("Invalid matrix size '%s', using default %d%n",
                        args[0], DEFAULT_MATRIX_SIZE);
            }
        }

        System.out.println("=".repeat(70));
        System.out.println("  Matrix Multiplication Benchmark  -  n = " + matrixSize);
        System.out.println("  Elements: 6-digit integers [100 000, 999 999]");
        System.out.println("  Usage: java -jar <jar> [matrix_size]   (default 1024)");
        System.out.println("=".repeat(70));
        System.out.println();

        BenchmarkRunner runner = new BenchmarkRunner(matrixSize);

        // 16 algorithms registered, ordered by expected behavior.
        // 1-3 : ordenes alternativos del triple bucle
        runner.addAlgorithm(new NaivOnArray());
        runner.addAlgorithm(new NaivIKJ());
        runner.addAlgorithm(new NaivKIJ());
        runner.addAlgorithm(new NaivJIK());
        // 4-5 : optimizaciones sobre el triple bucle
        runner.addAlgorithm(new TransposedNaiv());
        runner.addAlgorithm(new BlockMatrix());
        // 6-8 : loop unrolling
        runner.addAlgorithm(new NaivLoopUnrollingTwo());
        runner.addAlgorithm(new NaivLoopUnrollingFour());
        runner.addAlgorithm(new NaivLoopUnrollingEight());
        // 9-10 : Winograd
        runner.addAlgorithm(new WinogradOriginal());
        runner.addAlgorithm(new WinogradScaled());
        // 11 : divide y venceras clasico
        runner.addAlgorithm(new RecursiveDivideConquer());
        // 12-13 : Strassen
        runner.addAlgorithm(new StrassenNaiv());
        runner.addAlgorithm(new StrassenWinograd());
        // 14-15 : paralelos
        runner.addAlgorithm(new ParallelNaiv());
        runner.addAlgorithm(new ParallelBlock());

        runner.run();
    }
}
