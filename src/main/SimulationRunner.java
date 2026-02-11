package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimulationRunner {

    private static final int NUM_ALLOC = 50; // Número de asignaciones a realizar por corrida
    private static final int NUM_LIBERACION = 30; // Número de liberaciones a realizar por corrida
    private long mejorTiempo = Long.MAX_VALUE; // Inicializar con el mayor valor posible con Long.MAX_VALUE nos sirve para comparar tiempos
    private String mejorEstructura = ""; // Para guardar el nombre de la estructura más eficiente

    public void run() {
        try {
            File archivo = new File("EmuladorDiscos/data/resultados.txt");
            archivo.getParentFile().mkdirs();
            new FileWriter(archivo, false).close(); // limpia el archivo
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Probar cada estructura de gestión de espacio libre y guardar resultados en resultados.txt
        probarEstructura("Bitmap");
        probarEstructura("ListaSimple");
        probarEstructura("ListaDoble");

        System.out.println("La estructura más eficiente es: " + mejorEstructura);

        try {
            BufferedWriter writer = new BufferedWriter(
                new FileWriter("EmuladorDiscos/data/resultados.txt", true)
            );
            writer.write("LA ESTRUCTURA MÁS EFICIENTE ES: " + mejorEstructura);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void probarEstructura(String tipo){
            //Crear archivo resultados.txt para guardar resultados de cada corrida
            try {
                File archivo = new File("EmuladorDiscos/data/resultados.txt");
                archivo.getParentFile().mkdirs(); // Crear directorio si no existe
                BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true));

                // Escribir encabezado para el tipo de estructura actual
                writer.write("==========> " + tipo + " <=========");
                writer.newLine();

                // Variables para acumular tiempos por estructura
                long estructuraAlloc = 0;
                long estructuraFree = 0;
                long estructuraSearch = 0;

                // Realizar 5 corridas para cada estructura
                for (int corrida = 1; corrida <= 5; corrida++) {

                    // Variables para acumular tiempos
                    long totalAllocTime = 0;
                    long totalFreeTime = 0;
                    long totalSearchTime = 0;

                    // Realizar 10 ejecuciones por operación para obtener promedio
                    for (int ejecucion = 0; ejecucion < 10; ejecucion++) {

                        Disco disk = new Disco(); // Crear nuevo disco para cada ejecución
                        disk.initializeDisk(); //Inicializar con 70% ocupado
                        disk.guardarArchivo("EmuladorDiscos/data/disco.txt"); //Guardar estado inicial en archivo 
                        disk.loadFromFile("EmuladorDiscos/data/disco.txt"); //Cargar estado desde archivo antes de cada ejecución

                        FreeSpaceManager manager; // Crear manager según tipo

                        //Inicializar manager según tipo elegido
                        if (tipo.equals("Bitmap")) {
                            manager = new BitmapManager(disk);
                        } 
                        else if (tipo.equals("ListaSimple")) {
                            manager = new LinkedManager(disk);
                        } 
                        else {
                            manager = new DoubleManager(disk);
                        }

                        // Simulación de operaciones de asignación, liberación y búsqueda
                        Random random = new Random(); // Para generar tamaños aleatorios de asignación
                        List<int[]> allocaciones = new ArrayList<>(); // Para guardar asignaciones realizadas (pos, size)

                        // Variables para medir tiempos
                        long allocTime = 0;
                        long freeTime = 0;
                        long searchTime = 0;

                        // 50 Allocaciones
                        for (int i = 0; i < NUM_ALLOC; i++) {

                            int size = random.nextInt(32) + 1; // Tamaño aleatorio entre 1 y 32 bloques

                            // Medir tiempo de asignación (considerando tiempo de acceso a disco)
                            long start = System.currentTimeMillis();
                            int pos = manager.allocate(size);
                            long end = System.currentTimeMillis();

                            allocTime += (end - start); // Acumular tiempo de asignación

                            // Guardar asignación realizada para posibles liberaciones futuras
                            if (pos != -1) {
                                allocaciones.add(new int[]{pos, size});
                            }
                        }

                        // 30 Liberaciones
                        for (int i = 0; i < NUM_LIBERACION && !allocaciones.isEmpty(); i++) {

                            int index = random.nextInt(allocaciones.size()); // Elegir aleatoriamente una asignación realizada para liberar
                            int[] alloc = allocaciones.remove(index); // Obtener y remover la asignación de la lista

                            long start = System.currentTimeMillis(); // Medir tiempo de liberación (considerando tiempo de acceso a disco)
                            manager.free(alloc[0], alloc[1]); // Liberar el bloque asignado
                            long end = System.currentTimeMillis(); // Fin medición de tiempo de liberación

                            freeTime += (end - start); // Acumular tiempo de liberación
                        }

                        // Busqueda
                        long startSearch = System.currentTimeMillis(); // Medir tiempo de búsqueda del bloque más grande disponible
                        manager.busquedaBloqueGrande(); // Medir tiempo de búsqueda del bloque más grande disponible
                        long endSearch = System.currentTimeMillis(); // Fin medición de tiempo de búsqueda

                        searchTime += (endSearch - startSearch); // Acumular tiempo de búsqueda 

                        // Acumular tiempos para calcular promedios después de 10 ejecuciones
                        totalAllocTime += allocTime / NUM_ALLOC;
                        totalFreeTime += freeTime / NUM_LIBERACION;
                        totalSearchTime += searchTime;

                    }

                    // Calcular promedios y escribir resultados en archivo y consola
                    long avgAlloc = totalAllocTime / 10;
                    long avgFree = totalFreeTime / 10;
                    long avgSearch = totalSearchTime / 10;

                    //Acumular tiempos para promedios generales por estructura después de 5 corridas (para imprimir al final)
                    estructuraAlloc += avgAlloc;
                    estructuraFree += avgFree;
                    estructuraSearch += avgSearch;


                    //Formatear resultado para impresión y escritura en archivo
                    String resultado = "Corrida " + corrida +
                            " | Allocacion: " + avgAlloc + " ms" +
                            " | Liberacion: " + avgFree + " ms" +
                            " | Busqueda: " + avgSearch + " ms";

                    //Imprimir y escribir resultado de la corrida
                    System.out.println(resultado);
                    writer.write(resultado);
                    writer.newLine();
                }

                //Calcular promedios finales por estructura después de 5 corridas y escribir resultado en archivo y consola
                long finalAlloc = estructuraAlloc / 5;
                long finalFree = estructuraFree / 5;
                long finalSearch = estructuraSearch / 5;

                long eficiencia = finalAlloc + finalFree + finalSearch; // Sumar tiempos para medir eficiencia

                // Actualizar la estructura más eficiente si es el mejor tiempo hasta ahora 
                if (eficiencia < mejorTiempo) {
                    mejorTiempo = eficiencia;
                    mejorEstructura = tipo;
                }

                // Formatear resumen final por estructura
                String resumen = "PROMEDIO FINAL " + tipo +
                        " | Allocacion: " + finalAlloc + " ms" +
                        " | Liberacion: " + finalFree + " ms" +
                        " | Busqueda: " + finalSearch + " ms";

                System.out.println(resumen); // Imprimir en consola el resumen final por estructura
                writer.write(resumen); // Escribir resumen en archivo
                writer.newLine();
                writer.newLine();


                //Separador entre tipos de estructuras en el archivo
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}