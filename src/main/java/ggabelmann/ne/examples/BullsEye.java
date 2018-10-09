package ggabelmann.ne.examples;

import ggabelmann.ne.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;

/**
 * Test that 3 hidden ReLU can classify a "bull's eye" problem.
 * See https://playground.tensorflow.org/ for more info.
 *
 * This class is very similar to Xor.
 */
public class BullsEye {

    private final Random random;
    private final FloatSupplier gaussianSupplier;

    public static void main(final String[] args) {
        new BullsEye();
    }

    public BullsEye() {
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
        for (int i = 0; i < 50; i++) {
            tests.add(new InputOutput(new float[] {random.nextFloat() * 4.0f - 2.0f, random.nextFloat() * 4.0f - 2.0f}, new float[] {1.0f, 0.0f}));
        }
        while (tests.size() < 100) {
            final InputOutput test = new InputOutput(new float[] {random.nextFloat() * 10.0f - 5.0f, random.nextFloat() * 10.0f - 5.0f}, new float[] {0.0f, 1.0f});
            if (test.input[0] >= 3.0f || test.input[0] <= -3.0f || test.input[1] >= 3.0f || test.input[1] <= -3.0f) {
                tests.add(test);
            }
        }
        return tests.stream().toArray(InputOutput[]::new);
    }

    public ConfusionMatrix genConfusionMatrix(final Network network, final InputOutput[] tests) {
        ConfusionMatrix internal = new ConfusionMatrix("in", "out");

        for (int i = 0; i < tests.length; i++) {
            final float[] result = network.apply(tests[i].input);
            internal = internal.add(new TwoCategories(result), new TwoCategories(tests[i].expectedOutput));
        }

        return internal;
    }

}
