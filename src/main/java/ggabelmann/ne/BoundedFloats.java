package ggabelmann.ne;

import java.util.function.UnaryOperator;

/**
 * A function that is able to bound an array of floats.
 * For example, bound the values between 0f and 255f, inclusive.
 */
public class BoundedFloats implements UnaryOperator<float[]> {

    private final float min;
    private final float max;

    public BoundedFloats() {
        this(-Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public BoundedFloats(final float min, final float max) {
        this.min = min;
        this.max = max;
    }

    /**
     * @return A new float[] that has been bounded.
     */
    @Override
    public float[] apply(final float[] floats) {
        final float[] result = new float[floats.length];
        for (int i = 0; i < floats.length; i++) {
            if (floats[i] < min) {
                result[i] = min;
            }
            else if (floats[i] > max) {
                result[i] = max;
            }
            else {
                result[i] = floats[i];
            }
        }
        return result;
    }

}
