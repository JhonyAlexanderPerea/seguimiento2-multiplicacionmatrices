package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo NaivLoopUnrollingFour
 * Multiplicación naiv con desenrollado de bucle por factor de 4.
 * Mayor reducción de overhead al procesar 4 elementos por iteración.
 * Complejidad: O(n³)
 */
public class NaivLoopUnrollingFour implements MatrixMultiplier {
 
    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] C = new long[n][n];
 
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                long sum = 0;
                int k = 0;
                // Desenrollado x4: procesar 4 elementos por iteración
                for (; k <= n - 4; k += 4) {
                    sum += A[i][k]     * B[k][j]
                         + A[i][k + 1] * B[k + 1][j]
                         + A[i][k + 2] * B[k + 2][j]
                         + A[i][k + 3] * B[k + 3][j];
                }
                // Residuo
                for (; k < n; k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] = sum;
            }
        }
        return C;
    }
 
    @Override
    public String getName() { return this.getClass().getName(); }

    @Override
    public String getComplexityOrder() { return "O(n³)"; }
}