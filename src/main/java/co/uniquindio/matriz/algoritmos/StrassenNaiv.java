package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo StrassenNaiv
 * Algoritmo de Strassen divide y vencerás.
 * Reduce las multiplicaciones de 8 a 7 por nivel de recursión,
 * usando el algoritmo Naiv en el caso base.
 * Complejidad: O(n^2.807)  [log₂(7)]
 */
public class StrassenNaiv implements MatrixMultiplier {

    // Tamaño mínimo para usar multiplicación naiv directa
    private static final int BASE_CASE = 64;

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        return strassen(A, B, n);
    }

    private long[][] strassen(long[][] A, long[][] B, int n) {
        if (n <= BASE_CASE) {
            return naivMultiply(A, B, n);
        }

        int half = n / 2;

        // Dividir matrices en 4 submatrices
        long[][] A11 = sub(A, 0, 0, half);
        long[][] A12 = sub(A, 0, half, half);
        long[][] A21 = sub(A, half, 0, half);
        long[][] A22 = sub(A, half, half, half);

        long[][] B11 = sub(B, 0, 0, half);
        long[][] B12 = sub(B, 0, half, half);
        long[][] B21 = sub(B, half, 0, half);
        long[][] B22 = sub(B, half, half, half);

        // 7 productos de Strassen
        long[][] M1 = strassen(add(A11, A22), add(B11, B22), half);
        long[][] M2 = strassen(add(A21, A22), B11, half);
        long[][] M3 = strassen(A11, sub(B12, B22), half);
        long[][] M4 = strassen(A22, sub(B21, B11), half);
        long[][] M5 = strassen(add(A11, A12), B22, half);
        long[][] M6 = strassen(sub(A21, A11), add(B11, B12), half);
        long[][] M7 = strassen(sub(A12, A22), add(B21, B22), half);

        // Construir submatrices del resultado
        long[][] C11 = add(sub(add(M1, M4), M5), M7);
        long[][] C12 = add(M3, M5);
        long[][] C21 = add(M2, M4);
        long[][] C22 = add(sub(add(M1, M3), M2), M6);

        return join(C11, C12, C21, C22, n);
    }

    private long[][] naivMultiply(long[][] A, long[][] B, int n) {
        long[][] C = new long[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    C[i][j] += A[i][k] * B[k][j];
        return C;
    }

    /** Extrae submatriz de tamaño size x size desde (row, col) */
    private long[][] sub(long[][] M, int row, int col, int size) {
        long[][] S = new long[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                S[i][j] = M[row + i][col + j];
        return S;
    }

    private long[][] add(long[][] A, long[][] B) {
        int n = A.length;
        long[][] R = new long[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                R[i][j] = A[i][j] + B[i][j];
        return R;
    }

    private long[][] sub(long[][] A, long[][] B) {
        int n = A.length;
        long[][] R = new long[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                R[i][j] = A[i][j] - B[i][j];
        return R;
    }

    private long[][] join(long[][] C11, long[][] C12, long[][] C21, long[][] C22, int n) {
        long[][] C = new long[n][n];
        int half = n / 2;
        for (int i = 0; i < half; i++)
            for (int j = 0; j < half; j++) {
                C[i][j]              = C11[i][j];
                C[i][j + half]       = C12[i][j];
                C[i + half][j]       = C21[i][j];
                C[i + half][j + half] = C22[i][j];
            }
        return C;
    }

    @Override
    public String getName() { return this.getClass().getName(); }

    @Override
    public String getComplexityOrder() { return "O(n^2.807)"; }
}