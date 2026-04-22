# Seguimiento 2 - Multiplicacion de Matrices

Proyecto en Java (Maven) para implementar, comparar y analizar algoritmos de multiplicacion de matrices cuadradas grandes.

## Objetivo

Evaluar el comportamiento de diferentes tecnicas de multiplicacion de matrices en terminos de complejidad teorica y rendimiento practico.

## Tecnologias

- Java 17
- Maven
- JFreeChart (graficas)
- JUnit 5 (pruebas)

## Algoritmos Implementados

En `src/main/java/co/uniquindio/matriz/algoritmos/`:

- `NaivOnArray`: multiplicacion clasica triple bucle. Complejidad `O(n^3)`.
- `NaivLoopUnrollingTwo`: version naiv con desenrollado por factor 2. Complejidad `O(n^3)`.
- `NaivLoopUnrollingFour`: version naiv con desenrollado por factor 4. Complejidad `O(n^3)`.
- `WinogradOriginal`: reduce multiplicaciones usando factores de filas/columnas. Complejidad `O(n^3)`.
- `WinogradScaled`: Winograd con escalado previo para estabilidad numerica. Complejidad `O(n^3)`.
- `StrassenNaiv`: algoritmo de Strassen con caso base naiv. Complejidad `O(n^2.807)`.
- `StrassenWinograd`: variante de Strassen con precomputos y padding a potencia de 2 para manejar tamanos generales. Complejidad `O(n^2.807)`.

## Estructura del Proyecto

```text
pom.xml
data/
  matrices/
  resultados/
src/
  main/
    java/
      co/uniquindio/
        Main.java
        matriz/
          algoritmos/
          modelo/
          persistencia/
          reportes/
          utilidades/
  test/
    java/
```

## Compilar y Ejecutar

### 1) Compilar

```bash
mvn clean compile
```

### 2) Ejecutar pruebas

```bash
mvn test
```

### 3) Empaquetar

```bash
mvn clean package
```

Se genera un artefacto ejecutable con dependencias en `target/` usando `maven-assembly-plugin`.

## Estado Actual

- El punto de entrada actual es `co.uniquindio.Main`.
- Hay clases utilitarias y de reportes aun en desarrollo (`utilidades`, `reportes`, `modelo`, `persistencia`).
- La carpeta `data/` esta preparada para almacenar matrices de entrada y resultados de benchmarks.

## Siguientes Pasos Recomendados

1. Implementar `MatrixGenerator` para generar datasets reproducibles (semilla fija).
2. Implementar `BenchmarkRunner` para medir tiempos por algoritmo y tamano.
3. Persistir resultados en `data/resultados/` con formato CSV o JSON.
4. Generar graficas comparativas con JFreeChart.
5. Agregar pruebas de regresion para validar exactitud entre algoritmos.
