package main;

// Implementación de la gestión de espacio libre utilizando un mapa de bits (bitmap)
public class BitmapManager implements FreeSpaceManager {

    private boolean[] bloques; // true = libre, false = ocupado
    private int bloquesTotales; // total de bloques en el disco

    // Constructor que utiliza el arreglo de bloques del disco
    public BitmapManager(Disco disk) {
        this.bloques = disk.getBloques(); // usamos el mismo arreglo del disco
        this.bloquesTotales = disk.getBloquesTotales();
    }

    @Override
    public int allocate(int size) {  // asignar bloques contiguos de tamaño aleatorio (size)
        int consecutive = 0; // contador de bloques libres consecutivos
        int startIndex = -1; // índice de inicio del bloque libre encontrado

        //Buscar bloques libres contiguos
        for (int i = 0; i < bloquesTotales; i++) {

            if (bloques[i]) { //bloque libre
                if (consecutive == 0) { //primer bloque libre encontrado
                    startIndex = i; // guardar índice de inicio
                }
                consecutive++;

                // Si se encuentran suficientes bloques libres contiguos
                if (consecutive == size) {
                    // Reservar los bloques
                    for (int j = startIndex; j < startIndex + size; j++) {
                        bloques[j] = false; // marcar como ocupado

                        // Simular tiempo de acceso a disco
                        try {
                            Thread.sleep(1); // simular acceso a disco por bloque
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return startIndex; //retornar índice de inicio de la asignación
                }

            } else {
                consecutive = 0; //resetear contador si se encuentra un bloque ocupado
            }
        }

        return -1; //No hay espacio suficiente
    }

    @Override
    public void free(int start, int size) {  //liberar bloques a partir del índice start y tamaño size
        for (int i = start; i < start + size && i < bloquesTotales; i++) { // marcar bloques como libres
            bloques[i] = true; // marcar como libre 

            try {
                Thread.sleep(2); // simular liberación
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int busquedaBloqueGrande() {  // buscar el bloque libre más grande disponible para asignación
        int max = 0;  //tamaño del bloque libre más grande encontrado
        int actual = 0;  //tamaño del bloque libre actual

        //Recorrer el arreglo de bloques para encontrar el bloque libre más grande
        for (int i = 0; i < bloquesTotales; i++) {

            if (bloques[i]) {  // bloque libre 
                actual++; //incrementar tamaño del bloque libre actual
                try {
                    Thread.sleep(1); // simular escaneo
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {  //bloque ocupado para verificar si el bloque libre actual es el más grande
                if (actual > max) {  //actualizar máximo si es necesario
                    max = actual;  // actualizar tamaño del bloque libre más grande
                }
                actual = 0;   // resetear tamaño del bloque libre actual
            }
        }

        //Verificar al final
        if (actual > max) {
            max = actual;  // actualizar tamaño del bloque libre más grande
        }

        return max;  //retornar tamaño del bloque libre más grande encontrado
    }
}
