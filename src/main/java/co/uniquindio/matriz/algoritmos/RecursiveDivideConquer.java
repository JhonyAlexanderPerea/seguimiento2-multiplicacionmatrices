package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo RecursiveDivideConquer
 * Divide y venceras clasico (no Strassen): cada multiplicacion de
 * tamano n se descompone en 8 multiplicaciones de tamano n/2 mas
 * 4 sumas. No reduce la complejidad asintotica frente al naiv,
 * pero ilustra el patron recursivo base sobre el que se construye
 * Strassen. Caso base directo cuando n <= 64.
 * Complejidad: T(n) = 8 T(n/2) + O(n^2) = O(n^3)
 */
public class RecursiveDivideConquer implements MatrixMultiplier {

    private static final int BASE_CASE = 64;

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        return divide(A, B, n);
    }

    private long[][] divide(long[][] A, long[][] B, int n) {
        if (n <= BASE_CASE) {
            return naiv(A, B, n);
        }
        int h = n / 2;
        long[][] A11 = sub(A, 0, 0, h);
        long[][] A12 = sub(A, 0, h, h);
        long[][] A21 = sub(A, h, 0, h);
        long[][] A22 = sub(A, h, h, h);
        long[][] B11 = sub(B, 0, 0, h);
        long[][] B12 = sub(B, 0, h, h);
        long[][] B21 = sub(B, h, 0, h);
        long[][] B22 = sub(B, h, h, h);

        long[][] C11 = add(divide(A11, B11, h), divide(A12, B21, h));
        long[][] C12 = add(divide(A11, B12, h), divide(A12, B22, h));
        long[][] C21 = add(divide(A21, B11, h), divide(A22, B21, h));
        long[][] C22 = add(divide(A21, B12, h), divide(A22, B22, h));

        return join(C11, C12, C21, C22, n);
    }

    private long[][] naiv(long[][] A, long[][] B, int n) {
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

    private long[][] sub(long[][] M, int r, int c, int s) {
        long[][] R = new long[s][s];
        for (int i = 0; i < s; i++)
            System.arraycopy(M[r + i], c, R[i], 0, s);
        return R;
    }

    private long[][] add(long[][] X, long[][] Y) {
        int n = X.length;
        long[][] R = new long[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                R[i][j] = X[i][j] + Y[i][j];
        return R;
    }

    private long[][] join(long[][] C11, long[][] C12, long[][] C21, long[][] C22, int n) {
        long[][] C = new long[n][n];
        int h = n / 2;
        for (int i = 0; i < h; i++) {
            System.arraycopy(C11[i], 0, C[i],     0, h);
            System.arraycopy(C12[i], 0, C[i],     h, h);
            System.arraycopy(C21[i], 0, C[i + h], 0, h);
            System.arraycopy(C22[i], 0, C[i + h], h, h);
        }
        return C;
    }

    @Override
    public String getName() { return "RecursiveDivideConquer"; }

    @Override
    public String getComplexityOrder() { return "O(n^3)"; }
}
