public class Instalacion extends Orden {
    private final int puntosInstalar;

    public Instalacion(String id, String cliente, String direccion, double horas, ZonaTrabajo zona, int puntos) {
        super(id, cliente, direccion, horas, zona);
        if (puntos <= 0)
            throw new IllegalArgumentException("Los puntos deben ser positivos.");
        puntosInstalar = puntos;
    }

    public int getPuntosInstalar() {
        return puntosInstalar;
    }

    @Override
    public String categoria() {
        return "Instalacion";
    }

    @Override
    public double calcularCosto() {
        double base = getZona() == ZonaTrabajo.METROPOLITANA ? 100 : 175;
        return base + 90 * Math.ceil(Math.max(getHorasEstimadas(), puntosInstalar * 0.75));
    }

    @Override
    public int calcularTiempo() {
        return getZona() == ZonaTrabajo.METROPOLITANA ? 3 : 5;
    }

    @Override
    public int maxVisitas() {
        return 3;
    }
}