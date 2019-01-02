package ggabelmann.ne;

/**
 * Very simple class for two categories.
 * This is primarily used by ConfusionMatrix.
 */
public class TwoCategories {

    public final float[] values;
    
    /**
     * Constructor.
     * Must be length 2.
     *
     * @param values The values to copy to an internal array.
     */
    public TwoCategories(final float[] values) {
        if (values == null || values.length != 2) {
            throw new IllegalArgumentException();
        }

        this.values = new float[] {values[0], values[1]};
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
