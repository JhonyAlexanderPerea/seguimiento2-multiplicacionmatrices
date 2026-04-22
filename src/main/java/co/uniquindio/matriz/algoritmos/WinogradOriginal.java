package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo  WinogradOriginal
 * Algoritmo de Winograd que reduce el número de multiplicaciones
 * precalculando factores de filas y columnas.
 * Complejidad: O(n³) con ~n³/2 multiplicaciones en lugar de n³.
 */
public class WinogradOriginal implements MatrixMultiplier {
 
    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] C = new long[n][n];
 
        // Precalcular factores de filas de A
        long[] rowFactor = new long[n];
        for (int i = 0; i < n; i++) {
            rowFactor[i] = 0;
            for (int k = 0; k < n / 2; k++) {
                rowFactor[i] += A[i][2 * k] * A[i][2 * k + 1];
            }
        }
 
        // Precalcular factores de columnas de B
        long[] colFactor = new long[n];
        for (int j = 0; j < n; j++) {
            colFactor[j] = 0;
            for (int k = 0; k < n / 2; k++) {
                colFactor[j] += B[2 * k][j] * B[2 * k + 1][j];
            }
        }
 
        // Calcular producto usando factores precalculados
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                long sum = -rowFactor[i] - colFactor[j];
                for (int k = 0; k < n / 2; k++) {
                    sum += (A[i][2 * k] + B[2 * k + 1][j])
                         * (A[i][2 * k + 1] + B[2 * k][j]);
                }
                // Residuo si n es impar
                if (n % 2 != 0) {
                    sum += A[i][n - 1] * B[n - 1][j];
                }
                C[i][j] = sum;
            }
        }
        return C;
    }
 
    @Override
    public String getName() { return this.getClass().getName(); }
 
    @Override
    public String getComplexityOrder() { return "O(n³)  [~n³/2 multiplicaciones]"; }
}