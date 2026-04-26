package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo NaivKIJ
 * Triple bucle clasico con orden k-i-j. Permite reusar la fila k de B
 * a lo largo de todas las filas i, manteniendo accesos secuenciales en B.
 * Complejidad: O(n^3)
 */
public class NaivKIJ implements MatrixMultiplier {

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] C = new long[n][n];
        for (int k = 0; k < n; k++) {
            long[] rowB = B[k];
            for (int i = 0; i < n; i++) {
                long a = A[i][k];
                long[] rowC = C[i];
                for (int j = 0; j < n; j++) {
                    rowC[j] += a * rowB[j];
                }
            }
        }
        return C;
    }

    @Override
    public String getName() { return "NaivKIJ"; }

    @Override
    public String getComplexityOrder() { return "O(n^3)"; }
}
