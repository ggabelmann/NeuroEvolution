package ggabelmann.ne;

/**
 * An immutable ConfusionMatrix that is currently limited to 2 categories.
 * http://www.dataschool.io/simple-guide-to-confusion-matrix-terminology/
 */
public class ConfusionMatrix {

    private final String[] labels;
    private final int[][] matrix; // predicted are the rows

    public ConfusionMatrix(final String... labels) {
        if (labels == null || labels.length != 2) {
            throw new IllegalArgumentException();
        }

        this.labels = labels;
        this.matrix = new int[labels.length][labels.length];
    }
    
    /**
     * @return A new ConfusionMatrix that has the updated data.
     */
    public ConfusionMatrix add(final TwoCategories predicted, final TwoCategories actual) {
        final ConfusionMatrix updated = new ConfusionMatrix(labels);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                updated.matrix[i][j] = matrix[i][j];
            }
        }
        updated.matrix[predicted.indexOfGreatest()][actual.indexOfGreatest()]++;
        return updated;
    }
    
    /**
     * TODO: refactor because this is pretty hardcoded.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\t\t\t\t").append(String.join("\t", labels)).append("\ttotal").append("\n");

        for (int i = 0; i < labels.length; i++) {
            sb.append("predicted ").append(labels[i]).append("\t");
            int total = 0;
            for (int j = 0; j < labels.length; j++) {
                total += matrix[i][j];
                sb.append(matrix[i][j]).append(" \t");
            }
            sb.append(total).append("\n");
        }

        sb.append("\n");

        sb.append("  accuracy:  ").append(getAccuracy()).append("\n");
        sb.append("  recall:    ").append(getRecall()).append("\t\tWhen it's actually '").append(labels[0]).append("', how often does it predict that?\n");
        sb.append("  precision: ").append(getPrecision()).append("\t\tWhen the prediction is '").append(labels[0]).append("', how often is it correct?\n");
        
        // Also create a "random" matrix and compare against it as a sanity check.
        final ConfusionMatrix randomMatrix = new ConfusionMatrix(labels);
        randomMatrix.matrix[0][0] = (matrix[0][0] + matrix[1][0]) / 2;
        randomMatrix.matrix[1][0] = randomMatrix.matrix[0][0];
        randomMatrix.matrix[0][1] = (matrix[0][1] + matrix[1][1]) / 2;
        randomMatrix.matrix[1][1] = randomMatrix.matrix[0][1];
        sb.append("r_accuracy:  ").append(randomMatrix.getAccuracy()).append("\n");
        sb.append("r_recall:    ").append(randomMatrix.getRecall()).append("\n");
        sb.append("r_precision: ").append(randomMatrix.getPrecision()).append("\n");

        return sb.toString();
    }
    
    // the two correct prediction quadrants / all four quadrants
    public float getAccuracy() {
        return (matrix[0][0] + matrix[1][1]) / (0.0f + matrix[0][0] + matrix[0][1] + matrix[1][0] + matrix[1][1]);
    }

    // When it's actually yes, how often does it predict yes?
    public float getRecall() {
        return matrix[0][0] / (0.0f + matrix[0][0] + matrix[1][0]);
    }

    // When it predicts yes, how often is it correct?
    public float getPrecision() {
        return matrix[0][0] / (0.0f + matrix[0][0] + matrix[0][1]);
    }

}
