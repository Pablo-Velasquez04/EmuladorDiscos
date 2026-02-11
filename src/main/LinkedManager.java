package main;

public class LinkedManager implements FreeSpaceManager {

    private class Node {
        int start; // índice del bloque inicial
        int size;  // cantidad de bloques libres consecutivos
        Node next; // referencia al siguiente nodo

        // Constructor para crear un nuevo nodo de bloque libre
        Node(int start, int size) {
            this.start = start;  // índice del bloque inicial
            this.size = size;  //cantidad de bloques libres consecutivos
            this.next = null;  // referencia al siguiente nodo, inicialmente null por que es el último nodo de la lista 
        }
    }

    private Node head; //cabeza de la lista de bloques libres

    //Constructor que inicializa la lista de bloques libres a partir del disco
    public LinkedManager(Disco disk) {

        boolean[] blocks = disk.getBloques(); // true = libre, false = ocupado
        int total = disk.getBloquesTotales(); // total de bloques en el disco

        int i = 0;

        // Recorrer el arreglo de bloques del disco para construir la lista de bloques libres
        while (i < total) {

            if (blocks[i]) { //bloque libre
                int start = i; //índice del bloque inicial
                int count = 0;  // contador para contar la cantidad de bloques libres consecutivos

                // Contar bloques libres consecutivos
                while (i < total && blocks[i]) {
                    count++;  // incrementar contador por cada bloque libre consecutivo
                    i++;  //avanzar al siguiente bloque
                }

                //Agregar un nuevo nodo a la lista de bloques libres con el índice inicial y el tamaño del bloque libre encontrado
                addNode(start, count);
            } else {
                i++; //avanzar al siguiente bloque porque está ocupado
            }
        }
    }

    // Método para agregar un nuevo nodo a la lista de bloques libres al final de la lista para mantener el orden de los bloques libres en el disco y facilitar la asignación de bloques contiguos para futuras asignaciones de bloques.
    private void addNode(int start, int size) {
        Node newNode = new Node(start, size);

        if (head == null) {
            head = newNode;
            return;
        }

        Node temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = newNode;
    }

    @Override
    public int allocate(int size) { // asignar bloques contiguos de tamaño aleatorio (size)

        Node current = head;
        Node previous = null;

        while (current != null) { //recorrer la lista de bloques libres para encontrar un bloque libre contiguo de tamaño suficiente para asignar el bloque solicitado y medir el tiempo de acceso a cada bloque libre para simular el tiempo de búsqueda en el disco

            if (current.size >= size) {  // si se encuentra un bloque libre contiguo de tamaño suficiente para asignar el bloque solicitado, se reserva el bloque actualizando el índice de inicio y el tamaño del bloque libre en la lista de bloques libres y se mide el tiempo de acceso a los bloques asignados para simular el tiempo de asignación en el disco y se retorna el índice de inicio del bloque asignado para que pueda ser utilizado por el programa que solicitó la asignación de bloques.

                int allocatedStart = current.start;  //índice de inicio del bloque asignado

                try {
                    Thread.sleep(2L * size);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                current.start += size;
                current.size -= size;

                //si el bloque libre actual se ha agotado completamente después de la asignación, se elimina el nodo correspondiente de la lista de bloques libres para mantener la lista de bloques libres actualizada y optimizada para futuras asignaciones y liberaciones de bloques.
                if (current.size == 0) {
                    if (previous == null) {
                        head = current.next;
                    } else {
                        previous.next = current.next;
                    }
                }

                return allocatedStart; // retornar índice de inicio del bloque asignado
            }

            previous = current;
            current = current.next;
        }

        return -1; //si no se encuentra un bloque libre contiguo de tamaño suficiente para asignar el bloque solicitado, se retorna -1 para indicar que la asignación ha fallado por falta de espacio suficiente en el disco.
    }

    @Override
    public void free(int start, int size) {  // liberar bloques a partir del índice start y tamaño size

        try {
            Thread.sleep(2L * size);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Node newNode = new Node(start, size);

        // Insertar el nuevo nodo de bloque libre a liberar en la lista de bloques libres manteniendo el orden de los bloques libres en el disco para facilitar la asignación de bloques contiguos para futuras asignaciones de bloques y medir el tiempo de acceso a los bloques liberados para simular el tiempo de liberación en el disco.
        if (head == null || start < head.start) {
            newNode.next = head;
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null && current.next.start < start) {  // recorrer la lista de bloques libres para encontrar la posición correcta para insertar el nuevo nodo de bloque libre a liberar manteniendo el orden de los bloques libres en el disco para facilitar la asignación de bloques contiguos para futuras asignaciones de bloques y medir el tiempo de acceso a los bloques liberados para simular el tiempo de liberación en el disco.
                current = current.next;
            }

            newNode.next = current.next;
            current.next = newNode;
        }

        merge(); //fusionar nodos adyacentes después de insertar el nuevo nodo de bloque libre a liberar para mantener la lista de bloques libres actualizada y optimizada para futuras asignaciones y liberaciones de bloques.
    }

    //Método para fusionar nodos adyacentes en la lista de bloques libres después de liberar un bloque nuevo para mantener la lista de bloques libres actualizada y optimizada para futuras asignaciones y liberaciones de bloques.
    private void merge() {

        Node current = head;

        while (current != null && current.next != null) {   // recorrer la lista de bloques libres para fusionar nodos adyacentes que representen bloques libres contiguos después de insertar un nuevo nodo de bloque libre a liberar para mantener la lista de bloques libres actualizada y optimizada para futuras asignaciones y liberaciones de bloques y medir el tiempo de acceso a los bloques fusionados para simular el tiempo de fusión en el disco.

            if (current.start + current.size == current.next.start) {  // si el bloque libre representado por el nodo actual es adyacente al bloque libre representado por el siguiente nodo, se fusionan los dos nodos actualizando el tamaño del bloque libre en el nodo actual y eliminando el siguiente nodo de la lista de bloques libres para mantener la lista de bloques libres actualizada y optimizada para futuras asignaciones y liberaciones de bloques y medir el tiempo de acceso a los bloques fusionados para simular el tiempo de fusión en el disco.
                current.size += current.next.size;
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }
    }

    @Override
    public int busquedaBloqueGrande() {  //buscar el bloque libre más grande disponible para asignación

        int max = 0;
        Node current = head;

        //recorrer la lista de bloques libres para encontrar el bloque libre más grande disponible para asignación y medir el tiempo de acceso a cada bloque libre para simular el tiempo de búsqueda en el disco
        while (current != null) {

            try {
                Thread.sleep(2L * current.size);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //si el bloque libre representado por el nodo actual es más grande que el bloque libre más grande encontrado hasta ahora, se actualiza el bloque libre más grande encontrado para asignación y se continúa recorriendo la lista de bloques libres para encontrar el bloque libre más grande disponible para asignación y medir el tiempo de acceso a cada bloque libre para simular el tiempo de búsqueda en el disco.
            if (current.size > max) {
                max = current.size;
            }

            current = current.next; // avanzar al siguiente nodo de la lista de bloques libres para continuar buscando el bloque libre más grande disponible para asignación y medir el tiempo de acceso a cada bloque libre para simular el tiempo de búsqueda en el disco.
        }

        return max;  //retornar el tamaño del bloque libre más grande encontrado para asignación
    }
}
