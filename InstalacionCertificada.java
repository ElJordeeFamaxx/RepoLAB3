public class InstalacionCertificada extends Instalacion {
    private final double valorMateriales;

    public InstalacionCertificada(String id, String cliente, String direccion, double horas, ZonaTrabajo zona,
            int puntos, double materiales) {
        super(id, cliente, direccion, horas, zona, puntos);
        if (!Double.isFinite(materiales) || materiales < 0)
            throw new IllegalArgumentException("Valor de materiales invalido.");
        valorMateriales = materiales;
    }

    public double getValorMateriales() {
        return valorMateriales;
    }

    @Override
    public String categoria() {
        return "Instalacion certificada";
    }

    @Override
    public double calcularCosto() {
        return super.calcularCosto() + 120 + valorMateriales * 0.05;
    }

    @Override
    public int calcularTiempo() {
        return super.calcularTiempo() + 2;
    }

    @Override
    public int maxVisitas() {
        return 2;
    }
}