/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.exp3_s7_cristian_olivares;

/**
 *
 * @author Cristian Olivares Sandia
 */

import java.util.ArrayList;
import java.util.Scanner;

public class Exp3_S7_Cristian_Olivares {
    //Paso 1: Declaración de variables
    // Constantes para estados de asientos (reservado, disponible, vendido)
    private static final String ASIENTO_DISPONIBLE = "[ ]";
    private static final String ASIENTO_RESERVADO = "[R]";
    private static final String ASIENTO_VENDIDO = "[X]";
    
    // Variables estáticas para estadísticas globales
    static int totalVendidas = 0;
    static double ingresosTotales = 0;
    static int reservasTotales = 0;
    static int reservasConvertidas = 0;
    static int bebidasRegaladas = 0;
    static long ultimoNumeroTransaccion = 1;

    // Precios de las zonas
    static final double PRECIO_VIP = 20000;
    static final double PRECIO_PLATEA = 15000;
    static final double PRECIO_GENERAL = 10000;

    // Variables de instancia
    static String[][] asientos = new String[3][10];
    static ArrayList<Entrada> ventas = new ArrayList<>();
    static final String nombreTeatro = "Teatro Moro";
    static final int TIEMPO_RESERVA_MINUTOS = 30;

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        inicializarSistema();
        mostrarMenuPrincipal();
    }

    // Paso 2: Menú del programa del Teatro
    private static void inicializarSistema() {
        for (int i = 0; i < asientos.length; i++) {
            for (int j = 0; j < asientos[i].length; j++) {
                asientos[i][j] = ASIENTO_DISPONIBLE;
            }
        }
        System.out.println("Bienvenidos al Teatro Moro");
    }

    // 1: Menú principal interactivo
    private static void mostrarMenuPrincipal() {
        int opcion;
        do {
            liberarReservasExpiradas();
            System.out.println("\n==============================");
            System.out.println(" TEATRO MORO - MENU");
            System.out.println("==============================");
            System.out.println("1. Reservar entradas"); // reserva de entradas
            System.out.println("2. Comprar entradas"); // compra directa de entradas
            System.out.println("3. Convertir reserva a compra"); //comprar boleto reservado
            System.out.println("4. Modificar una venta"); // modificar datos
            System.out.println("5. Imprimir boleta"); // imprimir la boleta con datos
            System.out.println("6. Mostrar estadisticas"); // mostrar estadisticas de ventas
            System.out.println("7. Ver ofertas especiales"); // mostrar ofertas disponibles del teatro
            System.out.println("8. Salir");
            System.out.println("==============================");
            System.out.print("Seleccione una opcion: ");

            opcion = leerEntero();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> {
                    System.out.println("[DEBUG] Menú: Reservar entradas");
                    realizarReserva();
                }
                case 2 -> {
                    System.out.println("[DEBUG] Menú: Comprar entradas");
                    realizarCompra();
                }
                case 3 -> convertirReserva();
                case 4 -> modificarVenta();
                case 5 -> imprimirBoleta();
                case 6 -> mostrarEstadisticas();
                case 7 -> mostrarOfertas();
                case 8 -> System.out.println("Gracias por usar el sistema del Teatro Moro.");
                default -> System.out.println("Opcion no valida. Intente de nuevo.");
            }
        } while (opcion != 8);
    }

    //2: Ofertas disponibles
    private static void mostrarOfertas() {
        System.out.println("\n=== OFERTAS ESPECIALES ===");
        System.out.println("1. Descuento del 10% para estudiantes (menores de 25 años)");
        System.out.println("2. Descuento del 15% para personas de la tercera edad (65+ años)");
        System.out.println("3. Promocion especial: Por la compra de 2 entradas");
        System.out.println("   - 2 bebidas gratis");
        System.out.println("   - Aplicable a todas las zonas (VIP, Platea, General)");
        System.out.println("\nEstas ofertas se aplican automáticamente al realizar tu compra.");
    }

    
    //Paso 3: Manejo de las entradas
    // 1: Realizar una reserva
    

    private static void realizarReserva() {
        boolean continuarReservando = true; // Variable para controlar si el usuario quiere reservar otra entrada
        while (continuarReservando) {
            System.out.println("\n--- RESERVAR ENTRADAS ---");
            mostrarPlanoAsientosConPrecios();
            
            System.out.print("Ingrese el asiento a reservar (Ej: A1 VIP): ");
            String input = scanner.nextLine().toUpperCase().trim();
            
            if (input.isEmpty()) {
                System.out.println("No ingresaste nada. Ejemplo: A1 VIP");
                continue; // Volver al inicio del bucle
            }
            
            String[] partes = input.split(" ");
            if (partes.length < 2) {
                System.out.println("Error: Debes incluir la ZONA. Ejemplo: A1 VIP");
                System.out.println("Zonas válidas: VIP, PLATEA, GENERAL");
                continue; // Volver al inicio del bucle
            }
            
            String asiento = partes[0].trim();
            String tipoZona = partes[1].trim();
            
            if (!validarZona(tipoZona)) {
                System.out.println("Zona no válida. Use VIP, Platea o General.");
                continue; // Volver al inicio del bucle
            }
            
            double precioBase = obtenerPrecioZona(tipoZona);
            
            if (!validarFormatoAsiento(asiento)) {
                System.out.println("Error: Formato de asiento incorrecto.");
                continue; // Volver al inicio del bucle
            }
            
            if (!validarAsientoDisponible(asiento)) {
                System.out.println("Error: El asiento no está disponible.");
                continue; // Volver al inicio del bucle
            }
            
            for (Entrada entrada : ventas) {
                if (entrada.asiento.equalsIgnoreCase(asiento) && entrada.estado.equals("Reservado")) {
                    System.out.println("Error: Ya existe una reserva activa para este asiento.");
                    continue; 
                }
            }
            
            int fila = obtenerFila(asiento);
            int columna = obtenerColumna(asiento);
            
            if (fila == -1 || columna == -1) {
                System.out.println("Error: Asiento no válido.");
                return; // Terminar el método
            }

        
            // Preguntar la edad para aplicar el descuento
            System.out.print("Ingrese la edad del comprador: ");
            int edad = leerEnteroPositivo(); // Lee la edad y valida que sea positiva
            
            // Calcular precio con descuento
            double precioFinal = calcularPrecioConDescuento(edad, precioBase);
            
            asientos[fila][columna] = ASIENTO_RESERVADO;
            reservasTotales++;
            long timestamp = System.currentTimeMillis();
            ventas.add(new Entrada(asiento, "Reservado", precioFinal, timestamp, tipoZona, ultimoNumeroTransaccion));
            
            System.out.println("Reserva exitosa: Asiento " + asiento + " (" + tipoZona + ") reservado por " +
                    TIEMPO_RESERVA_MINUTOS + " minutos.");
            System.out.printf("Precio base: $%.2f | Precio final con descuento: $%.2f%n", precioBase, precioFinal);
            if (edad < 25) {
                System.out.println("Descuento aplicado: 10% (Estudiante).");
            } else if (edad >= 65) {
                System.out.println("Descuento aplicado: 15% (Tercera edad).");
            }
            ultimoNumeroTransaccion++;
            
            // Preguntar si el usuario desea reservar otra entrada
            boolean respuestaValida = false;
                    while (!respuestaValida) {
                        System.out.print("Desea reservar otra entrada? (S/N): ");
                        String respuesta = scanner.nextLine().trim().toUpperCase();
                        
                        if (respuesta.isEmpty()) {
                             // Si el usuario no ingresó nada, vuelve a preguntar sin mostrar error
                            continue;
                        }
                        
                        if (respuesta.equals("S")) {
                            respuestaValida = true; // Respuesta válida, continuar reservando
                        } else if (respuesta.equals("N")) {
                            continuarReservando = false; // Salir del bucle principal
                            respuestaValida = true; // Respuesta válida
                        } else {
                            System.out.println("Respuesta no válida. Por favor, ingrese 'S' para Sí o 'N' para No.");
                        }
                    }
        }
    }
    

    //2: Realizar compra
    private static void realizarCompra() {
        System.out.println("\n--- COMPRAR ENTRADAS ---");
        mostrarPlanoAsientosConPrecios();
        System.out.print("¿Desea comprar (1) asientos disponibles o (2) convertir una reserva? [1/2]: ");
        int tipoCompra = leerEntero();
        scanner.nextLine();

        if (tipoCompra == 1) {
            comprarAsientosDisponibles();
        } else if (tipoCompra == 2) {
            convertirReserva();
        } else {
            System.out.println("Opcion no valida.");
        }
    }
    
    //3: Asientos disponibles
    
    private static void comprarAsientosDisponibles() {
        // Validar cantidad con límite de intentos
        int cantidad = 0;
        int intentos = 0;
        final int MAX_INTENTOS = 3;
        
        while (intentos < MAX_INTENTOS) {
            System.out.print("¿Cuántas entradas desea comprar? (1-10): ");
            try {
                cantidad = Integer.parseInt(scanner.nextLine());
            
                if (cantidad >= 1 && cantidad <= 10) {
                    break; // Cantidad válida, salimos del bucle
                } else {
                    System.out.println("Error: La cantidad debe ser entre 1 y 10.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número entero válido.");
            }
            
            intentos++;
            if (intentos < MAX_INTENTOS) {
                System.out.println("Intentos restantes: " + (MAX_INTENTOS - intentos));
            }
        }
        
        // Si se agotaron los intentos
        if (intentos >= MAX_INTENTOS) {
            System.out.println("Demasiados intentos fallidos. Volviendo al menú...");
            return;
        }
        
        // Verificar disponibilidad
        int disponibles = contarAsientosDisponibles();
        if (cantidad > disponibles) {
            System.out.println("Error: Solo hay " + disponibles + " asientos disponibles.");
            return;
        }
        
        // Procesar compra
        long numeroTransaccion = ++ultimoNumeroTransaccion;
        
        if (cantidad >= 2) {
            bebidasRegaladas += 2;
            System.out.println("¡Promoción! 2 bebidas gratis por comprar " + cantidad + " entradas.");
        }
        
        // Comprar cada asiento
        for (int i = 0; i < cantidad; i++) {
            boolean compraExitosa = procesarCompraIndividual(numeroTransaccion, i+1);
            
            if (!compraExitosa) {
                System.out.println("Compra interrumpida. Se cancelaron las entradas restantes.");
                return;
            }
        }
    }
    
    
    // 4: Procesar compra individual
    private static boolean procesarCompraIndividual(long numeroTransaccion, int numeroAsiento) {
        int intentos = 0;
        final int MAX_INTENTOS = 3;
        
        while (intentos < MAX_INTENTOS) {
            System.out.print("\nIngrese asiento #" + numeroAsiento + " (Ej: A1 VIP): ");
            String input = scanner.nextLine().toUpperCase().trim();
            
            // Validaciones básicas
            if (input.isEmpty()) {
                System.out.println("Error: No ingresó datos.");
                intentos++;
                continue;
            }
            
            String[] partes = input.split(" ");
            if (partes.length < 2) {
                System.out.println("Error: Formato incorrecto. Debe ser: LetraNúmero Zona (Ej: A1 VIP)");
                intentos++;
                continue;
            }
            
            String asiento = partes[0];
            String zona = partes[1];
            
            // Validar zona y asiento
            if (!validarZona(zona)) {
                System.out.println("Error: Zona no válida. Use VIP, PLATEA o GENERAL");
                intentos++;
                continue;
            }
            
            if (!validarFormatoAsiento(asiento)) {
                System.out.println("Error: Formato de asiento incorrecto. Ejemplo: A1, B2, C10");
                intentos++;
                continue;
            }
            
            if (!validarAsientoDisponible(asiento)) {
                System.out.println("Error: Asiento no disponible");
                intentos++;
                continue;
            }
            
            // Proceso de compra
            System.out.print("Ingrese edad del comprador: ");
            int edad = leerEnteroPositivo();
            scanner.nextLine(); // Limpiar buffer
            
            double precioBase = obtenerPrecioZona(zona);
            double precioFinal = calcularPrecioConDescuento(edad, precioBase);
            
            // Registrar venta
            int fila = obtenerFila(asiento);
            int columna = obtenerColumna(asiento);
            asientos[fila][columna] = ASIENTO_VENDIDO;
            ventas.add(new Entrada(asiento, "Comprado", precioFinal, 
                    System.currentTimeMillis(), zona, numeroTransaccion));
            
            totalVendidas++;
            ingresosTotales += precioFinal;
            
            System.out.println("\n--- COMPRA EXITOSA ---");
            System.out.println("Asiento: " + asiento + " (" + zona + ")");
            System.out.println("Precio final: $" + precioFinal);
            System.out.println("N° Transacción: " + numeroTransaccion);
            return true; // Compra exitosa
        }
        System.out.println("Demasiados intentos fallidos para este asiento.");
        return false;
    }
      
    // 5: Convertir reserva a compra
    private static void convertirReserva() {
        System.out.println("\n[DEBUG] Iniciando conversión de reserva");
        System.out.println("\n--- CONVERTIR RESERVA A COMPRA ---");
        System.out.print("Ingrese el asiento reservado a comprar (Ej: A1 VIP): ");
        String input = scanner.nextLine().toUpperCase().trim();
        
        if (input.isEmpty()) {
            System.out.println("Error: No ingresaste nada. Ejemplo: A1 VIP");
            return;
        }
        
        String[] partes = input.split(" ");
        if (partes.length < 2) {
            System.out.println("Error: Debes incluir la ZONA. Ejemplo: A1 VIP");
            return;
        }

        String asiento = partes[0];
        String zona = partes[1];
                
        Entrada reserva = buscarReserva(asiento);
        if (reserva == null) {
            int fila = obtenerFila(asiento);
            int columna = obtenerColumna(asiento);
            if (fila == -1 || columna == -1) {
                System.out.println("Error: Asiento " + asiento + " no existe");
                return;
            }
            System.out.println("No se encontró una reserva activa para el asiento " + asiento);
            return;
        }
        
        if (!reserva.zona.equalsIgnoreCase(zona)){
            System.out.println("Error: La zona indicada (" + zona + ") no coincide con la zona de la reserva (" + reserva.zona +")");
            return;
        }
        
        int fila = obtenerFila(asiento);
        char letraFila = asiento.charAt(0);
        if ((letraFila == 'A' && !zona.equalsIgnoreCase("VIP")) ||
                (letraFila == 'B' && !zona.equalsIgnoreCase("PLATEA")) ||
                (letraFila == 'C' && !zona.equalsIgnoreCase("GENERAL"))) {
            System.out.println("Error: El asiento " + asiento + " no pertenece a la zona " + zona);
            return;
        }
        
        reserva.numeroTransaccion = ultimoNumeroTransaccion;

        long tiempoTranscurrido = (System.currentTimeMillis() - reserva.timestamp) / (60 * 1000);
        long segundos = ((System.currentTimeMillis() - reserva.timestamp) % (60 * 1000)) / 1000;
        
        if (tiempoTranscurrido > TIEMPO_RESERVA_MINUTOS) {
            System.out.printf("La reserva ha expirado (%d minutos %d segundos).\n",tiempoTranscurrido, segundos);
            reservasTotales--;
            liberarAsiento(asiento);
            return;
        }

        System.out.print("Ingrese la edad del comprador: ");
        int edad = leerEnteroPositivo(); // para evitar una edad negativa
        scanner.nextLine();

        double precioFinal = calcularPrecioConDescuento(edad, reserva.precio);
        int columna = obtenerColumna(asiento);

        if (fila == -1 || columna == -1) {
            System.out.println("Error: Asiento no válido.");
            return;
        }

        double precioBase = reserva.precio;
        asientos[fila][columna] = ASIENTO_VENDIDO;
        reserva.estado = "Comprado";
        reserva.precio = precioFinal;
        totalVendidas++;
        reservasConvertidas++;
        reservasTotales--;
        ingresosTotales += precioFinal;
        
        System.out.println("\n¡Reserva convertida a compra exitosamente!");
        System.out.println("Asiento: " + reserva.asiento + " (" + reserva.zona + ")");
        System.out.println("Precio base: $" + precioBase);
        if (edad < 25) {
            System.out.println("Descuento (Estudiante <25 años): 10%");
        } else if (edad >= 65) {
            System.out.println("Descuento (Tercera edad 65+ años): 15%");
        }
        System.out.println("Precio final: $" + precioFinal);
        System.out.println("Número de transacción: " + reserva.numeroTransaccion);
        mostrarResumenPostAccion();
        ultimoNumeroTransaccion++;
    }

    // 6: Modificar venta
    
    private static void modificarVenta() {
        System.out.println("\n--- MODIFICAR VENTA ---");
        System.out.print("Ingrese el asiento a modificar (Ej: A1 VIP): ");
        String input = scanner.nextLine().toUpperCase().trim();
        
        if (input.isEmpty()) {
            System.out.println("❌ Error: No ingresaste nada. Ejemplo: A1 VIP");
            return;
        }
        
        String[] partes = input.split(" ");
        if (partes.length < 2) {
            System.out.println("❌ Error: Debes incluir la ZONA. Ejemplo: A1 VIP");
            return;
        }
        
        String asiento = partes[0];
        Entrada entrada = buscarEntrada(asiento);
        if (entrada == null) {
            int fila = obtenerFila(asiento);
            int columna = obtenerColumna(asiento);
            if (fila == -1 || columna == -1) {
                System.out.println("Error: Asiento no existe");
                return;
            }
            System.out.println("No se encontró ninguna venta/reserva para el asiento " + asiento);
            return;
        }
        
        System.out.println("Datos actuales: " + entrada);
        System.out.print("¿Qué desea modificar? (1) Estado, (2) Precio [1/2]: ");
        int opcion = leerEntero();
        scanner.nextLine();
        
        switch (opcion) {
            case 1 -> {
                System.out.print("Nuevo estado (Reservado/Comprado/Cancelado): ");
                String nuevoEstado = scanner.nextLine().trim();
                
                if (!validarEstado(nuevoEstado)) {
                    System.out.println("Estado no válido.");
                    return;
                }
                
                int fila = obtenerFila(asiento);
                int columna = obtenerColumna(asiento);
                
                if (nuevoEstado.equalsIgnoreCase("Cancelado")) {
                    if (confirmarAccion("¿Está seguro de que desea cancelar esta venta/reserva?")) {
                        if (entrada.estado.equalsIgnoreCase("Comprado")) {
                            totalVendidas--;
                            ingresosTotales -= entrada.precio;
                        } else if (entrada.estado.equalsIgnoreCase("Reservado")) {
                        reservasTotales--;
                        }
                        asientos[fila][columna] = ASIENTO_DISPONIBLE;
                        entrada.estado = nuevoEstado;
                        System.out.println("Cancelación exitosa.");
                    } else {
                        System.out.println("Cancelación abortada. La venta/reserva sigue activa.");
                    }
                } else {
                    entrada.estado = nuevoEstado;
                    System.out.println("Estado actualizado correctamente.");
                }
            }
            
            case 2 -> {
                System.out.print("Nuevo precio: ");
                double nuevoPrecio = leerDouble();
                scanner.nextLine();
                entrada.precio = nuevoPrecio;
                System.out.println("Precio actualizado correctamente.");
            }
            default -> System.out.println("Opción no válida.");
        }
    }
    
    
    // 7: Imprimir boleta de compra
    private static void imprimirBoleta() {
        System.out.println("\n[DEBUG] Menú de impresión de boletas");
        System.out.println("\n--- IMPRIMIR BOLETA ---");
        System.out.println("1. Buscar por asiento individual");
        System.out.println("2. Buscar por número de transacción");
        System.out.print("Seleccione opción: ");
        
        int opcion = leerEntero();
        scanner.nextLine();
        
        switch(opcion) {
            case 1 -> {
                System.out.println("[DEBUG] Opción: Boleta individual");
                imprimirBoletaIndividual();
            }
            case 2 -> {
                System.out.println("[DEBUG] Opción: Boletas por transacción");
                imprimirBoletasPorTransaccion();
            }
            default -> System.out.println("Opción no válida");
        }
    }

    // 8: Imprimir boletas multiples
    private static void imprimirBoletasPorTransaccion() {
        System.out.print("Ingrese su número de transacción: ");
        long numTransaccion = leerEntero();
        scanner.nextLine();
        
        if (numTransaccion <= 0) {
            System.out.println("Error: El número de transacción debe ser positivo");
            System.out.println("[DEBUG] Número de transacción inválido: " + numTransaccion);
            return;
        }
        
        System.out.println("\n=== BOLETAS DE LA TRANSACCIÓN " + numTransaccion + " ===");
        boolean encontradas = false;
        double totalTransaccion = 0;
        int contador = 0;
        
        for (Entrada entrada : ventas) {
            if (entrada.numeroTransaccion == numTransaccion) {
                contador++;
                System.out.println("\nBOLETA #" + contador);
                imprimirDetallesBoleta(entrada);
                totalTransaccion += entrada.precio;
                encontradas = true;
            }
        }
        
        if (encontradas) {
            System.out.println("-----------------------------");
            System.out.printf("TOTAL TRANSACCIÓN: $%.2f\n", totalTransaccion);
            System.out.println("=============================");
        } else {
            System.out.println("[DEBUG] No se encontraron boletas para la transacción: " + numTransaccion);
            System.out.println("No se encontraron boletas con ese número de transacción.");
        }
    }


    // 9: Imprimir boleta individual
    private static void imprimirBoletaIndividual() {
        System.out.print("Ingrese el asiento para generar boleta (Ej: A1 VIP): ");
        String input = scanner.nextLine().toUpperCase().trim();

        if (input == null || input.isEmpty()) {
            System.out.println("Error: Debe ingresar un asiento");
            System.out.println("[DEBUG] Input vacío");
            return;
        }
        
        String[] partes = input.split(" ");
        String asiento = partes[0];
        String zona = partes.length > 1 ? partes[1] : "";
   
        
        Entrada entrada = buscarEntrada(asiento);
        
        if (entrada == null || (!zona.isEmpty() && !entrada.zona.equalsIgnoreCase(zona))) {
            System.out.println("Error: No existe boleta para el asiento " + asiento +
                    (zona.isEmpty() ? "" : " en la zona " + zona));
            return;
        }
        imprimirDetallesBoleta(entrada);
    }
    
    // 10: Imprimir detalles de boleta
    private static void imprimirDetallesBoleta(Entrada entrada) {
        if (entrada == null) {
            System.out.println("No se encontró información para el asiento especificado");
            return;
        }

        java.util.Date fechaHora = new java.util.Date(entrada.timestamp);
        java.text.SimpleDateFormat formateador = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        System.out.println("\n=== BOLETA ELECTRÓNICA ===");
        System.out.println("Teatro: " + nombreTeatro);
        System.out.println("N° Transacción: " + entrada.numeroTransaccion);
        System.out.println("Asiento: " + entrada.asiento + " (" + entrada.zona + ")");
        System.out.println("Estado: " + entrada.estado);
        System.out.printf("Precio: $%.2f\n", entrada.precio);
        System.out.println("Fecha y hora de compra: " + formateador.format(fechaHora));
        
        if (entrada.estado.equals("Comprado") && entrada.precio < obtenerPrecioZona(entrada.zona)) {
            System.out.println("(Incluye descuento especial)");
        }
        System.out.println("=== GRACIAS POR SU COMPRA ===");
    }

    // 11: Mostrar estadisticas de ventas
    private static void mostrarEstadisticas() {
        int totalAsientos = asientos.length * asientos[0].length;
        System.out.println("\n=== ESTADÍSTICAS DEL TEATRO ===");
        System.out.println("Total de entradas vendidas: " + totalVendidas);
        System.out.printf("Total de ingresos: $%.2f\n", ingresosTotales);
        System.out.println("Total de reservas activas: " + (reservasTotales - reservasConvertidas));
        System.out.println("Reservas convertidas a ventas: " + reservasConvertidas);
        System.out.println("Bebidas regaladas: " + bebidasRegaladas);
        
        
        int asientosOcupados = 0;
        for (String[] fila : asientos) {
            for (String estado : fila) {
                if (!estado.equals(ASIENTO_DISPONIBLE)) {
                    asientosOcupados++;
                }
            }
        }
        double ocupacion = (double) asientosOcupados / totalAsientos * 100;
        System.out.printf("Porcentaje de ocupación: %.2f%%\n", ocupacion);
    }

    // Paso 4: Funciones auxiliares 
    private static double obtenerPrecioZona(String zona) {
        if (!validarZona(zona)) {
            System.out.println("[DEBUG] Zona no válida: " + zona);
            return 0;
        }
        return switch (zona.toUpperCase()) {
            case "VIP" -> PRECIO_VIP;
            case "PLATEA" -> PRECIO_PLATEA;
            case "GENERAL" -> PRECIO_GENERAL;
            default -> 0;
        };
    }

    // 1: Validar formato de asiento (A1, A2, etc)
    private static boolean validarFormatoAsiento(String asiento) {
        if (asiento == null || !asiento.matches("^[A-C](10|[1-9])$")) {
            System.out.println("\n❌ Error: Formato debe ser LetraNúmero (ej: A1, B10)");
            System.out.println("   - Letras válidas: A, B, C");
            System.out.println("   - Números válidos: 1 al 10");
            return false;
        }
        return true;
    }

    // 2: Validar asiento disponible 
    private static boolean validarAsientoDisponible(String asiento) {
        
        if (!validarInputAsiento(asiento)) {
            System.out.println("[DEBUG] Input de asiento inválido");
            return false;
        }
        int fila = obtenerFila(asiento);
        int columna = obtenerColumna(asiento);
        
        if (fila == -1 || columna == -1 || fila >= asientos.length || columna >= asientos[0].length) {
            System.out.println("Error: Asiento no existe o está fuera de rango. Use formato LetraNúmero (ej: A1-C10)");
            return false;
        }
       
        String estado = asientos[fila][columna];
        System.out.println("[DEBUG] Estado actual: " + estado);
        return estado.equals(ASIENTO_DISPONIBLE);

        }

    // 3: Obtener fila  A-C
    private static int obtenerFila(String asiento) {
        if (asiento == null || asiento.isEmpty()) return -1;
        char fila = asiento.charAt(0);
        return fila >= 'A' && fila <= 'C' ? fila - 'A' : -1;
    }

    // 4: Obtener columna 0-10
    private static int obtenerColumna(String asiento) {
        try {
            int columna = Integer.parseInt(asiento.substring(1)) - 1;
            return columna >= 0 && columna < 10 ? columna : -1;
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número válido.");
            return -1;
        }
    }

    // 5: Calcular precio con descuento (estudiante y tercera edad)
    private static double calcularPrecioConDescuento(int edad, double precioBase) {
        System.out.println("[DEBUG] Aplicando descuento para edad: " + edad);
        
        if (edad < 0 || edad > 120) { // En caso de poner una edad no valida como +120
            System.out.println("Error: Edad inválida. Debe ser entre 0 y 120 años.");
            return precioBase;
        }
        if (edad < 25){
            System.out.println("[DEBUG] Aplicando descuento del 10% (estudiante)");
            return precioBase * 0.90;
        }
        
        if (edad >= 65){
            System.out.println("[DEBUG] Aplicando descuento del 15% (tercera edad)");
            return precioBase * 0.85;
        }
        System.out.println("[DEBUG] Sin descuentos aplicables");
        return precioBase;
    }

    // 6: Buscar entrada
    private static Entrada buscarEntrada(String asiento) {
        for (Entrada entrada : ventas) {
            if (entrada.asiento.equalsIgnoreCase(asiento)) {
                return entrada;
            }
        }
        return null;
    }

    // 7: Buscar reserva
    private static Entrada buscarReserva(String asiento) {
        for (Entrada entrada : ventas) {
            if (entrada.asiento.equalsIgnoreCase(asiento) && entrada.estado.equals("Reservado")) { 
                return entrada;
            }
        }
        return null;
    }

    // 8: Liberar asiento
    private static void liberarAsiento(String asiento) {
        int fila = obtenerFila(asiento);
        int columna = obtenerColumna(asiento);
        
        if (fila != -1 && columna != -1) {
            if (asientos[fila][columna].equals(ASIENTO_RESERVADO)) {
                reservasTotales--;
            }
            asientos[fila][columna] = ASIENTO_DISPONIBLE;
            
            for (Entrada entrada : ventas){
                if (entrada.asiento.equalsIgnoreCase(asiento) && entrada.estado.equals("Reservado")){
                    entrada.estado = "Cancelado";
                    break;
                }
            }

        }
    }

    // 9: Mostrar plano del teatro con precios 
    private static void mostrarPlanoAsientosConPrecios() {
        System.out.println("\n--- PLANO DE ASIENTOS CON PRECIOS ---");
        System.out.println("   1     2     3     4     5     6     7     8     9    10");
        
        for (int i = 0; i < asientos.length; i++) {
            char letraFila = (char) ('A' + i);
            String zona = "";
            double precio = 0;
            
            switch (letraFila) {
                case 'A' -> { zona = "VIP"; precio = PRECIO_VIP; }
                case 'B' -> { zona = "PLATEA"; precio = PRECIO_PLATEA; }
                case 'C' -> { zona = "GENERAL"; precio = PRECIO_GENERAL; }
            }
            
            System.out.printf("%s (%s $%.2f): ", letraFila, zona, precio);
            for (int j = 0; j < asientos[i].length; j++) {
                System.out.print(asientos[i][j] + " ");
            }
            System.out.println();
        }
        
        System.out.println("\nLeyenda:");
        System.out.println(ASIENTO_DISPONIBLE + " Disponible  " + 
                          ASIENTO_RESERVADO + " Reservado  " + 
                          ASIENTO_VENDIDO + " Vendido");
        System.out.printf("Precios: VIP $%.2f | Platea $%.2f | General $%.2f%n", 
                         PRECIO_VIP, PRECIO_PLATEA, PRECIO_GENERAL);
    }

    // 10 : Leer entero/double/positivo - evitar ingreso incorrecto de los datos
    private static int leerEntero() {
        while (!scanner.hasNextInt()) {
            System.out.println("Error: Debe ingresar un número entero.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static double leerDouble() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Error: Debe ingresar un número.");
            scanner.next();
        }
        return scanner.nextDouble();
    }
    
    private static int leerEnteroPositivo() {
        while (true) {
            int numero = leerEntero();
            if (numero >= 0) {
                return numero;
            }
            System.out.println("Error: La edad no puede ser negativa. Ingrese un valor válido:");
        }
    }

    // 11: Validar y procesar asiento
    private static void validarYProcesarAsiento(String input, boolean esReserva, long numTransaccion) {
        System.out.println("[DEBUG] Validando asiento: " + input);
        
        String[] partes = input.split(" ");
        if (partes.length < 2 || partes[1] == null || partes[1].trim().isEmpty()) {
            System.out.println("Formato incorrecto. Debe ser: LetraNumero Zona (Ej: A1 VIP)");
            return;
        }
        
        String asiento = partes[0];
        String tipoZona = partes[1];
        double precioBase = obtenerPrecioZona(tipoZona);
        
        String mensajeError = !validarFormatoAsiento(asiento) ? "Formato de asiento incorrecto" :
                         !validarAsientoDisponible(asiento) ? "El asiento no está disponible" :
                         precioBase == 0 ? "Zona no válida" : null;
        
        if (mensajeError != null) {
            System.out.println("Error: " + mensajeError);
            return;
        }
        
        int fila = obtenerFila(asiento);
        int columna = obtenerColumna(asiento);
        
        System.out.printf("[DEBUG] Asiento: %s, Fila: %d, Columna: %d%n", asiento, fila, columna);
        
        if (fila == -1 || columna == -1) {
            System.out.println("Error: Asiento no válido.");
            return;
        }
        
        if (esReserva) {
            asientos[fila][columna] = ASIENTO_RESERVADO;
            reservasTotales++;
            ventas.add(new Entrada(asiento, "Reservado", precioBase, System.currentTimeMillis(), tipoZona, numTransaccion));
            System.out.println("Reserva exitosa: Asiento " + asiento);
        } else {
            asientos[fila][columna] = ASIENTO_VENDIDO;
            totalVendidas++;
            ingresosTotales += precioBase;
            ventas.add(new Entrada(asiento, "Comprado", precioBase, System.currentTimeMillis(), tipoZona, numTransaccion));
            System.out.println("Compra exitosa: Asiento " + asiento);
        }
        
        System.out.println("[DEBUG] Estado actual del asiento: " + asientos[fila][columna]);
    }

    // 12: Mostrar disponibilidad de asiento (Disponible-Reservado-Vendido)
    private static void mostrarDisponibilidad() {
        System.out.println("\n--- DISPONIBILIDAD DE ASIENTOS ---");
        for (String[] fila : asientos) {
            for (String asiento : fila) {
                System.out.print(asiento + " ");
            }
            System.out.println();
        }
        System.out.println("Leyenda: " + 
                          ASIENTO_DISPONIBLE + " Disponible, " + 
                          ASIENTO_RESERVADO + " Reservado, " + 
                          ASIENTO_VENDIDO + " Vendido");
    }
    
    // 13: Validar estado de asiento
    private static boolean validarEstado(String estado) {
        if (estado == null) return false;
        
        String estadoUpper = estado.trim().toUpperCase();
        return estadoUpper.equals("RESERVADO") ||
                estadoUpper.equals("COMPRADO") ||
                estadoUpper.equals("CANCELADO");
    }
    
    // 14: Validar zona
    //// Alternativa: if(zona.equalsIgnoreCase("VIP") || ...)
    private static boolean validarZona(String zona) {
        if (zona == null) return false;
        return switch(zona.toUpperCase()) {
            case "VIP", "PLATEA", "GENERAL" -> true;
            default -> false;
        };
    }
    
    // 15: Validar imput asiento
    private static boolean validarInputAsiento(String input) {
    if (input == null || input.trim().isEmpty()) {
        System.out.println("[DEBUG] Input vacío");
        return false;
    }
    return true;
    }
    
    // 16: Contador de asientos disponibles
    private static int contarAsientosDisponibles() {
        int count = 0;
        for (String[] fila : asientos) {
            for (String estado : fila) {
                if (estado.equals(ASIENTO_DISPONIBLE)) {
                    count++;
                }
            }
        }
        System.out.println("[DEBUG] Asientos disponibles: " + count); // Opcional: mensaje debug
        return count;
    }
    
    // 17: Liberar las reservas expiradas 
    private static void liberarReservasExpiradas() {
        long ahora = System.currentTimeMillis();
        ArrayList<Entrada> reservasParaCancelar = new ArrayList<>();
        
        for (Entrada entrada : ventas) {
            if (entrada.estado.equals("Reservado")) {
                long minutosPasados = (ahora - entrada.timestamp) / (60 * 1000);
                if (minutosPasados >= TIEMPO_RESERVA_MINUTOS) {
                reservasParaCancelar.add(entrada);
            }
        }
    }
        for (Entrada reserva : reservasParaCancelar) {
            liberarAsiento(reserva.asiento);
            
            System.out.println("[INFO] Reserva expirada liberada: " + reserva.asiento);
        }
    }
    
    // 18: Mostrar menu post acción 
    private static void mostrarResumenPostAccion() {
        System.out.println("\n=== RESUMEN POST-ACCIÓN ===");
        int disponibles = contarAsientosDisponibles();
        System.out.println("Asientos disponibles: " + disponibles);
        System.out.println("Ofertas activas:");
        System.out.println("- 10% descuento estudiantes (<25 años)");
        System.out.println("- 15% descuento tercera edad (65+ años)");
        System.out.println("- Compra 2 o más entradas = 2 bebidas gratis");
    }
    
    //19: Confirmar accion eliminar reserva
    
    private static boolean confirmarAccion(String mensaje) {
        System.out.print(mensaje + " (S/N): ");
        String respuesta = scanner.nextLine().trim().toUpperCase();
        return respuesta.equals("S");
    }

    // 20 : Reintentar en caso de que se ingresen mal los datos
    private static boolean preguntarReintentar(String mensajeError) {
        System.out.println("\n❌ " + mensajeError);
        System.out.print("¿Deseas intentarlo de nuevo? (S/N): ");
        String respuesta = scanner.nextLine().trim().toUpperCase();
        return respuesta.equals("S"); // Retorna true si el usuario quiere reintentar
    }

    // Paso 5: Crear entradas 
    static class Entrada {
        String asiento;
        String estado;
        double precio;
        long timestamp;
        String zona;
        long numeroTransaccion;

        Entrada(String asiento, String estado, double precio, long timestamp, String zona, long numeroTransaccion) {
            if (asiento == null || estado == null || zona == null) {
                throw new IllegalArgumentException("Parámetros no pueden ser nulos");
            }
            if (precio < 0) {
                throw new IllegalArgumentException("Precio no puede ser negativo");
            }
            this.asiento = asiento;
            this.estado = estado;
            this.precio = precio;
            this.timestamp = timestamp;
            this.zona = zona;
            this.numeroTransaccion = numeroTransaccion;
        }

        @Override
        public String toString() {
            return String.format("Asiento: %s (%s), Estado: %s, Precio: $%.2f, N° Transacción: %d", 
               asiento, zona, estado, precio, numeroTransaccion);
        }
    }
}