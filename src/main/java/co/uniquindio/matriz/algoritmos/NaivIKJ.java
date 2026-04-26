package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo NaivIKJ
 * Triple bucle clasico con orden de iteracion i-k-j.
 * Mejora la localidad de cache respecto a i-j-k al recorrer
 * filas de B contiguamente en memoria.
 * Complejidad: O(n^3)
 */
public class NaivIKJ implements MatrixMultiplier {

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
                for (int j = 0; j < n; j++) {
                    rowC[j] += a * rowB[j];
                }
            }
        }
        return C;
    }

    @Override
    public String getName() { return "NaivIKJ"; }

    @Override
    public String getComplexityOrder() { return "O(n^3)"; }
}
