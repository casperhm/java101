package coding101.tq.example;

import java.io.IOException;

/**
 * Demonstration of a random range calculation.
 */
public class DynamicRandomRange {

    public static int randomIntFromZeroTo(int max) {
        double outPut = (Math.random() * max);
        int roundedOutPut = (int) Math.round(outPut);

        return roundedOutPut;
    }

    public static void main(String[] args) throws IOException {
        int max = Integer.parseInt(args[0]);
        for (int i = 0; i < 10; i++) {
            int rand = randomIntFromZeroTo(max);
            System.out.println(rand);
        }
    }
}
