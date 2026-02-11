package main;

import java.io.*;
import java.util.Random;


public class Disco {
    private static final int TOTAL_BLOCKS = 1024;
    private boolean[] blocks;

    //Constructor
    public Disco() {
        blocks = new boolean[TOTAL_BLOCKS];
    }

    // Inicializa el disco con 70% ocupado
    public void initializeDisk() {
        int occupiedBlocks = (int)(TOTAL_BLOCKS * 0.7);
        //Random random = new Random();

        // Primero marcar todo libre
        for (int i = 0; i < occupiedBlocks; i++) {
            blocks[i] = false;
        }
        for (int i = occupiedBlocks; i < TOTAL_BLOCKS; i++) {
            blocks[i] = true;
        }

        /*int count = 0;
        while (count < occupiedBlocks) {
            int index = random.nextInt(TOTAL_BLOCKS);
            if (blocks[index]) {
                blocks[index] = false; // ocupar bloque
                count++;
            }
        }*/
    }

    public boolean[] getBlocks() {
        return blocks;
    }

    public int getTotalBlocks() {
        return TOTAL_BLOCKS;
    }

    // Guardar estado en disco.txt
    public void saveToFile(String path) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < TOTAL_BLOCKS; i++) {
                writer.write(blocks[i] ? "1" : "0");
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Cargar estado desde archivo
    public void loadFromFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            for (int i = 0; i < TOTAL_BLOCKS && i < line.length(); i++) {
                blocks[i] = (line.charAt(i) == '1');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Solo para debug
    public void printDiskStatus() {
        int free = 0;
        int used = 0;

        for (boolean block : blocks) {
            if (block) free++;
            else used++;
        }

        System.out.println("Bloques libres: " + free);
        System.out.println("Bloques ocupados: " + used);
    }
}
