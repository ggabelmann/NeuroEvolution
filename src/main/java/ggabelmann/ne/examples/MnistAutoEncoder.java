package ggabelmann.ne.examples;

import ggabelmann.ne.*;
import ggabelmann.ne.turkdogan.MnistDataReader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Autoencoder for MNIST, but down-samples the data and restricts the data to one symbol to make
 * the process manageable on my laptop.
 */
public class MnistAutoEncoder {

    private final int NUM_IMAGES = 100;
    private final int INPUT_NODES = 7 * 7;
    private final int HIDDEN_NODES = 5;
    private final int OUTPUT_NODES = 7 * 7;
    private final Random random;
    private final FloatSupplier gaussianSupplier;
    private final NumberFormat nf;

    public static void main(final String[] args) {
        if (args == null || args.length != 2) {
            System.out.println("Needs two arguments to run: path to images, path to labels.");
        }
        else {
            new MnistAutoEncoder(Paths.get(args[0]), Paths.get(args[1]));
        }
    }

    public MnistAutoEncoder(final Path images, final Path labels) {
        this.random = new Random();
        this.gaussianSupplier = () -> (float) random.nextGaussian();
        this.nf = NumberFormat.getInstance();

        final InputOutput[] tests = mnistTestData(images, labels)
                .filter(matrix -> matrix.getLabel() == 1) // Only keeps the 1 symbols.
                .limit(NUM_IMAGES)
                .map(matrix -> {
                    // Shrink and convert to floats.
                    final float[] flattened = new float[INPUT_NODES];
                    for (int row = 0; row < 7; row++) {
                        for (int col = 0; col < 7; col++) {
                            flattened[(row * 7) + col] = (float) matrix.getValue(row * 4, col * 4);
                        }
                    }
                    return new InputOutput(flattened, flattened);
                })
                .toArray(InputOutput[]::new);

        final UnaryOperator<float[]> bounded = new BoundedFloats(0f, 255f);
        final ToDoubleFunction<Network> calcFitness = (aNetwork) -> {
            float error = 0.0f;
            for (int i = 0; i < tests.length; i++) {
                final float newError = Utilities.squaredError(aNetwork.andThen(bounded).apply(tests[i].input), tests[i].expectedOutput);
                error += newError;
            }
            return error;
        };
        final Comparator<Network> comparator = Comparator.comparingDouble(calcFitness);

        final DiscreteDistribution distribution = new DiscreteDistribution(random,
                new DiscreteDistribution.Internal(0.1, 1),
                new DiscreteDistribution.Internal(0.01, 1),
                new DiscreteDistribution.Internal(0.0, 18)
        );

        SimpleSimulatedAnnealing population = new SimpleSimulatedAnnealing(
                random,
                new MutateNetwork(random, () -> distribution.getAsDouble()),
                NetworkBuilder.singleHiddenLayer(gaussianSupplier, INPUT_NODES, HIDDEN_NODES, OUTPUT_NODES));

        final BiConsumer<BooleanSupplier, Supplier<String>> conditionalPrinter = Utilities.conditionalPrinter();

        for (float energy = 0.01f; ;) {
            population = population.evolve(comparator, energy);
            final Network best = population.bestNetwork();
            conditionalPrinter.accept(
                    () -> random.nextInt(100) == 0,
                    () -> String.format("bestNetwork: %s", nf.format(calcFitness.applyAsDouble(best))));
        }
    }

    private Stream<MnistDataReader.MnistMatrix> mnistTestData(final Path images, final Path labels) {
        try {
            return Arrays.stream(new MnistDataReader().readData(images, labels));
        }
        catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
