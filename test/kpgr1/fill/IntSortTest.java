package kpgr1.fill;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;

class IntSortTest {
    private static void check(int[] in) {
        int[] expected = in.clone();
        Arrays.sort(expected);
        int[] actual = in.clone();
        IntSort.insertionSort(actual, actual.length);
        assertArrayEquals(expected, actual);
    }

    @Test void empty() { check(new int[0]); }

    @Test void single() { check(new int[] {5}); }

    @Test void sorted() { check(new int[] {1, 2, 3, 4}); }

    @Test void reversed() { check(new int[] {9, 7, 5, 3, 1}); }

    @Test void duplicates() { check(new int[] {3, 1, 3, 1, 2, 2}); }

    @Test void random() {
        Random r = new Random(42);
        for (int n = 0; n < 50; n++) {
            int[] a = new int[r.nextInt(40)];
            for (int i = 0; i < a.length; i++) a[i] = r.nextInt(100) - 50;
            check(a);
        }
    }

    @Test void sortsOnlyFirstCount() {
        int[] a = {3, 2, 1, 0};
        IntSort.insertionSort(a, 3);
        assertArrayEquals(new int[] {1, 2, 3, 0}, a);
    }
}
