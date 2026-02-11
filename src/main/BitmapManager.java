package main;

public class BitmapManager implements FreeSpaceManager {

    private boolean[] blocks; // true = libre, false = ocupado
    private int totalBlocks;

    public BitmapManager(Disco disk) {
        this.blocks = disk.getBlocks(); // usamos el mismo arreglo del disco
        this.totalBlocks = disk.getTotalBlocks();
    }

    @Override
    public int allocate(int size) {
        int consecutive = 0;
        int startIndex = -1;

        for (int i = 0; i < totalBlocks; i++) {

            if (blocks[i]) { // bloque libre
                if (consecutive == 0) {
                    startIndex = i;
                }
                consecutive++;

                if (consecutive == size) {
                    // Reservar los bloques
                    for (int j = startIndex; j < startIndex + size; j++) {
                        blocks[j] = false;

                        try {
                            Thread.sleep(2); // simular acceso a disco
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return startIndex;
                }

            } else {
                consecutive = 0;
            }
        }

        return -1; // No hay espacio suficiente
    }

    @Override
    public void free(int start, int size) {
        for (int i = start; i < start + size && i < totalBlocks; i++) {
            blocks[i] = true;

            try {
                Thread.sleep(2); // simular liberación
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int findLargestFreeBlock() {
        int max = 0;
        int current = 0;

        for (int i = 0; i < totalBlocks; i++) {

            if (blocks[i]) {
                current++;
                try {
                    Thread.sleep(2); // simular escaneo
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                if (current > max) {
                    max = current;
                }
                current = 0;
            }
        }

        // Verificar al final
        if (current > max) {
            max = current;
        }

        return max;
    }
}
