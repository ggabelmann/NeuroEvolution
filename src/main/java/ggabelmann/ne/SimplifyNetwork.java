package ggabelmann.ne;

import java.util.function.UnaryOperator;

/**
 * Finds the edges that have weights closest to 0.0f and sets them to 0.0f.
 */
public class SimplifyNetwork implements UnaryOperator<Network> {

    public SimplifyNetwork() {
    }

    @Override
    public Network apply(final Network network) {
        final Network.Edge[] edges = network.edges().toArray(Network.Edge[]::new);
        for (int count = 0; count < edges.length / 100; count++) {
            int index = 0;
            for (int i = 0; i < edges.length; i++) {
                if (Math.abs(edges[i].weight) < Math.abs(edges[index].weight)) {
                    index = i;
                }
            }
            edges[index] = new Network.Edge(edges[index].from, edges[index].to, 0.0f);
        }

        return new Network(
                network.nodes().toArray(Network.Node[]::new),
                edges
        );
    }

}
