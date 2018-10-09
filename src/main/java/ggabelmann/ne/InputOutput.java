package ggabelmann.ne;

/**
 * A helper class for specifying expected output during training.
 */
public class InputOutput {

    public final float[] input;
    public final float[] expectedOutput;

    public InputOutput(final float[] input, final float[] expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

}
