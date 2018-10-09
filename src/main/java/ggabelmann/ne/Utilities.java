package ggabelmann.ne;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Utilities {

    public static float squaredError(final float[] actual, final float[] expected) {
        float result = 0.0f;
        for (int i = 0; i < actual.length; i++) {
            result += (actual[i] - expected[i]) * (actual[i] - expected[i]);
        }
        return result;
    }

    public static BiConsumer<BooleanSupplier, Supplier<String>> conditionalPrinter() {
        return (bool, val) -> {
            if (bool.getAsBoolean()) {
                System.out.println(val.get());
            }
        };
    }

}
