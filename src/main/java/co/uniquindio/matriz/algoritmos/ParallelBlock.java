package co.uniquindio.matriz.algoritmos;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

/**
 * Algoritmo ParallelBlock
 * Multiplicacion por bloques (tiling) paralelizada en el eje de
 * bloques de filas. Combina localidad de cache con paralelismo
 * para aprovechar arquitecturas multi-core.
 * Complejidad: O(n^3) / p
 */
public class ParallelBlock implements MatrixMultiplier {

    private static final int BLOCK = 64;

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] C = new long[n][n];
        int blocks = (n + BLOCK - 1) / BLOCK;
        IntStream.range(0, blocks).parallel().forEach(bi -> {
            int ii = bi * BLOCK;
            int iMax = Math.min(ii + BLOCK, n);
            for (int kk = 0; kk < n; kk += BLOCK) {
                int kMax = Math.min(kk + BLOCK, n);
                for (int jj = 0; jj < n; jj += BLOCK) {
                    int jMax = Math.min(jj + BLOCK, n);
                    for (int i = ii; i < iMax; i++) {
                        long[] rowA = A[i];
                        long[] rowC = C[i];
                        for (int k = kk; k < kMax; k++) {
                            long a = rowA[k];
                            long[] rowB = B[k];
                            for (int j = jj; j < jMax; j++) {
                                rowC[j] += a * rowB[j];
                            }
                        }
                    }
                }
            }
        });
        return C;
    }

    @Override
    public String getName() {
        return "ParallelBlock(B=" + BLOCK
                + ",threads=" + ForkJoinPool.commonPool().getParallelism() + ")";
    }

    @Override
    public String getComplexityOrder() { return "O(n^3)"; }
}
