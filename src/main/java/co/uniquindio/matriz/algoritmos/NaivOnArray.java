package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo NaivOnArray
 * Multiplicación clásica triple bucle sobre arrays bidimensionales.
 * Complejidad: O(n³)
 */

public class NaivOnArray implements MatrixMultiplier {

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] C = new long[n][n];
 
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = 0;
                for (int k = 0; k < n; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }
 
    @Override
    public String getName() { return this.getClass().getName(); }
 
    @Override
    public String getComplexityOrder() { return "O(n³)"; }
}