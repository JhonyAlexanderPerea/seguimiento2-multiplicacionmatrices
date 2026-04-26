package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo NaivLoopUnrollingEight
 * Multiplicacion clasica con desenrollado del bucle interno por
 * factor 8 para reducir overhead de control y favorecer pipelining
 * de instrucciones. Asume que n es multiplo de 8.
 * Complejidad: O(n^3)
 */
public class NaivLoopUnrollingEight implements MatrixMultiplier {

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] C = new long[n][n];
        for (int i = 0; i < n; i++) {
            long[] rowA = A[i];
            long[] rowC = C[i];
            for (int k = 0; k < n; k++) {
                long a = rowA[k];
                long[] rowB = B[k];
                int j = 0;
                int limit = n - (n % 8);
                for (; j < limit; j += 8) {
                    rowC[j]     += a * rowB[j];
                    rowC[j + 1] += a * rowB[j + 1];
                    rowC[j + 2] += a * rowB[j + 2];
                    rowC[j + 3] += a * rowB[j + 3];
                    rowC[j + 4] += a * rowB[j + 4];
                    rowC[j + 5] += a * rowB[j + 5];
                    rowC[j + 6] += a * rowB[j + 6];
                    rowC[j + 7] += a * rowB[j + 7];
                }
                for (; j < n; j++) {
                    rowC[j] += a * rowB[j];
                }
            }
        }
        return C;
    }

    @Override
    public String getName() { return "NaivLoopUnrollingEight"; }

    @Override
    public String getComplexityOrder() { return "O(n^3)"; }
}
