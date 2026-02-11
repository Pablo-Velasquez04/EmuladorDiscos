package main;

public interface FreeSpaceManager {
    int allocate(int size);

    void free(int start, int size);

    int findLargestFreeBlock();
}
