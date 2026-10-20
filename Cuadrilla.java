public class Cuadrilla {
    private final String codigo;
    private final double capacidadHoras;
    private final boolean certificadaRefrigeracion;

    public Cuadrilla(String codigo, double capacidadHoras, boolean certificadaRefrigeracion) {
        if (Orden.vacio(codigo) || !Double.isFinite(capacidadHoras) || capacidadHoras <= 0)
            throw new IllegalArgumentException("Datos de cuadrilla invalidos.");
        this.codigo = codigo.trim();
        this.capacidadHoras = capacidadHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getCapacidadHoras() {
        return capacidadHoras;
    }

    public boolean isCertificadaRefrigeracion() {
        return certificadaRefrigeracion;
    }

    @Override
    public String toString() {
        return codigo + " / capacidad =" + capacidadHoras + "h / refrigeracion ="
                + (certificadaRefrigeracion ? "SI" : "NO");
    }
}