package main;

public class DoubleManager implements FreeSpaceManager {

    // Nodo de la lista doblemente enlazada de bloques libres
    private class Node {
        int start;
        int size;
        Node next;
        Node prev;

        // Constructor del nodo 
        Node(int start, int size) {
            this.start = start;
            this.size = size;
            this.next = null;
            this.prev = null;
        }
    }

    private Node head;  //cabeza de la lista de bloques libres

    //Constructor que utiliza el arreglo de bloques del disco
    public DoubleManager(Disco disk) {

        boolean[] blocks = disk.getBloques();  // true = libre, false = ocupado
        int total = disk.getBloquesTotales();  // total de bloques en el disco

        int i = 0;  //índice para recorrer el arreglo de bloques

        //Recorrer el arreglo de bloques para construir la lista de bloques libres
        while (i < total) {

            if (blocks[i]) { // bloque libre
                int start = i;  //índice del bloque inicial
                int count = 0;  // contador para contar la cantidad de bloques libres consecutivos

                //Contar bloques libres consecutivos
                while (i < total && blocks[i]) {
                    count++;  //incrementar contador por cada bloque libre consecutivo
                    i++;  // avanzar al siguiente bloque
                }

                addNode(start, count);  // agregar un nuevo nodo a la lista de bloques libres con el índice inicial y el tamaño del bloque libre encontrado
            } else {
                i++;  // avanzar al siguiente bloque porque está ocupado
            }
        }
    }

    // Método para agregar un nuevo nodo a la lista de bloques libres
    private void addNode(int start, int size) {
        Node newNode = new Node(start, size); // crear un nuevo nodo con el índice de inicio y el tamaño del bloque libre

        if (head == null) {
            head = newNode;
            return;
        }

        Node temp = head;  // nodo temporal para recorrer la lista de bloques libres
        while (temp.next != null) {  // recorrer la lista hasta el final para agregar el nuevo nodo al final de la lista
            temp = temp.next;
        }

        temp.next = newNode;
        newNode.prev = temp;
    }

    @Override
    public int allocate(int size) {  // asignar bloques contiguos de tamaño aleatorio (size)

        Node current = head;

        // Recorrer la lista de bloques libres para encontrar un bloque que tenga suficiente espacio para asignar el bloque solicitado
        while (current != null) {

            //Verificar si el bloque actual tiene suficiente espacio para asignar el bloque solicitado
            if (current.size >= size) {

                int allocatedStart = current.start;

                try {
                    Thread.sleep(2L * size);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                current.start += size;
                current.size -= size;

                if (current.size == 0) {
                    removeNode(current);
                }

                return allocatedStart; //retornar índice de inicio de la asignación
            }

            current = current.next; //avanzar al siguiente nodo si el bloque actual no tiene suficiente espacio para asignar el bloque solicitado
        }

        return -1;
    }

    @Override
    public void free(int start, int size) {  //liberar bloques a partir del índice start y tamaño size

        try {
            Thread.sleep(2L * size);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Node newNode = new Node(start, size); // crear un nuevo nodo con el índice de inicio y el tamaño del bloque libre a liberar

        if (head == null) {   // si la lista de bloques libres está vacía, el nuevo nodo se convierte en la cabeza de la lista
            head = newNode;
            return;
        }

        Node current = head;  // nodo temporal para recorrer la lista de bloques libres y encontrar la posición correcta para insertar el nuevo nodo de bloque libre a liberar

        //Insertar ordenado por start
        while (current != null && current.start < start) {
            current = current.next;
        }

        if (current == head) { //si el nuevo nodo debe ser insertado al inicio de la lista de bloques libres, se actualiza la cabeza de la lista para que apunte al nuevo nodo y se actualizan las referencias del nuevo nodo para que apunten al nodo anterior (que ahora es el nuevo nodo) y al nodo siguiente (que era el nodo anterior a la cabeza)
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        } else if (current == null) { // si el nuevo nodo debe ser insertado al final de la lista de bloques libres, se recorre la lista hasta el final para agregar el nuevo nodo al final de la lista y se actualizan las referencias del nuevo nodo para que apunten al nodo anterior (que ahora es el nuevo nodo) y al nodo siguiente (que es null porque es el final de la lista)
            Node tail = head;
            while (tail.next != null) {  //recorrer la lista hasta el final para encontrar el último nodo
                tail = tail.next;
            }
            tail.next = newNode;
            newNode.prev = tail;
        } else {  //si el nuevo nodo debe ser insertado en medio de la lista de bloques libres, se actualizan las referencias del nuevo nodo para que apunten al nodo anterior (que ahora es el nuevo nodo) y al nodo siguiente (que es el nodo actual) y se actualizan las referencias del nodo anterior para que apunten al nuevo nodo y las referencias del nodo siguiente para que apunten al nuevo nodo
            Node previous = current.prev;
            previous.next = newNode;
            newNode.prev = previous;

            newNode.next = current;
            current.prev = newNode;
        }

        merge(newNode);  // fusionar nodos adyacentes después de insertar el nuevo nodo de bloque libre a liberar para mantener la lista de bloques libres actualizada y optimizada para futuras asignaciones y liberaciones de bloques.
    }

    // Método para fusionar nodos adyacentes en la lista de bloques libres después de liberar un bloque nuevo
    private void merge(Node node) {

        // Merge hacia atrás
        if (node.prev != null &&
            node.prev.start + node.prev.size == node.start) {

            node.prev.size += node.size;
            removeNode(node);
            node = node.prev;
        }

        // Merge hacia adelante
        if (node.next != null &&
            node.start + node.size == node.next.start) {

            node.size += node.next.size;
            removeNode(node.next);
        }
    }

    // Método para eliminar un nodo de la lista de bloques libres después de fusionar nodos adyacentes para mantener la lista de bloques libres actualizada y optimizada para futuras asignaciones y liberaciones de bloques.
    private void removeNode(Node node) {

        if (node.prev == null) { //si el nodo a eliminar es la cabeza de la lista de bloques libres, se actualiza la cabeza de la lista para que apunte al siguiente nodo y se actualizan las referencias del siguiente nodo para que apunten a null porque ahora es la nueva cabeza de la lista
            head = node.next;
        } else {
            node.prev.next = node.next;  //si el nodo a eliminar no es la cabeza de la lista de bloques libres, se actualizan las referencias del nodo anterior para que apunten al nodo siguiente del nodo a eliminar
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        }
    }

    @Override
    public int busquedaBloqueGrande() {  // buscar el bloque libre más grande disponible para asignación

        int max = 0;
        Node current = head;

        while (current != null) {  //recorrer la lista de bloques libres para encontrar el bloque libre más grande disponible para asignación y medir el tiempo de acceso a cada bloque libre para simular el tiempo de búsqueda en el disco

            try {
                Thread.sleep(2L * current.size);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (current.size > max) {
                max = current.size;
            }

            current = current.next;
        }

        return max;  //retornar el tamaño del bloque libre más grande encontrado para asignación
    }
}
