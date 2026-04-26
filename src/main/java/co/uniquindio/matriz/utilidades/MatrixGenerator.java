package co.uniquindio.matriz.utilidades;

import java.util.Random;

public class MatrixGenerator {

    public static final long MIN_VALUE = 100_000L;   // 6 digits minimum
    public static final long MAX_VALUE = 999_999L;   // 6 digits maximum

    private final Random random;

    public MatrixGenerator(long seed) {
        this.random = new Random(seed);
    }

    public MatrixGenerator() {
        this.random = new Random();
    }

    /**
     * Generates a square matrix of the given size where every element
     * is a 6-digit number in [100_000, 999_999].
     */
    public long[][] generate(int size) {
        long[][] matrix = new long[size][size];
        long range = MAX_VALUE - MIN_VALUE + 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = MIN_VALUE + (long) (random.nextDouble() * range);
            }
        }
        return matrix;
    }
}
