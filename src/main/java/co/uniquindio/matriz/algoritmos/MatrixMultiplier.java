package co.uniquindio.matriz.algoritmos;

public interface MatrixMultiplier {
 
    /**
     * Multiplica dos matrices cuadradas A y B.
     *
     * @param A Matriz izquierda (n x n)
     * @param B Matriz derecha (n x n)
     * @return Matriz resultado C = A x B (n x n)
     */
    long[][] multiply(long[][] A, long[][] B);
 
    /**
     * Retorna el nombre del algoritmo para reportes y persistencia.
     */
    String getName();
 
    /**
     * Retorna el orden de complejidad teórico del algoritmo.
     * Ejemplo: "O(n³)", "O(n^2.807)", etc.
     */
    String getComplexityOrder();
}