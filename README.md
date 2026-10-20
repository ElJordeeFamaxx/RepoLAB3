# TecniHogar - Lab 3 Herencia (CC2008)

## Compilar y ejecutar
Desde la carpeta del proyecto:

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

Java 8 o superior. No usa bibliotecas externas, base de datos ni archivos de persistencia.

## Pruebas
```bash
javac -encoding UTF-8 -cp out -d out tests/Pruebas.java
java -cp out Pruebas
```

## Diseño
- `Orden` abstracta implementa `Cotizable`.
- `Diagnostico`, `Instalacion`, `MantenimientoAC` heredan de `Orden`.
- `InstalacionCertificada` hereda de `Instalacion`.
- `Jornada` implementa `Iterable<Orden>` y devuelve un iterador de solo lectura.
- `Sistema` mantiene colecciones e ingresos, y centraliza operaciones.
- Todas las clases están en el paquete por defecto para compilar fácilmente con `javac src/*.java`.

## Supuesto a confirmar
El enunciado no define numéricamente la tarifa base del mantenimiento AC. Se asume Q100 metropolitana, reutilizando la base de instalación. Se puede cambiar en `MantenimientoAC.TARIFA_BASE_METROPOLITANA`.

## Consideraciones
- Las 8 órdenes precargadas no generan ingresos.
- Se contabiliza Q40 por cada visita posterior a la primera, cuando inicia la jornada.
- Una cotización no modifica el sistema.
- `InstalacionCertificada` admite materiales Q0 o superiores, pues el enunciado no exige estrictamente un valor positivo.
- No se implementa persistencia: al cerrar el programa se pierden los cambios.