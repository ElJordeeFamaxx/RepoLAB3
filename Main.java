import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final Sistema sistema = new Sistema();

    public static void main(String[] args) {
        sistema.cargarEjemplos();
        while (true) {
            System.out.println(
                    "\n--- TECNIHOGAR ---\n1. Cotizar\n2. Registrar orden\n3. Registrar cuadrilla\n4. Crear jornada\n5. Asignar orden\n6. Retirar orden\n7. Iniciar jornada\n8. Registrar resultado\n9. Consultar ordenes\n10. Consultar cuadrillas\n11. Consultar jornadas\n12. Reportes\n0. Salir");
            try {
                int op = entero("Opcion: ");
                if (op == 0) {
                    System.out.println("Hasta luego.");
                    break;
                }
                accion(op);
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Operacion rechazada: " + e.getMessage());
            }
        }
    }

    private static String texto(String pregunta) {
        System.out.print(pregunta);
        if (!sc.hasNextLine())
            throw new IllegalStateException("Entrada finalizada.");
        return sc.nextLine().trim();
    }

    private static int entero(String p) {
        try {
            return Integer.parseInt(texto(p));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Se esperaba un numero entero.");
        }
    }

    private static double decimal(String p) {
        try {
            double d = Double.parseDouble(texto(p).replace(',', '.'));
            if (!Double.isFinite(d))
                throw new NumberFormatException();
            return d;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Se esperaba un numero decimal valido.");
        }
    }

    private static boolean siNo(String p) {
        String s = texto(p + " (s/n): ");
        if (s.equalsIgnoreCase("s"))
            return true;
        if (s.equalsIgnoreCase("n"))
            return false;
        throw new IllegalArgumentException("Responda s o n.");
    }

    private static ZonaTrabajo zona() {
        int z = entero("Zona (1 Metropolitana, 2 Periferica): ");
        if (z == 1)
            return ZonaTrabajo.METROPOLITANA;
        if (z == 2)
            return ZonaTrabajo.PERIFERICA;
        throw new IllegalArgumentException("Zona invalida.");
    }

    private static Orden nuevaOrden() {
        int tipo = entero("Categoria (1 Diagnostico, 2 Instalacion, 3 Certificada, 4 Mantenimiento AC): ");
        if (tipo < 1 || tipo > 4)
            throw new IllegalArgumentException("Categoria invalida.");
        String id = texto("Codigo: "), cliente = texto("Cliente: "), dir = texto("Direccion: ");
        double horas = decimal("Horas estimadas: ");
        ZonaTrabajo zona = zona();
        switch (tipo) {
            case 1:
                return new Diagnostico(id, cliente, dir, horas, zona);
            case 2:
                return new Instalacion(id, cliente, dir, horas, zona, entero("Puntos: "));
            case 3:
                return new InstalacionCertificada(id, cliente, dir, horas, zona, entero("Puntos: "),
                        decimal("Valor materiales: "));
            default:
                return new MantenimientoAC(id, cliente, dir, horas, zona, entero("BTU: "));
        }
    }

    private static void accion(int op) {
        switch (op) {
            case 1:
                System.out.println(sistema.cotizar(nuevaOrden()));
                break;
            case 2:
                Orden o = nuevaOrden();
                System.out.println(sistema.cotizar(o));
                if (siNo("Aceptar y cobrar?")) {
                    sistema.registrarOrden(o);
                    System.out.println("Registrada y cobrada.");
                }
                break;
            case 3:
                sistema.registrarCuadrilla(new Cuadrilla(texto("Codigo: "), decimal("Capacidad horas: "),
                        siNo("Certificada refrigeracion?")));
                System.out.println("Cuadrilla registrada.");
                break;
            case 4:
                System.out.println(
                        "Creada: " + sistema.crearJornada(texto("Codigo jornada: "), texto("Codigo cuadrilla: ")));
                break;
            case 5:
                sistema.asignarOrden(texto("Jornada: "), texto("Orden: "));
                System.out.println("Asignada.");
                break;
            case 6:
                sistema.retirarOrden(texto("Jornada: "), texto("Orden: "));
                System.out.println("Retirada.");
                break;
            case 7:
                sistema.iniciarJornada(texto("Jornada: "));
                System.out.println("Jornada iniciada.");
                break;
            case 8:
                sistema.registrarResultado(texto("Jornada: "), texto("Orden: "), siNo("Completada?"));
                System.out.println("Resultado registrado.");
                break;
            case 9:
                for (Orden orden : sistema.getOrdenes())
                    System.out.println(orden);
                break;
            case 10:
                for (Cuadrilla c : sistema.getCuadrillas())
                    System.out.println(c);
                break;
            case 11:
                for (Jornada j : sistema.getJornadas())
                    System.out.println(j);
                break;
            case 12:
                System.out.println(sistema.reporteOrdenes());
                System.out.println(sistema.reporteIngresos());
                System.out.println(sistema.reporteJornadas());
                break;
            default:
                System.out.println("Opcion no disponible.");
        }
    }
}
