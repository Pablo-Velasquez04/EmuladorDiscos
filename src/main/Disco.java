package main;

import java.io.*;
import java.util.Random;


public class Disco {
    private static final int TOTAL_BLOQUES = 1024; // Total de bloques en el disco (1 KB cada uno)
    private boolean[] bloques; // true = libre, false = ocupado

    //Constructor que inicializa todos los bloques como libres
    public Disco() {
        bloques = new boolean[TOTAL_BLOQUES]; // true = libre, false = ocupado
    }

    //Inicializa el disco con 70% ocupado
    public void initializeDisk() {
        int bloquesOcupados = (int)(TOTAL_BLOQUES * 0.7);  //70% de bloques ocupados
        //Random random = new Random();

        // Primero marcar todo libre
        for (int i = 0; i < bloquesOcupados; i++) {
            bloques[i] = false; //ocupar bloque
        }
        for (int i = bloquesOcupados; i < TOTAL_BLOQUES; i++) {
            bloques[i] = true;  // marcar bloque como libre
        }

        /*int count = 0;
        while (count < bloquesOcupados) {
            int index = random.nextInt(TOTAL_BLOQUES);
            if (bloques[index]) {
                bloques[index] = false; // ocupar bloque
                count++;
            }
        }*/
    }

    // Obtener el arreglo de bloques
    public boolean[] getBloques() {
        return bloques;  // true = libre, false = ocupado
    }

    //Obtener el total de bloques
    public int getBloquesTotales() {
        return TOTAL_BLOQUES; //total de bloques en el disco
    }

    // Guardar estado en disco.txt
    public void guardarArchivo(String path) {
        try {
            File file = new File(path); 
            file.getParentFile().mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));  //Abrir archivo para escritura
            for (int i = 0; i < TOTAL_BLOQUES; i++) {  //escribir estado de cada bloque
                writer.write(bloques[i] ? "1" : "0");  // 1 = libre, 0 = ocupado
            }
            writer.close();  // Cerrar el archivo

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Cargar estado desde archivo
    public void loadFromFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String linea = reader.readLine();
            for (int i = 0; i < TOTAL_BLOQUES && i < linea.length(); i++) {  // cargar estado de cada bloque desde el archivo
                bloques[i] = (linea.charAt(i) == '1');  // 1 = libre, 0 = ocupado
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Solo para debug (No se usa en la simulación, IGNORAR JSJS)
    public void imprimirEstadoDisco() {
        int free = 0;  // contador de bloques libres
        int used = 0;  // contador de bloques ocupados

        // Contar bloques libres y ocupados
        for (boolean block : bloques) {
            if (block) free++;  // bloque libre
            else used++;  // bloque ocupado
        }

        // Imprimir resultados
        System.out.println("Bloques libres: " + free);
        System.out.println("Bloques ocupados: " + used);
    }
}
