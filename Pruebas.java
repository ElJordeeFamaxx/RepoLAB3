public class Pruebas {
    private static int aprobadas = 0;

    private static void ok(boolean b, String mensaje) {
        if (!b)
            throw new AssertionError(mensaje);
        aprobadas++;
    }

    private static void igual(double a, double b) {
        ok(Math.abs(a - b) < 0.000001, "Esperado " + b + ", obtenido " + a);
    }

    private static void error(Runnable r) {
        boolean lanzo = false;
        try {
            r.run();
        } catch (IllegalArgumentException | IllegalStateException e) {
            lanzo = true;
        }
        ok(lanzo, "Se esperaba rechazo.");
    }

    public static void main(String[] args) {
        Sistema s = new Sistema();
        s.cargarEjemplos();
        ok(s.getOrdenes().size() == 8, "8 iniciales");
        ok(s.getCuadrillas().size() == 3, "3 cuadrillas");
        igual(s.getIngresoTotal(), 0);
        igual(s.buscarOrden("D1").calcularCosto(), 150);
        igual(s.buscarOrden("D2").calcularCosto(), 225);
        igual(s.buscarOrden("I1").calcularCosto(), 460);
        igual(s.buscarOrden("I2").calcularCosto(), 445);
        igual(s.buscarOrden("E1").calcularCosto(), 680);
        igual(s.buscarOrden("E2").calcularCosto(), 615);
        igual(s.buscarOrden("A1").calcularCosto(), 345);
        ok(s.buscarOrden("D1").calcularTiempo() == 1, "D1 tiempo");
        ok(s.buscarOrden("D2").calcularTiempo() == 2, "D2 tiempo");
        ok(s.buscarOrden("I1").calcularTiempo() == 3, "I1 tiempo");
        ok(s.buscarOrden("I2").calcularTiempo() == 5, "I2 tiempo");
        ok(s.buscarOrden("E1").calcularTiempo() == 5, "E1 tiempo");
        ok(s.buscarOrden("E2").calcularTiempo() == 7, "E2 tiempo");
        ok(s.buscarOrden("A1").calcularTiempo() == 2, "A1 tiempo");
        error(() -> new Diagnostico("X", "A", "D", 1.6, ZonaTrabajo.METROPOLITANA));
        error(() -> new MantenimientoAC("X", "A", "D", 1, ZonaTrabajo.PERIFERICA, 12000));
        error(() -> new MantenimientoAC("X", "A", "D", 1, ZonaTrabajo.METROPOLITANA, 8999));
        error(() -> new Instalacion("X", "A", "D", 1, ZonaTrabajo.METROPOLITANA, 0));
        error(() -> s.registrarOrden(new Diagnostico("D1", "A", "D", 1, ZonaTrabajo.METROPOLITANA)));
        s.cotizar(new Diagnostico("X", "A", "D", 1, ZonaTrabajo.METROPOLITANA));
        igual(s.getIngresoTotal(), 0);
        Jornada j = s.crearJornada("J1", "C1");
        error(() -> s.crearJornada("J2", "C1"));
        error(() -> s.asignarOrden("J1", "A1"));
        error(() -> s.iniciarJornada("J1"));
        s.asignarOrden("J1", "D1");
        error(() -> s.asignarOrden("J1", "D1"));
        s.retirarOrden("J1", "D1");
        igual(s.getIngresoTotal(), 0);
        s.asignarOrden("J1", "D1");
        s.iniciarJornada("J1");
        ok(s.buscarOrden("D1").getEstado() == EstadoOrden.EN_ATENCION, "en atencion");
        error(() -> s.retirarOrden("J1", "D1"));
        s.registrarResultado("J1", "D1", false);
        ok(j.getEstado() == EstadoJornada.CERRADA, "jornada cerrada");
        ok(s.buscarOrden("D1").getEstado() == EstadoOrden.PENDIENTE, "reprogramada");
        s.crearJornada("J2", "C1");
        s.asignarOrden("J2", "D1");
        s.iniciarJornada("J2");
        igual(s.getIngresoAdicionales(), 40);
        s.registrarResultado("J2", "D1", false);
        s.crearJornada("J3", "C1");
        s.asignarOrden("J3", "D1");
        s.iniciarJornada("J3");
        igual(s.getIngresoAdicionales(), 80);
        s.registrarResultado("J3", "D1", false);
        ok(s.buscarOrden("D1").getEstado() == EstadoOrden.CANCELADA, "cancelada");
        s.crearJornada("J4", "C1");
        error(() -> s.asignarOrden("J4", "D1"));
        igual(s.getIngresoTotal(), 80);
        s.registrarOrden(new Instalacion("BIG", "A", "D", 9, ZonaTrabajo.METROPOLITANA, 1));
        double antes = s.getIngresoTotal();
        error(() -> s.asignarOrden("J4", "BIG"));
        igual(s.getIngresoTotal(), antes);
        s.asignarOrden("J4", "I1");
        ok(s.buscarOrden("I1").getEstado() == EstadoOrden.PENDIENTE, "asignada pendiente");
        error(() -> s.asignarOrden("J4", "I1"));
        ok(s.reporteOrdenes().contains("CANCELADA=1"), "reporte estados");
        ok(s.reporteIngresos().contains("Visitas adicionales: Q80.00"), "reporte ingresos");
        System.out.println("Pruebas aprobadas: " + aprobadas);
    }
}
