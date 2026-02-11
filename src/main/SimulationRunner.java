package main;

import java.util.*;

public class SimulationRunner {

    private static final int NUM_ALLOC = 50;
    private static final int NUM_FREE = 30;

    public void run() {

        long totalAllocTime = 0;
        long totalFreeTime = 0;
        long totalSearchTime = 0;
        int largest = 0;

        for (int ejecucion = 0; ejecucion < 10; ejecucion++) {
            System.out.println("Ejecución: " + ejecucion);


            Disco disk = new Disco();
            disk.initializeDisk();


            FreeSpaceManager manager = new BitmapManager(disk);

            Random random = new Random();
            List<int[]> allocations = new ArrayList<>();

            long allocTime = 0;
            long freeTime = 0;
            long searchTime = 0;

            // 50 ALLOC
            for (int i = 0; i < 50; i++) {

                int size = random.nextInt(32) + 1;

                long start = System.currentTimeMillis();
                int pos = manager.allocate(size);
                long end = System.currentTimeMillis();

                allocTime += (end - start);

                if (pos != -1) {
                    allocations.add(new int[]{pos, size});
                }
            }

            // 30 FREE
            for (int i = 0; i < 30 && !allocations.isEmpty(); i++) {

                int index = random.nextInt(allocations.size());
                int[] alloc = allocations.remove(index);

                long start = System.currentTimeMillis();
                manager.free(alloc[0], alloc[1]);
                long end = System.currentTimeMillis();

                freeTime += (end - start);
            }

            // SEARCH
            long startSearch = System.currentTimeMillis();
            manager.findLargestFreeBlock();
            long endSearch = System.currentTimeMillis();
            largest = manager.findLargestFreeBlock();

            searchTime += (endSearch - startSearch);

            totalAllocTime += allocTime / 50;
            totalFreeTime += freeTime / 30;
            totalSearchTime += searchTime;

        }

        System.out.println("Promedio final alloc: " + (totalAllocTime / 10) + " ms");
        System.out.println("Promedio final free: " + (totalFreeTime / 10) + " ms");
        System.out.println("Promedio final búsqueda: " + (totalSearchTime / 10) + " ms");
        System.out.println("Mayor bloque libre: " + largest);
    }
}