package co.uniquindio.matriz.modelo;

public class BenchmarkResult {

    private final String algorithmName;
    private final String complexityOrder;
    private final int matrixSize;
    private final long elapsedMillis;

    public BenchmarkResult(String algorithmName, String complexityOrder,
                           int matrixSize, long elapsedMillis) {
        this.algorithmName = algorithmName;
        this.complexityOrder = complexityOrder;
        this.matrixSize = matrixSize;
        this.elapsedMillis = elapsedMillis;
    }

    public String getAlgorithmName()  { return algorithmName; }
    public String getComplexityOrder() { return complexityOrder; }
    public int    getMatrixSize()      { return matrixSize; }
    public long   getElapsedMillis()   { return elapsedMillis; }

    @Override
    public String toString() {
        return String.format("%-30s %-12s n=%-6d %,d ms",
                algorithmName, complexityOrder, matrixSize, elapsedMillis);
    }
}
