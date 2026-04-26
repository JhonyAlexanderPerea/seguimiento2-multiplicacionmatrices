package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo TransposedNaiv
 * Antes de multiplicar transpone B para acceder a sus columnas
 * como filas contiguas en memoria, mejorando notablemente la
 * localidad de cache del bucle interno.
 * Complejidad: O(n^3) + O(n^2) por la transposicion.
 */
public class TransposedNaiv implements MatrixMultiplier {

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] Bt = new long[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Bt[j][i] = B[i][j];
            }
        }
        long[][] C = new long[n][n];
        for (int i = 0; i < n; i++) {
            long[] rowA = A[i];
            long[] rowC = C[i];
            for (int j = 0; j < n; j++) {
                long[] colB = Bt[j];
                long sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += rowA[k] * colB[k];
                }
                rowC[j] = sum;
            }
        }
        return C;
    }

    @Override
    public String getName() { return "TransposedNaiv"; }

    @Override
    public String getComplexityOrder() { return "O(n^3)"; }
}
