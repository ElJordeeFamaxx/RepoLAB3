import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jornada implements Iterable<Orden> {
    private final String codigo;
    private final Cuadrilla cuadrilla;
    private final List<Orden> ordenes = new ArrayList<>();
    private final List<Orden> sinResultado = new ArrayList<>();
    private EstadoJornada estado = EstadoJornada.PREPARACION;

    Jornada(String codigo, Cuadrilla cuadrilla) {
        if (Orden.vacio(codigo) || cuadrilla == null)
            throw new IllegalArgumentException("Jornada invalida.");
        this.codigo = codigo.trim();
        this.cuadrilla = cuadrilla;
    }

    public String getCodigo() {
        return codigo;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public List<Orden> getOrdenes() {
        return Collections.unmodifiableList(ordenes);
    }

    @Override
    public java.util.Iterator<Orden> iterator() {
        return getOrdenes().iterator();
    }

    public double horasOcupadas() {
        double h = 0;
        for (Orden o : ordenes)
            h += o.getHorasEstimadas();
        return h;
    }

    public double horasDisponibles() {
        return cuadrilla.getCapacidadHoras() - horasOcupadas();
    }

    public boolean isVacia() {
        return ordenes.isEmpty();
    }

    void asignarOrden(Orden o) {
        if (estado != EstadoJornada.PREPARACION || o == null || !o.compatible(cuadrilla)
                || o.getHorasEstimadas() > horasDisponibles() + 1e-9)
            throw new IllegalStateException("Asignacion invalida: estado, capacidad o certificacion.");
        o.asignar(this);
        ordenes.add(o);
    }

    void retirarOrden(Orden o) {
        if (estado != EstadoJornada.PREPARACION || !ordenes.contains(o))
            throw new IllegalStateException("Solo se retiran ordenes durante preparacion.");
        o.retirar(this);
        ordenes.remove(o);
    }

    // La validacion completa se realiza antes de cualquier cambio.
    double iniciar() {
        if (estado != EstadoJornada.PREPARACION || ordenes.isEmpty())
            throw new IllegalStateException("Solo se inicia una jornada en preparacion y no vacia.");
        for (Orden o : ordenes)
            if (o.getEstado() != EstadoOrden.PENDIENTE || o.getJornadaActiva() != this
                    || o.getVisitas() >= o.maxVisitas())
                throw new IllegalStateException("Orden no disponible para iniciar.");
        double cargos = 0;
        for (Orden o : ordenes)
            cargos += o.iniciarVisita(this);
        sinResultado.addAll(ordenes);
        estado = EstadoJornada.EN_CURSO;
        return cargos;
    }

    void registrarResultado(Orden o, boolean completada) {
        if (estado != EstadoJornada.EN_CURSO || !sinResultado.contains(o))
            throw new IllegalStateException("Resultado invalido o duplicado.");
        o.registrarResultado(this, completada);
        sinResultado.remove(o);
        if (sinResultado.isEmpty())
            estado = EstadoJornada.CERRADA;
    }

    @Override
    public String toString() {
        return codigo + " / cuadrilla =" + cuadrilla.getCodigo() + " / " + estado + " / ordenes =" + ordenes.size()
                + " / horas =" + horasOcupadas() + "/" + cuadrilla.getCapacidadHoras();
    }
}