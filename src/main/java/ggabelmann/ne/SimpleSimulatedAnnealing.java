package ggabelmann.ne;

import java.util.*;

/**
 * There are lots of alternatives to this class's algorithm.
 *
 * Possible actions?
 * - Choose one NN and remove from the population.
 * - Create a new NN and add to the population.
 * - Choose an NN and add its mutated child to the population.
 *
 * Metadata could be?
 * - History of actions.
 * - History of fitnesses.
 *
 * Could the algorithm observe its own sequence of actions and their results and "learn" which actions to do when?
 */
public class SimpleSimulatedAnnealing {

    private final Random random;
    public final Network network;
    private final MutateNetwork mutateNetwork;

    public SimpleSimulatedAnnealing(final Random random, final MutateNetwork mutateNetwork, final Network network) {
        this.random = random;
        this.mutateNetwork = mutateNetwork;
        this.network = network;
    }

    /**
     * @param comparator A way of comparing two Networks. It must return less than 0 if the left Network is better than the right Network.
     * @param energy Does not match the true definition of "energy". This is the chance that a poor result will be chosen.
     */
    public SimpleSimulatedAnnealing evolve(final Comparator<Network> comparator, final float energy) {
        for (;;) {
            final Network next = mutateNetwork.apply(network);
            final int compare = comparator.compare(next, network);
            if (compare < 0 || (random.nextFloat() < energy)) {
                return new SimpleSimulatedAnnealing(random, mutateNetwork, next);
            }
        }
    }

    public Network bestNetwork() {
        return network;
    }

}
