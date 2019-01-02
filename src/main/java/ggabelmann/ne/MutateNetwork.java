package ggabelmann.ne;

import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.function.UnaryOperator;

/**
 * A function that is able to create a new Network from a given Network.
 */
public class MutateNetwork implements UnaryOperator<Network> {

    private final Random random;
    private final DoubleSupplier scale;
    
    /**
     * Constructor.
     *
     * @param random A source of random values.
     * @param scale Used to scale a source of random Gaussian values.
     */
    public MutateNetwork(final Random random, final DoubleSupplier scale) {
        this.random = random;
        this.scale = scale;
    }

    public MutateNetwork(final Random random) {
        this(random, () -> 0.1);
    }

    @Override
    public Network apply(final Network network) {
        return new Network(
                network.nodes()
                        .map(this::mutateNode)
                        .toArray(Network.Node[]::new),
                network.edges()
                        .map(this::mutateEdge)
                        .toArray(Network.Edge[]::new)
        );
    }

    private Network.Node mutateNode(final Network.Node node) {
        final double factor = scale.getAsDouble();
        if (factor == 0.0) {
            // If the scaling factor is 0.0 then no mutation can happen.
            return node;
        }

        if (node.type == Network.Node.Type.HIDDEN || node.type == Network.Node.Type.OUTPUT) {
            // Basically, if the node's bias is above 0 then there is a 55% chance that the mutation
            // will move in the negative direction.
            // And vice-versa.
            // This is to keep values close to zero.
            final boolean biasAboveZero = node.bias >= 0.0f;
            final boolean tendBack = random.nextFloat() < 0.55f;

            final float mutation = (float) Math.abs(factor * random.nextGaussian());
            if (biasAboveZero && tendBack || !biasAboveZero && !tendBack) {
                return new Network.Node(node.bias - mutation, node.type, node.activation);
            }
            else {
                return new Network.Node(node.bias + mutation, node.type, node.activation);
            }
        }
        else {
            return node;
        }
    }

    private Network.Edge mutateEdge(final Network.Edge edge) {
        final double factor = scale.getAsDouble();
        if (factor == 0.0) {
            // If the scaling factor is 0.0 then no mutation can happen.
            return edge;
        }

        // Basically, if the node's weight is above 0 then there is a 55% chance that the mutation
        // will move in the negative direction.
        // And vice-versa.
        // This is to keep values close to zero.
        final boolean weightAboveZero = edge.weight >= 0.0f;
        final boolean tendBack = random.nextFloat() < 0.55f;

        final float mutation = (float) Math.abs(factor * random.nextGaussian());
        if (weightAboveZero && tendBack || !weightAboveZero && !tendBack) {
            return new Network.Edge(edge.from, edge.to, edge.weight - mutation);
        }
        else {
            return new Network.Edge(edge.from, edge.to, edge.weight + mutation);
        }
    }

}
