package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo StrassenWinograd
 * Variante de Strassen que usa la formulación de Winograd,
 * reduciendo el número de sumas/restas intermedias de 18 a 15.
 * Complejidad: O(n^2.807)
 */
public class StrassenWinograd implements MatrixMultiplier {

    private static final int BASE_CASE = 64;

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;
        int m = nextPowerOfTwo(n);

        long[][] paddedA = (m == n) ? A : padMatrix(A, m);
        long[][] paddedB = (m == n) ? B : padMatrix(B, m);

        long[][] paddedResult = strassenWinograd(paddedA, paddedB, m);
        return (m == n) ? paddedResult : trimMatrix(paddedResult, n);
    }

    private long[][] strassenWinograd(long[][] A, long[][] B, int n) {
        if (n <= BASE_CASE) {
            return naivMultiply(A, B, n);
        }

        int half = n / 2;

        long[][] A11 = sub(A, 0, 0, half);
        long[][] A12 = sub(A, 0, half, half);
        long[][] A21 = sub(A, half, 0, half);
        long[][] A22 = sub(A, half, half, half);

        long[][] B11 = sub(B, 0, 0, half);
        long[][] B12 = sub(B, 0, half, half);
        long[][] B21 = sub(B, half, 0, half);
        long[][] B22 = sub(B, half, half, half);

        // Precomputos para reducir sumas/restas intermedias.
        long[][] A11PlusA22 = addM(A11, A22);
        long[][] B11PlusB22 = addM(B11, B22);
        long[][] A21PlusA22 = addM(A21, A22);
        long[][] B12MinusB22 = subM(B12, B22);
        long[][] B21MinusB11 = subM(B21, B11);
        long[][] A11PlusA12 = addM(A11, A12);
        long[][] A21MinusA11 = subM(A21, A11);
        long[][] B11PlusB12 = addM(B11, B12);
        long[][] A12MinusA22 = subM(A12, A22);
        long[][] B21PlusB22 = addM(B21, B22);

        // 7 productos recursivos de Strassen.
        long[][] P1 = strassenWinograd(A11PlusA22, B11PlusB22, half);
        long[][] P2 = strassenWinograd(A21PlusA22, B11, half);
        long[][] P3 = strassenWinograd(A11, B12MinusB22, half);
        long[][] P4 = strassenWinograd(A22, B21MinusB11, half);
        long[][] P5 = strassenWinograd(A11PlusA12, B22, half);
        long[][] P6 = strassenWinograd(A21MinusA11, B11PlusB12, half);
        long[][] P7 = strassenWinograd(A12MinusA22, B21PlusB22, half);

        long[][] C11 = addM(subM(addM(P1, P4), P5), P7);
        long[][] C12 = addM(P3, P5);
        long[][] C21 = addM(P2, P4);
        long[][] C22 = addM(subM(addM(P1, P3), P2), P6);

        return join(C11, C12, C21, C22, n);
    }

    private long[][] naivMultiply(long[][] A, long[][] B, int n) {
        long[][] C = new long[n][n];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                long aik = A[i][k];
                for (int j = 0; j < n; j++) {
                    C[i][j] += aik * B[k][j];
                }
            }
        }
        return C;
    }

    private int nextPowerOfTwo(int n) {
        int p = 1;
        while (p < n) {
            p <<= 1;
        }
        return p;
    }

    private long[][] padMatrix(long[][] matrix, int newSize) {
        long[][] padded = new long[newSize][newSize];
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, padded[i], 0, n);
        }
        return padded;
    }

    private long[][] trimMatrix(long[][] matrix, int size) {
        long[][] trimmed = new long[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(matrix[i], 0, trimmed[i], 0, size);
        }
        return trimmed;
    }

    private long[][] sub(long[][] M, int row, int col, int size) {
        long[][] S = new long[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                S[i][j] = M[row + i][col + j];
        return S;
    }

    private long[][] addM(long[][] A, long[][] B) {
        int n = A.length;
        long[][] R = new long[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                R[i][j] = A[i][j] + B[i][j];
        return R;
    }

    private long[][] subM(long[][] A, long[][] B) {
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
                C[i][j]               = C11[i][j];
                C[i][j + half]        = C12[i][j];
                C[i + half][j]        = C21[i][j];
                C[i + half][j + half] = C22[i][j];
            }
        return C;
    }

    @Override
    public String getName() { return "StrassenWinograd"; }

    @Override
    public String getComplexityOrder() { return "O(n^2.807)"; }
}