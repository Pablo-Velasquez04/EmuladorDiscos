package main;

// Interfaz para la gestión de espacio libre en el disco
public interface FreeSpaceManager {
    int allocate(int size);   // asignar bloques contiguos de tamaño aleatorio (size)

    void free(int start, int size);  // liberar bloques a partir del índice start y tamaño size

    int busquedaBloqueGrande();  // buscar el bloque libre más grande disponible para asignación
}
