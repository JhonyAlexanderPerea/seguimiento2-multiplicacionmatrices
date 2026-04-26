package co.uniquindio.matriz.utilidades;

import co.uniquindio.matriz.modelo.BenchmarkResult;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * BenchmarkLogger
 * Genera un log tecnico, en la carpeta logs/.
 *
 * Cada ejecucion produce dos archivos:
 *   - logs/benchmark_n{tamano}_{timestamp}.log : reporte legible 
 *   - logs/benchmark_n{tamano}_{timestamp}.csv : tabla para analisis posterior
 *
 * El reporte legible se organiza en secciones claramente separadas:
 *   [ENVIRONMENT]    metadatos del sistema y la JVM
 *   [PARAMETERS]     parametros del benchmark (tamano, tipo, rango)
 *   [EXECUTION]      bloque detallado por algoritmo, en orden de ejecucion
 *   [RANKING]        tabla ordenada de mas rapido a mas lento
 *   [SUMMARY]        estadisticas agregadas (mejor, peor, mediana, total)
 */
public class BenchmarkLogger implements AutoCloseable {

    private static final DateTimeFormatter FILE_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter ISO_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static final String LINE_DOUBLE =
            "================================================================================";
    private static final String LINE_SINGLE =
            "--------------------------------------------------------------------------------";

    private final Path logFile;
    private final Path csvFile;
    private final BufferedWriter writer;
    private final BufferedWriter csvWriter;
    private final long sessionStartNs;
    private final int matrixSize;

    private long matrixGenerationNs = 0L;
    private final List<BenchmarkResult> results = new ArrayList<>();

    public BenchmarkLogger(int matrixSize) throws IOException {
        this.matrixSize = matrixSize;
        Path logsDir = Paths.get("logs");
        Files.createDirectories(logsDir);
        String stamp = LocalDateTime.now().format(FILE_FMT);
        this.logFile = logsDir.resolve(
                String.format("benchmark_n%d_%s.log", matrixSize, stamp));
        this.csvFile = logsDir.resolve(
                String.format("benchmark_n%d_%s.csv", matrixSize, stamp));
        this.writer = Files.newBufferedWriter(logFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
        this.csvWriter = Files.newBufferedWriter(csvFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
        this.sessionStartNs = System.nanoTime();
        writeCsvHeader();
    }

    public Path getLogFile() { return logFile; }
    public Path getCsvFile() { return csvFile; }

    // -------------------------------------------------------------- HEADER

    public void writeHeader(int matrixSize, long elementMin, long elementMax,
                            int algorithmCount,
                            List<String> algorithmNames) throws IOException {
        RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        Runtime jr = Runtime.getRuntime();

        double matrixBytesEachMB =
                (double) matrixSize * matrixSize * Long.BYTES / (1024.0 * 1024.0);

        line(LINE_DOUBLE);
        line("                MATRIX MULTIPLICATION BENCHMARK - TECHNICAL LOG");
        line(LINE_DOUBLE);
        line("");
        line("[ENVIRONMENT]");
        kv("session.start",        LocalDateTime.now().format(ISO_FMT));
        kv("session.timezone",     java.time.ZoneId.systemDefault().toString());
        kv("os",                   System.getProperty("os.name") + " "
                                   + System.getProperty("os.version") + " ("
                                   + System.getProperty("os.arch") + ")");
        kv("cpu.cores",            String.valueOf(os.getAvailableProcessors()));
        kv("cpu.load.avg",         String.format(Locale.ROOT, "%.2f",
                                       os.getSystemLoadAverage()));
        kv("jvm.vendor",           System.getProperty("java.vendor"));
        kv("jvm.version",          System.getProperty("java.version"));
        kv("jvm.runtime",          rt.getVmName() + " " + rt.getVmVersion());
        kv("jvm.heap.max",         formatMB(jr.maxMemory()));
        kv("jvm.heap.total",       formatMB(jr.totalMemory()));
        kv("jvm.heap.free",        formatMB(jr.freeMemory()));
        kv("jvm.uptime.ms",        String.valueOf(rt.getUptime()));
        kv("jvm.input.args",       rt.getInputArguments().isEmpty()
                                        ? "(none)"
                                        : String.join(" ", rt.getInputArguments()));
        line("");
        line("[PARAMETERS]");
        kv("matrix.size",          matrixSize + " x " + matrixSize);
        kv("matrix.cells",         String.format(Locale.ROOT, "%,d",
                                       (long) matrixSize * matrixSize));
        kv("matrix.element.type",  "long (8 bytes, signed 64-bit integer)");
        kv("matrix.element.range", "[" + String.format(Locale.ROOT, "%,d", elementMin)
                                   + " , " + String.format(Locale.ROOT, "%,d", elementMax) + "]");
        kv("matrix.bytes.each",    String.format(Locale.ROOT, "%.2f MB", matrixBytesEachMB));
        kv("matrix.bytes.both",    String.format(Locale.ROOT, "%.2f MB", matrixBytesEachMB * 2.0));
        kv("algorithms.count",     String.valueOf(algorithmCount));
        line("");
        line("[ALGORITHMS REGISTERED]  (in execution order)");
        for (int i = 0; i < algorithmNames.size(); i++) {
            line(String.format(Locale.ROOT, "  %2d. %s", i + 1, algorithmNames.get(i)));
        }
        line("");
        flush();
    }

    public void recordMatrixGeneration(long nanos) throws IOException {
        this.matrixGenerationNs = nanos;
        line("[MATRIX GENERATION]");
        kv("matrices.generated",   "2 (A, B)");
        kv("generation.elapsed",   formatElapsed(nanos));
        line("");
        line("[EXECUTION]");
        line(LINE_SINGLE);
        flush();
    }

    // --------------------------------------------------------- PER-ALGORITHM

    public void logAlgorithmStart(int index, int total, String name,
                                  String complexity) throws IOException {
        line(String.format(Locale.ROOT, "[%d/%d] %s", index, total, name));
        kv("  complexity",  complexity);
        kv("  start",       LocalDateTime.now().format(ISO_FMT));
        flush();
    }

    public void logAlgorithmEnd(BenchmarkResult r,
                                long heapUsedBytesBefore) throws IOException {
        results.add(r);
        long n = r.getMatrixSize();
        long ops = 2L * n * n * n; // multiplicaciones + sumas en O(n^3)
        double opsPerSec = r.getElapsedNanos() == 0 ? 0
                : (ops * 1_000_000_000.0) / r.getElapsedNanos();

        kv("  end",            LocalDateTime.now().format(ISO_FMT));
        kv("  elapsed",        formatElapsed(r.getElapsedNanos()));
        kv("  elapsed.ns",     String.format(Locale.ROOT, "%,d", r.getElapsedNanos()));
        kv("  elapsed.ms",     String.format(Locale.ROOT, "%,.3f",
                                   r.getElapsedNanos() / 1_000_000.0));
        kv("  elapsed.s",      String.format(Locale.ROOT, "%.3f",
                                   r.getElapsedSeconds()));
        kv("  throughput",     String.format(Locale.ROOT,
                                   "%,.2f equiv-ops/s  (assuming 2*n^3 ops)",
                                   opsPerSec));
        kv("  heap.before",    formatMB(heapUsedBytesBefore));
        kv("  heap.after",     formatMB(r.getHeapUsedBytes()));
        kv("  heap.delta",     formatMB(r.getHeapUsedBytes() - heapUsedBytesBefore));
        kv("  status",         "OK");
        line(LINE_SINGLE);
        flush();
        writeCsvRow(r);
    }

    public void logAlgorithmFailure(BenchmarkResult r, Throwable t) throws IOException {
        results.add(r);
        kv("  end",            LocalDateTime.now().format(ISO_FMT));
        kv("  status",         "FAILED");
        kv("  exception",      t.getClass().getName());
        kv("  message",        String.valueOf(t.getMessage()));
        line(LINE_SINGLE);
        flush();
        writeCsvRow(r);
    }

    // ----------------------------------------------------------- RANKING + SUMMARY

    public void writeRankingAndSummary() throws IOException {
        List<BenchmarkResult> ok = new ArrayList<>();
        for (BenchmarkResult r : results) if (r.isSuccess()) ok.add(r);
        ok.sort(Comparator.comparingLong(BenchmarkResult::getElapsedNanos));

        line("");
        line("[RANKING]  (fastest first)");
        line(LINE_SINGLE);
        line(String.format(Locale.ROOT,
                "%-4s %-32s %-12s %12s %10s %10s %8s",
                "Rank", "Algorithm", "Complexity", "Time",
                "vs Best", "vs Worst", "Share"));
        line(LINE_SINGLE);

        if (ok.isEmpty()) {
            line("  (no successful runs)");
        } else {
            long best  = ok.get(0).getElapsedNanos();
            long worst = ok.get(ok.size() - 1).getElapsedNanos();
            long total = 0;
            for (BenchmarkResult r : ok) total += r.getElapsedNanos();

            for (int i = 0; i < ok.size(); i++) {
                BenchmarkResult r = ok.get(i);
                double vsBest  = r.getElapsedNanos() / (double) best;
                double vsWorst = worst / (double) r.getElapsedNanos();
                double share   = (r.getElapsedNanos() * 100.0) / total;
                line(String.format(Locale.ROOT,
                        "%-4d %-32s %-12s %10s   %7.2fx   %7.2fx  %6.2f%%",
                        i + 1,
                        truncate(r.getAlgorithmName(), 32),
                        r.getComplexityOrder(),
                        formatMs(r.getElapsedNanos()),
                        vsBest,
                        vsWorst,
                        share));
            }
        }

        // Failed runs explicitly listed
        List<BenchmarkResult> failed = new ArrayList<>();
        for (BenchmarkResult r : results) if (!r.isSuccess()) failed.add(r);
        if (!failed.isEmpty()) {
            line("");
            line("[FAILED RUNS]");
            for (BenchmarkResult r : failed) {
                line(String.format(Locale.ROOT,
                        "  - %-32s %s", r.getAlgorithmName(), r.getErrorMessage()));
            }
        }

        // Summary stats
        line("");
        line("[SUMMARY]");
        long totalNs = System.nanoTime() - sessionStartNs;
        kv("session.end",          LocalDateTime.now().format(ISO_FMT));
        kv("session.total",        formatElapsed(totalNs));
        kv("matrix.generation",    formatElapsed(matrixGenerationNs));
        kv("algorithms.executed",  String.valueOf(results.size()));
        kv("algorithms.ok",        String.valueOf(ok.size()));
        kv("algorithms.failed",    String.valueOf(failed.size()));
        if (!ok.isEmpty()) {
            BenchmarkResult fastest = ok.get(0);
            BenchmarkResult slowest = ok.get(ok.size() - 1);
            BenchmarkResult median  = ok.get(ok.size() / 2);
            double speedup = slowest.getElapsedNanos() / (double) fastest.getElapsedNanos();
            kv("fastest.algorithm", fastest.getAlgorithmName()
                                    + "  (" + formatMs(fastest.getElapsedNanos()) + ")");
            kv("slowest.algorithm", slowest.getAlgorithmName()
                                    + "  (" + formatMs(slowest.getElapsedNanos()) + ")");
            kv("median.algorithm",  median.getAlgorithmName()
                                    + "  (" + formatMs(median.getElapsedNanos()) + ")");
            kv("speedup.range",     String.format(Locale.ROOT,
                                       "%.2fx (slowest / fastest)", speedup));
        }
        line(LINE_DOUBLE);
        line(" END OF REPORT");
        line(LINE_DOUBLE);
        flush();
    }

    // ------------------------------------------------------------- CSV

    private void writeCsvHeader() throws IOException {
        csvWriter.write("rank,algorithm,complexity,matrix_size,"
                + "elapsed_ns,elapsed_ms,elapsed_s,heap_used_MB,status,error\n");
        csvWriter.flush();
    }

    private void writeCsvRow(BenchmarkResult r) throws IOException {
        csvWriter.write(String.format(Locale.ROOT,
                "%d,%s,%s,%d,%d,%.3f,%.3f,%d,%s,%s%n",
                results.size(),
                csv(r.getAlgorithmName()),
                csv(r.getComplexityOrder()),
                r.getMatrixSize(),
                r.getElapsedNanos(),
                r.getElapsedNanos() / 1_000_000.0,
                r.getElapsedSeconds(),
                r.getHeapUsedBytes() / (1024 * 1024),
                r.isSuccess() ? "OK" : "FAILED",
                r.isSuccess() ? "" : csv(String.valueOf(r.getErrorMessage()))));
        csvWriter.flush();
    }

    private static String csv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    // -------------------------------------------------------- format helpers

    private static String formatMB(long bytes) {
        return String.format(Locale.ROOT, "%.2f MB", bytes / (1024.0 * 1024.0));
    }

    private static String formatElapsed(long nanos) {
        double ms = nanos / 1_000_000.0;
        double s  = nanos / 1_000_000_000.0;
        if (s >= 1.0) {
            return String.format(Locale.ROOT, "%.3f s   (%,.3f ms)", s, ms);
        }
        return String.format(Locale.ROOT, "%,.3f ms", ms);
    }

    private static String formatMs(long nanos) {
        double ms = nanos / 1_000_000.0;
        return String.format(Locale.ROOT, "%,.2f ms", ms);
    }

    private static String truncate(String s, int max) {
        return (s.length() <= max) ? s : s.substring(0, max - 1) + ".";
    }

    private void kv(String key, String value) throws IOException {
        line(String.format("%-22s : %s", key, value));
    }

    private void line(String s) throws IOException {
        writer.write(s);
        writer.newLine();
    }

    private void flush() throws IOException { writer.flush(); }

    @Override
    public void close() throws IOException {
        try { writer.close(); } finally { csvWriter.close(); }
    }
}
