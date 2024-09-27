package Fridge_Chef.team.common;


import java.util.function.Supplier;

public class UtilTest {

    public static <T> T executionTime(Supplier<T> supplier) {
        final long startTime = System.nanoTime();
        T result = supplier.get();
        final long endTime = System.nanoTime();
        final long duration = (endTime - startTime) / 1_000_000;
        System.out.println("Execution time: " + duration + " ms");
        return result;
    }
    public static void executionTime(Runnable supplier) {
        final long startTime = System.nanoTime();
        supplier.run();
        final long endTime = System.nanoTime();
        final long duration = (endTime - startTime) / 1_000_000;
        System.out.println("Execution time: " + duration + " ms");
    }
}
