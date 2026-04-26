package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo NaivLoopUnrollingTwo
 * Multiplicación naiv con desenrollado de bucle por factor de 2.
 * Reduce el overhead de control del bucle procesando 2 elementos por iteración.
 * Complejidad: O(n³)
 */
public class NaivLoopUnrollingTwo implements MatrixMultiplier {
 
    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] C = new long[n][n];
 
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                long sum = 0;
                int k = 0;
                // Desenrollado x2: procesar 2 elementos por iteración
                for (; k <= n - 2; k += 2) {
                    sum += A[i][k]     * B[k][j]
                         + A[i][k + 1] * B[k + 1][j];
                }
                // Residuo si n es impar
                for (; k < n; k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] = sum;
            }
        }
        return C;
    }
 
    @Override
    public String getName() { return "NaivLoopUnrollingTwo"; }

    @Override
    public String getComplexityOrder() { return "O(n^3)"; }
}