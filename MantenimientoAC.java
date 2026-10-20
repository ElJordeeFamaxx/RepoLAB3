public class MantenimientoAC extends Orden {
    // El enunciado no precisa la tarifa base de mantenimiento: se adopta Q100
    // (metropolitana).
    public static final double TARIFA_BASE_METROPOLITANA = 100.0;
    private final int capacidadBTU;

    public MantenimientoAC(String id, String cliente, String direccion, double horas, ZonaTrabajo zona, int btu) {
        super(id, cliente, direccion, horas, zona);
        if (zona != ZonaTrabajo.METROPOLITANA)
            throw new IllegalArgumentException("AC solo en zona metropolitana.");
        if (btu < 9000 || btu > 60000)
            throw new IllegalArgumentException("BTU fuera del rango 9000-60000.");
        capacidadBTU = btu;
    }

    public int getCapacidadBTU() {
        return capacidadBTU;
    }

    @Override
    public String categoria() {
        return "Mantenimiento AC";
    }

    @Override
    public double calcularCosto() {
        return TARIFA_BASE_METROPOLITANA + 110 * getHorasEstimadas() + 80;
    }

    @Override
    public int calcularTiempo() {
        return 2;
    }

    @Override
    public int maxVisitas() {
        return 1;
    }

    @Override
    public boolean compatible(Cuadrilla cuadrilla) {
        return cuadrilla != null && cuadrilla.isCertificadaRefrigeracion();
    }
}