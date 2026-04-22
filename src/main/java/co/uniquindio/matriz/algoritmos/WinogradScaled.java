package co.uniquindio.matriz.algoritmos;

/**
 * Algoritmo WinogradScaled
 * Variante del algoritmo de Winograd con escalado previo
 * para mejorar la estabilidad numérica en matrices con
 * valores de distinta magnitud.
 * Complejidad: O(n³)
 */
public class WinogradScaled implements MatrixMultiplier {

    @Override
    public long[][] multiply(long[][] A, long[][] B) {
        int n = A.length;

        // Calcular el factor de escala (máximo absoluto de A y B)
        long maxVal = findMaxAbsolute(A, B, n);
        long scale = (maxVal == 0) ? 1L : maxVal;

        // Crear matrices escaladas
        long[][] As = new long[n][n];
        long[][] Bs = new long[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                As[i][j] = A[i][j] / scale;
                Bs[i][j] = B[i][j] / scale;
            }
        }

        // Aplicar Winograd Original sobre matrices escaladas
        long[][] Cs = applyWinograd(As, Bs, n);

        // Desescalar resultado: C = Cs * scale²
        long[][] C = new long[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = Cs[i][j] * scale * scale;
            }
        }
        return C;
    }

    private long findMaxAbsolute(long[][] A, long[][] B, int n) {
        long max = 1L;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (Math.abs(A[i][j]) > max) max = Math.abs(A[i][j]);
                if (Math.abs(B[i][j]) > max) max = Math.abs(B[i][j]);
            }
        return max;
    }

    private long[][] applyWinograd(long[][] A, long[][] B, int n) {
        long[][] C = new long[n][n];
        long[] rowFactor = new long[n];
        long[] colFactor = new long[n];

        for (int i = 0; i < n; i++)
            for (int k = 0; k < n / 2; k++)
                rowFactor[i] += A[i][2 * k] * A[i][2 * k + 1];

        for (int j = 0; j < n; j++)
            for (int k = 0; k < n / 2; k++)
                colFactor[j] += B[2 * k][j] * B[2 * k + 1][j];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                long sum = -rowFactor[i] - colFactor[j];
                for (int k = 0; k < n / 2; k++)
                    sum += (A[i][2 * k] + B[2 * k + 1][j])
                         * (A[i][2 * k + 1] + B[2 * k][j]);
                if (n % 2 != 0)
                    sum += A[i][n - 1] * B[n - 1][j];
                C[i][j] = sum;
            }
        }
        return C;
    }

    @Override
    public String getName() { return this.getClass().getName(); }

    @Override
    public String getComplexityOrder() { return "O(n³)  [Winograd + escalado numérico]"; }
}