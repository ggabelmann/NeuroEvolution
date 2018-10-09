package ggabelmann.ne;

import java.util.Random;
import java.util.function.DoubleSupplier;

/**
 * A way of supplying doubles according to a given distribution.
 * For example, 0.1:2, 10.0:3, means the 10.0 value will be chosen 3/5 times.
 */
public class DiscreteDistribution implements DoubleSupplier {

    private final Internal[] distribution;
    private final Random random;
    private final int total;

    public DiscreteDistribution(final Random random, final Internal... distribution) {
        this.random = random;
        this.distribution = distribution;

        int temp = 0;
        for (int i = 0; i < distribution.length; i++) {
            temp += distribution[i].probability;
        }
        this.total = temp;
    }


    @Override
    public double getAsDouble() {
        int chosen = random.nextInt(total);
        for (int i = 0; i < distribution.length; i++) {
            chosen -= distribution[i].probability;
            if (chosen < 0) {
                // if the first element in the distribution is say: 0.1, 100
                // then choosing 99 should return it, but not choosing 100, because Random starts with 0.
                return distribution[i].value;
            }
        }

        throw new IllegalStateException();
    }

    public static class Internal {

        public final double value;
        public final int probability;

        public Internal(final double value, final int probability) {
            this.value = value;
            this.probability = probability;
        }
    }

}
