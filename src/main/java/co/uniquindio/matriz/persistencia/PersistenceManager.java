package co.uniquindio.matriz.persistencia;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PersistenceManager {

	private static final DateTimeFormatter FILE_TIMESTAMP =
			DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

	private final Path matricesDirectory;

	public PersistenceManager() {
		this(Paths.get("data", "matrices"));
	}

	public PersistenceManager(Path matricesDirectory) {
		this.matricesDirectory = matricesDirectory;
	}

	public Path saveMatrix(long[][] matrix, String matrixName, int matrixSize) throws IOException {
		Files.createDirectories(matricesDirectory);

		String stamp = LocalDateTime.now().format(FILE_TIMESTAMP);
		String safeName = sanitize(matrixName);
		Path file = matricesDirectory.resolve(
				String.format(Locale.ROOT, "%s_n%d_%s.csv", safeName, matrixSize, stamp));

		try (BufferedWriter writer = Files.newBufferedWriter(file,
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.WRITE)) {
			for (long[] row : matrix) {
				for (int j = 0; j < row.length; j++) {
					if (j > 0) {
						writer.write(',');
					}
					writer.write(Long.toString(row[j]));
				}
				writer.newLine();
			}
		}

		return file;
	}

	public MatrixSaveResult saveMatrices(long[][] matrixA, long[][] matrixB, int matrixSize)
			throws IOException {
		Path fileA = saveMatrix(matrixA, "matrix_a", matrixSize);
		Path fileB = saveMatrix(matrixB, "matrix_b", matrixSize);
		return new MatrixSaveResult(fileA, fileB);
	}

	private String sanitize(String value) {
		return value == null ? "matrix" : value.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]+", "_");
	}

	public record MatrixSaveResult(Path matrixAFile, Path matrixBFile) { }
}
