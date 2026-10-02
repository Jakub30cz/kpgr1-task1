package kpgr1.fill;

/** Own sorting helper (no library sort), used for scan-line intersections. */
public final class IntSort {
    private IntSort() {}

    /** Sorts the first {@code count} elements of {@code values} ascending, in place. */
    public static void insertionSort(int[] values, int count) {
        for (int i = 1; i < count; i++) {
            int v = values[i];
            int j = i - 1;
            while (j >= 0 && values[j] > v) {
                values[j + 1] = values[j];
                j--;
            }
            values[j + 1] = v;
        }
    }
}
