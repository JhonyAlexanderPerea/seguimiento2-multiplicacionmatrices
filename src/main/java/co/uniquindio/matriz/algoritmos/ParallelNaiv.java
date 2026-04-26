package co.uniquindio.matriz.algoritmos;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

/**
 * Algoritmo ParallelNaiv
 * Variante del bucle i-k-j paralelizada por filas usando IntStream.parallel().
 * Cada fila de C se calcula independientemente en un hilo del ForkJoinPool.
 * Complejidad: O(n^3) / p, donde p es el numero de hilos disponibles.
 */
public class ParallelNaiv implements MatrixMultiplier {

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] C = new long[n][n];
        IntStream.range(0, n).parallel().forEach(i -> {
            long[] rowA = A[i];
            long[] rowC = C[i];
            for (int k = 0; k < n; k++) {
                long a = rowA[k];
                long[] rowB = B[k];
                for (int j = 0; j < n; j++) {
                    rowC[j] += a * rowB[j];
                }
            }
        });
        return C;
    }

    @Override
    public String getName() {
        return "ParallelNaiv(threads=" + ForkJoinPool.commonPool().getParallelism() + ")";
    }

    @Override
    public String getComplexityOrder() { return "O(n^3)"; }
}
