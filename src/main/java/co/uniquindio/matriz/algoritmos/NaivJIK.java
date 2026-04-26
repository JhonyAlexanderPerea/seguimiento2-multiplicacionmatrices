package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo NaivJIK
 * Triple bucle clasico con orden j-i-k. Es la peor variante de cache
 * porque recorre B por columnas en el bucle interno; util como
 * referencia comparativa frente a los ordenes i-k-j y k-i-j.
 * Complejidad: O(n^3)
 */
public class NaivJIK implements MatrixMultiplier {

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] C = new long[n][n];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                long sum = 0;
                long[] rowA = A[i];
                for (int k = 0; k < n; k++) {
                    sum += rowA[k] * B[k][j];
                }
                C[i][j] = sum;
            }
        }
        return C;
    }

    @Override
    public String getName() { return "NaivJIK"; }

    @Override
    public String getComplexityOrder() { return "O(n^3)"; }
}
