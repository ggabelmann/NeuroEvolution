package ggabelmann.ne.examples;

import ggabelmann.ne.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;

/**
 * Test that 3 hidden ReLU can classify an XOR problem:
 * Category A in the top right quad and bottom left quad.
 * Category B in the top left quad and the bottom right quad.
 * See https://playground.tensorflow.org/ for more info.
 *
 * This class is very similar to BullsEye.
 */
public class Xor {

    private final Random random;
    private final FloatSupplier gaussianSupplier;

    public static void main(final String[] args) {
        new Xor();
    }

    public Xor() {
        this.random = new Random();
        this.gaussianSupplier = () -> (float) random.nextGaussian();

        final InputOutput[] tests = genTests();

        SimpleSimulatedAnnealing population = new SimpleSimulatedAnnealing(
                random,
                new MutateNetwork(random),
                NetworkBuilder.singleHiddenLayer(gaussianSupplier, 2, 3, 2));

        final ToDoubleFunction<Network> calcFitness = (aNetwork) -> {
            return 1.0 - genConfusionMatrix(aNetwork, tests).getAccuracy();
        };
        final Comparator<Network> comparator = Comparator.comparingDouble(calcFitness);

        for (;;) {
            population = population.evolve(comparator, 0.01f);

            final ConfusionMatrix bestConfusionMatrix = genConfusionMatrix(population.bestNetwork(), tests);
            float bestFitness = bestConfusionMatrix.getAccuracy();
            System.out.println(bestConfusionMatrix);
            if (bestFitness >= 1.0f) {
                System.out.println(population.bestNetwork());
                break;
            }
        }
    }

    private InputOutput[] genTests() {
        final List<InputOutput> tests = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            tests.add(new InputOutput(new float[] {random.nextFloat() + 0.1f, random.nextFloat() + 0.1f}, new float[] {1.0f, 0.0f}));
        }
        for (int i = 0; i < 25; i++) {
            tests.add(new InputOutput(new float[] {-random.nextFloat() - 0.1f, -random.nextFloat() - 0.1f}, new float[] {1.0f, 0.0f}));
        }
        for (int i = 0; i < 25; i++) {
            tests.add(new InputOutput(new float[] {-random.nextFloat() - 0.1f, random.nextFloat() + 0.1f}, new float[] {0.0f, 1.0f}));
        }
        for (int i = 0; i < 25; i++) {
            tests.add(new InputOutput(new float[] {random.nextFloat() + 0.1f, -random.nextFloat() - 0.1f}, new float[] {0.0f, 1.0f}));
        }
        return tests.stream().toArray(InputOutput[]::new);
    }

    public ConfusionMatrix genConfusionMatrix(final Network network, final InputOutput[] tests) {
        ConfusionMatrix internal = new ConfusionMatrix("catA", "catB");

        for (int i = 0; i < tests.length; i++) {
            final float[] result = network.apply(tests[i].input);
            internal = internal.add(new TwoCategories(result), new TwoCategories(tests[i].expectedOutput));
        }

        return internal;
    }

}
