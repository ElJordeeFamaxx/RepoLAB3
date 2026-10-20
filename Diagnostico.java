public class Diagnostico extends Orden {
    public Diagnostico(String id, String cliente, String direccion, double horas, ZonaTrabajo zona) {
        super(id, cliente, direccion, horas, zona);
        if (horas > 1.5)
            throw new IllegalArgumentException("Diagnostico: maximo 1.5 horas.");
    }

    @Override
    public String categoria() {
        return "Diagnostico";
    }

    @Override
    public double calcularCosto() {
        return getZona() == ZonaTrabajo.METROPOLITANA ? 150 : 225;
    }

    @Override
    public int calcularTiempo() {
        return getZona() == ZonaTrabajo.METROPOLITANA ? 1 : 2;
    }

    @Override
    public int maxVisitas() {
        return 3;
    }
}
