package co.uniquindio.matriz.modelo;

import java.util.Locale;

public class BenchmarkResult {

    private final String algorithmName;
    private final String complexityOrder;
    private final int matrixSize;
    private final long elapsedNanos;
    private final long heapUsedBytes;
    private final boolean success;
    private final String errorMessage;

    public BenchmarkResult(String algorithmName, String complexityOrder,
                           int matrixSize, long elapsedNanos,
                           long heapUsedBytes, boolean success,
                           String errorMessage) {
        this.algorithmName   = algorithmName;
        this.complexityOrder = complexityOrder;
        this.matrixSize      = matrixSize;
        this.elapsedNanos    = elapsedNanos;
        this.heapUsedBytes   = heapUsedBytes;
        this.success         = success;
        this.errorMessage    = errorMessage;
    }

    public static BenchmarkResult ok(String name, String complexity,
                                     int n, long nanos, long heap) {
        return new BenchmarkResult(name, complexity, n, nanos, heap, true, null);
    }

    public static BenchmarkResult failed(String name, String complexity,
                                         int n, String message) {
        return new BenchmarkResult(name, complexity, n, 0L, 0L, false, message);
    }

    public String  getAlgorithmName()   { return algorithmName; }
    public String  getComplexityOrder() { return complexityOrder; }
    public int     getMatrixSize()      { return matrixSize; }
    public long    getElapsedNanos()    { return elapsedNanos; }
    public long    getElapsedMillis()   { return elapsedNanos / 1_000_000L; }
    public double  getElapsedSeconds()  { return elapsedNanos / 1_000_000_000.0; }
    public long    getHeapUsedBytes()   { return heapUsedBytes; }
    public boolean isSuccess()          { return success; }
    public String  getErrorMessage()    { return errorMessage; }

    @Override
    public String toString() {
        if (!success) {
            return String.format(Locale.ROOT, "%-32s %-12s n=%-6d FAILED (%s)",
                    algorithmName, complexityOrder, matrixSize, errorMessage);
        }
        return String.format(Locale.ROOT, "%-32s %-12s n=%-6d %,10d ms",
                algorithmName, complexityOrder, matrixSize, getElapsedMillis());
    }
}
