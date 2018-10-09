package ggabelmann.ne;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder for Networks.
 * This could be improved a lot.
 */
public class NetworkBuilder {

    public static Network singleHiddenLayer(final FloatSupplier floatSupplier, final int input, final int hidden, final int output) {
        final Network.Node[] nodes = new Network.Node[input + hidden + output];

        for (int i = 0; i < input; i++) {
            nodes[i] = new Network.Node(0.0f, Network.Node.Type.INPUT, Network.Node.Activation.NONE);
            nodes[input + hidden + i] = new Network.Node(0.0f, Network.Node.Type.OUTPUT, Network.Node.Activation.NONE);
        }
        for (int i = input; i < input + hidden; i++) {
            nodes[i] = new Network.Node(floatSupplier.getAsFloat(), Network.Node.Type.HIDDEN, Network.Node.Activation.RELU);
        }

        // Now build the edges.
        final List<Network.Edge> edges = new ArrayList<>();

        for (int i = 0; i < input; i++) {
            for (int j = input; j < input + hidden; j++) {
                edges.add(new Network.Edge(i, j, floatSupplier.getAsFloat()));
            }
        }
        for (int i = input; i < input + hidden; i++) {
            for (int j = input + hidden; j < input + hidden + output; j++) {
                edges.add(new Network.Edge(i, j, floatSupplier.getAsFloat()));
            }
        }

        return new Network(nodes, edges.stream().toArray(Network.Edge[]::new));
    }

    public static Network doubleHiddenLayer(final FloatSupplier floatSupplier, final int input, final int firstHidden, final int secondHidden, final int output) {
        final Network.Node[] nodes = new Network.Node[input + firstHidden + secondHidden + output];

        for (int i = 0; i < input; i++) {
            nodes[i] = new Network.Node(0.0f, Network.Node.Type.INPUT, Network.Node.Activation.NONE);
            nodes[input + firstHidden + secondHidden + i] = new Network.Node(0.0f, Network.Node.Type.OUTPUT, Network.Node.Activation.NONE);
        }
        for (int i = input; i < input + firstHidden + secondHidden; i++) {
            nodes[i] = new Network.Node(floatSupplier.getAsFloat(), Network.Node.Type.HIDDEN, Network.Node.Activation.RELU);
        }

        final List<Network.Edge> edges = new ArrayList<>();

        for (int i = 0; i < input; i++) {
            for (int j = input; j < input + firstHidden; j++) {
                edges.add(new Network.Edge(i, j, floatSupplier.getAsFloat()));
            }
        }
        for (int i = input; i < input + firstHidden; i++) {
            for (int j = input + firstHidden; j < input + firstHidden + secondHidden; j++) {
                edges.add(new Network.Edge(i, j, floatSupplier.getAsFloat()));
            }
        }
        for (int i = input; i < input + firstHidden; i++) {
            for (int j = input + firstHidden + secondHidden; j < input + firstHidden + secondHidden + output; j++) {
                edges.add(new Network.Edge(i, j, floatSupplier.getAsFloat()));
            }
        }

        return new Network(nodes, edges.stream().toArray(Network.Edge[]::new));
    }

}
