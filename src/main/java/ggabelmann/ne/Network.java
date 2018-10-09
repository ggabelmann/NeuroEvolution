package ggabelmann.ne;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * A stateless neural network.
 */
public class Network implements UnaryOperator<float[]> {

    private final Node[] nodes;
    private final Edge[] edges;

    public Network(final Node[] nodes, final Edge[] edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public float[] apply(final float[] input) {
        final float[] working = new float[nodes.length];

        for (int i = 0; i < nodes.length; i++) {
            working[i] = nodes[i].bias;
        }

        for (int i = 0; i < input.length; i++) {
            working[i] += input[i];
        }

        // These loops are somewhat tricky.
        // They work because the edges are sorted by their 'from' fields.
        int i = 0;
        int j = 0;
        for (; i < nodes.length; i++) {
            if (nodes[i].activation == Node.Activation.RELU) {
                if (working[i] < 0.0f) {
                    working[i] /= 10.0f; // Interpret as a Leaky ReLU, which seems to help converge faster.
                }
            }

            for (; j < edges.length; j++) {
                final int from = edges[j].from;
                final int to = edges[j].to;
                final float weight = edges[j].weight;

                if (from > i) {
                    // no point in looking at the rest of the edges...
                    break;
                }

                if (from == i) {
                    final float delta = working[from] * weight;
                    working[to] += delta;
                }
            }
        }

        int index = 0;
        for (int k = 0; k < nodes.length; k++) {
            if (nodes[k].type == Node.Type.OUTPUT) {
                index = k;
                break;
            }
        }
        final float[] output = new float[nodes.length - index];
        System.arraycopy(working, index, output, 0, output.length);
        return output;
    }

    public Stream<Node> nodes() {
        return Arrays.stream(nodes);
    }

    public Stream<Edge> edges() {
        return Arrays.stream(edges);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("biases: id, type, activation, bias\n");
        for (int i = 0; i < nodes.length; i++) {
            sb.append(i).append(", ").append(nodes[i]).append("\n");
        }
        sb.append("edges: from, to, weight\n");
        edges().forEach(edge -> sb.append(edge).append("\n"));
        return sb.toString();
    }


    //


    public static class Node {

        enum Type {
            INPUT,
            HIDDEN,
            OUTPUT
        }

        enum Activation {
            NONE,
            RELU
        }

        public final float bias;
        public final Type type;
        public final Activation activation;

        public Node(final float bias, final Type type, final Activation activation) {
            this.bias = bias;
            this.type = type;
            this.activation = activation;
        }

        @Override
        public String toString() {
            return type + ", " + activation + ", " + bias;
        }
    }

    public static class Edge implements Comparable<Edge> {

        public final int from;
        public final int to;
        public final float weight;

        public Edge(final int from, final int to, final float weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public int compareTo(final Edge o) {
            return Integer.compare(from, from);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Edge edge = (Edge) o;
            return from == edge.from && to == edge.to;
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }

        @Override
        public String toString() {
            return from + ", " + to + ", " + weight;
        }
    }

}
