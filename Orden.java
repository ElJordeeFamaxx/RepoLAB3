public abstract class Orden implements Cotizable {
    private final String id, nombreCliente, direccion;
    private final double horasEstimadas;
    private final ZonaTrabajo zona;
    private EstadoOrden estado = EstadoOrden.PENDIENTE;
    private int visitas = 0;
    private Jornada jornadaActiva;

    protected Orden(String id, String nombreCliente, String direccion, double horasEstimadas, ZonaTrabajo zona) {
        if (vacio(id) || vacio(nombreCliente) || vacio(direccion) || zona == null || !Double.isFinite(horasEstimadas)
                || horasEstimadas <= 0)
            throw new IllegalArgumentException("Datos de orden invalidos (horas deben ser positivas).");
        this.id = id.trim();
        this.nombreCliente = nombreCliente.trim();
        this.direccion = direccion.trim();
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
    }

    protected static boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    public String getId() {
        return id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getHorasEstimadas() {
        return horasEstimadas;
    }

    public ZonaTrabajo getZona() {
        return zona;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public int getVisitas() {
        return visitas;
    }

    public Jornada getJornadaActiva() {
        return jornadaActiva;
    }

    public abstract String categoria();

    public abstract int maxVisitas();

    public boolean compatible(Cuadrilla cuadrilla) {
        return cuadrilla != null;
    }

    void asignar(Jornada jornada) {
        if (jornada == null || estado != EstadoOrden.PENDIENTE || jornadaActiva != null || visitas >= maxVisitas())
            throw new IllegalStateException("Orden no disponible para asignacion.");
        jornadaActiva = jornada;
    }

    void retirar(Jornada jornada) {
        if (jornadaActiva != jornada || estado != EstadoOrden.PENDIENTE)
            throw new IllegalStateException("La orden no puede retirarse.");
        jornadaActiva = null;
    }

    // Devuelve el cargo generado en el inicio de la visita.
    double iniciarVisita(Jornada jornada) {
        if (jornadaActiva != jornada || estado != EstadoOrden.PENDIENTE || visitas >= maxVisitas())
            throw new IllegalStateException("No se puede iniciar la visita.");
        visitas++;
        estado = EstadoOrden.EN_ATENCION;
        return visitas > 1 ? 40.0 : 0.0;
    }

    void registrarResultado(Jornada jornada, boolean completada) {
        if (jornadaActiva != jornada || estado != EstadoOrden.EN_ATENCION)
            throw new IllegalStateException("La orden no esta en atencion en esta jornada.");
        estado = completada ? EstadoOrden.COMPLETADA
                : (visitas >= maxVisitas() ? EstadoOrden.CANCELADA : EstadoOrden.PENDIENTE);
        jornadaActiva = null;
    }

    @Override
    public String toString() {
        return id + " / " + categoria() + " / " + nombreCliente + " / " + zona + " / " + estado
                + " / horas =" + horasEstimadas + " / visitas =" + visitas + "/" + maxVisitas();
    }
}
