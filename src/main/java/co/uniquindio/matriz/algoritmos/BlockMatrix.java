package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo BlockMatrix
 * Multiplicacion por bloques (tiling) que divide las matrices en
 * sub-bloques de tamano BLOCK x BLOCK, multiplicando bloque a bloque
 * para maximizar reuso de cache L1/L2.
 * Complejidad: O(n^3) pero con menor numero de fallos de cache.
 */
public class BlockMatrix implements MatrixMultiplier {

    private static final int BLOCK = 64;

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        long[][] C = new long[n][n];
        for (int ii = 0; ii < n; ii += BLOCK) {
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
        }
        return C;
    }

    @Override
    public String getName() { return "BlockMatrix(B=" + BLOCK + ")"; }

    @Override
    public String getComplexityOrder() { return "O(n^3)"; }
}
