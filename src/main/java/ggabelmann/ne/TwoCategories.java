package ggabelmann.ne;

/**
 * Very simple class for two categories.
 * Does not affect the input values at all.
 *
 * This is primarily used by ConfusionMatrix.
 */
public class TwoCategories {

    public final float[] values;

    public TwoCategories(final float[] values) {
        if (values == null || values.length != 2) {
            throw new IllegalArgumentException();
        }

        this.values = values;
    }

    public int indexOfGreatest() {
        if (values[0] >= values[1]) {
            return 0;
        }
        else {
            return 1;
        }
    }

}
