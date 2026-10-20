import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Sistema {
    private final Map<String, Orden> ordenes = new LinkedHashMap<>();
    private final Map<String, Cuadrilla> cuadrillas = new LinkedHashMap<>();
    private final Map<String, Jornada> jornadas = new LinkedHashMap<>();
    private final Map<String, Double> ingresosPorCategoria = new LinkedHashMap<>();
    private double ingresoRegistros, ingresoAdicionales;

    public Collection<Orden> getOrdenes() {
        return Collections.unmodifiableCollection(ordenes.values());
    }

    public Collection<Cuadrilla> getCuadrillas() {
        return Collections.unmodifiableCollection(cuadrillas.values());
    }

    public Collection<Jornada> getJornadas() {
        return Collections.unmodifiableCollection(jornadas.values());
    }

    public Orden buscarOrden(String id) {
        return ordenes.get(id);
    }

    public Cuadrilla buscarCuadrilla(String id) {
        return cuadrillas.get(id);
    }

    public Jornada buscarJornada(String id) {
        return jornadas.get(id);
    }

    public double getIngresoRegistros() {
        return ingresoRegistros;
    }

    public double getIngresoAdicionales() {
        return ingresoAdicionales;
    }

    public double getIngresoTotal() {
        return ingresoRegistros + ingresoAdicionales;
    }

    public String cotizar(Orden o) {
        if (o == null)
            throw new IllegalArgumentException("Orden inexistente.");
        return o.categoria() + " / costo Q" + String.format(java.util.Locale.US, "%.2f", o.calcularCosto())
                + " / atencion en " + o.calcularTiempo() + " dia(s) habil(es)";
    }

    public void registrarOrden(Orden o) {
        registrarOrdenInterna(o, true);
    }

    private void registrarOrdenInterna(Orden o, boolean cobrar) {
        if (o == null || ordenes.containsKey(o.getId()) || o.getEstado() != EstadoOrden.PENDIENTE || o.getVisitas() != 0
                || o.getJornadaActiva() != null)
            throw new IllegalArgumentException("Orden invalida o codigo repetido.");
        double costo = o.calcularCosto();
        if (!Double.isFinite(costo) || costo < 0)
            throw new IllegalArgumentException("Costo invalido.");
        ordenes.put(o.getId(), o);
        if (cobrar) {
            ingresoRegistros += costo;
            ingresosPorCategoria.merge(o.categoria(), costo, Double::sum);
        }
    }

    public void registrarCuadrilla(Cuadrilla c) {
        if (c == null || cuadrillas.containsKey(c.getCodigo()))
            throw new IllegalArgumentException("Cuadrilla invalida o repetida.");
        cuadrillas.put(c.getCodigo(), c);
    }

    public Jornada crearJornada(String codigo, String codigoCuadrilla) {
        if (Orden.vacio(codigo) || jornadas.containsKey(codigo.trim()))
            throw new IllegalArgumentException("Codigo de jornada invalido o repetido.");
        Cuadrilla c = cuadrillas.get(codigoCuadrilla);
        if (c == null)
            throw new IllegalArgumentException("Cuadrilla inexistente.");
        for (Jornada j : jornadas.values())
            if (j.getCuadrilla() == c && j.getEstado() != EstadoJornada.CERRADA)
                throw new IllegalStateException("La cuadrilla ya tiene una jornada sin cerrar.");
        Jornada j = new Jornada(codigo, c);
        jornadas.put(j.getCodigo(), j);
        return j;
    }

    private Jornada jornada(String id) {
        Jornada j = jornadas.get(id);
        if (j == null)
            throw new IllegalArgumentException("Jornada inexistente.");
        return j;
    }

    private Orden orden(String id) {
        Orden o = ordenes.get(id);
        if (o == null)
            throw new IllegalArgumentException("Orden inexistente.");
        return o;
    }

    public void asignarOrden(String jornadaId, String ordenId) {
        jornada(jornadaId).asignarOrden(orden(ordenId));
    }

    public void retirarOrden(String jornadaId, String ordenId) {
        jornada(jornadaId).retirarOrden(orden(ordenId));
    }

    public void iniciarJornada(String jornadaId) {
        Jornada j = jornada(jornadaId);
        double cargos = j.iniciar();
        ingresoAdicionales += cargos;
        for (Orden o : j.getOrdenes())
            if (o.getVisitas() > 1)
                ingresosPorCategoria.merge(o.categoria(), 40.0, Double::sum);
    }

    public void registrarResultado(String jornadaId, String ordenId, boolean completada) {
        jornada(jornadaId).registrarResultado(orden(ordenId), completada);
    }

    public String reporteOrdenes() {
        StringBuilder sb = new StringBuilder("ORDENES POR CATEGORIA Y ESTADO\n");
        Map<String, int[]> conteos = new LinkedHashMap<>();
        for (Orden o : ordenes.values())
            conteos.computeIfAbsent(o.categoria(), k -> new int[EstadoOrden.values().length])[o.getEstado()
                    .ordinal()]++;
        for (Map.Entry<String, int[]> e : conteos.entrySet()) {
            int total = 0;
            for (int n : e.getValue())
                total += n;
            sb.append(e.getKey()).append(" / total =").append(total);
            for (EstadoOrden est : EstadoOrden.values())
                sb.append(" / ").append(est).append("=").append(e.getValue()[est.ordinal()]);
            sb.append('\n');
        }
        return sb.toString();
    }

    public String reporteIngresos() {
        StringBuilder sb = new StringBuilder(String.format(java.util.Locale.US,
                "INGRESOS\nRegistros: Q%.2f\nVisitas adicionales: Q%.2f\nTotal: Q%.2f\nPor categoria (incluye adicionales):\n",
                ingresoRegistros, ingresoAdicionales, getIngresoTotal()));
        for (Map.Entry<String, Double> e : ingresosPorCategoria.entrySet())
            sb.append(String.format(java.util.Locale.US, "%s: Q%.2f%n", e.getKey(), e.getValue()));
        return sb.toString();
    }

    public String reporteJornadas() {
        StringBuilder sb = new StringBuilder("JORNADAS SIN CERRAR\n");
        for (Jornada j : jornadas.values())
            if (j.getEstado() != EstadoJornada.CERRADA)
                sb.append(j).append(" / disponibles=")
                        .append(String.format(java.util.Locale.US, "%.2f", j.horasDisponibles())).append("h\n");
        return sb.toString();
    }

    public void cargarEjemplos() {
        registrarCuadrilla(new Cuadrilla("C1", 8, false));
        registrarCuadrilla(new Cuadrilla("C2", 10, true));
        registrarCuadrilla(new Cuadrilla("C3", 6, false));
        registrarOrdenInterna(new Diagnostico("D1", "Ana", "Zona 1", 1, ZonaTrabajo.METROPOLITANA), false);
        registrarOrdenInterna(new Diagnostico("D2", "Luis", "Mixco", 1.5, ZonaTrabajo.PERIFERICA), false);
        registrarOrdenInterna(new Instalacion("I1", "Eva", "Zona 4", 2.5, ZonaTrabajo.METROPOLITANA, 5), false);
        registrarOrdenInterna(new Instalacion("I2", "Juan", "Amatitlan", 3, ZonaTrabajo.PERIFERICA, 2), false);
        registrarOrdenInterna(
                new InstalacionCertificada("E1", "Rosa", "Zona 10", 2.5, ZonaTrabajo.METROPOLITANA, 5, 2000), false);
        registrarOrdenInterna(
                new InstalacionCertificada("E2", "Mario", "Villa Nueva", 2, ZonaTrabajo.PERIFERICA, 3, 1000), false);
        registrarOrdenInterna(new MantenimientoAC("A1", "Sara", "Zona 9", 1.5, ZonaTrabajo.METROPOLITANA, 12000),
                false);
        registrarOrdenInterna(new MantenimientoAC("A2", "Pablo", "Zona 14", 2, ZonaTrabajo.METROPOLITANA, 24000),
                false);
    }
}
