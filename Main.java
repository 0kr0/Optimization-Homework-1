import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // input
        Scanner in = new Scanner(System.in);

        System.out.println("The number of variables:");
        int n = in.nextInt();

        System.out.println("The coefficient of objective function:");
        double[] C = new double[n];
        for (int i = 0; i < n; i++) {
            C[i] = in.nextDouble();
        }

        System.out.println("The number of constraints:");
        int m = in.nextInt();

        System.out.println("The coefficient of constraint function:");
        double[][] A = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = in.nextDouble();
            }
        }

        System.out.println("The right-hand side numbers:");
        double[] b = new double[m];
        for (int i = 0; i < m; i++) {
            b[i] = in.nextDouble();
        }

        System.out.println("The approximation accuracy:");
        int accuracy = in.nextInt();
        if (accuracy < 0) {
            System.out.println("The approximation accuracy is negative");
            return;
        }

        // check
        if (!isApplicable(b)) {
            System.out.println("The method is not applicable!");
            return;
        }

        // solution
        double[][] simplexTable = new double[m + 1][n + m + 1];
        int[] basicVariables = new int[m]; // array for basis variables
        for (int i = 0; i < m + 1; i++) {
            if (i == 0) {
                for (int j = 0; j < n; j++) simplexTable[i][j] = -C[j]; // coeffs in objective
            } else {
                for (int j = 0; j < n; j++) simplexTable[i][j] = A[i - 1][j]; // coeffs of constraints
                simplexTable[i][n + i - 1] = 1; // Slack variable
                simplexTable[i][n + m] = b[i - 1]; // RHS
                basicVariables[i - 1] = n + i - 1; // keep track of basis variables
            }
        }

        while (!isOptimal(simplexTable, n)) {
            int keyColumn = -1;
            double minValue = 0;
            for (int i = 0; i < n; i++) {
                if (simplexTable[0][i] < minValue) {
                    minValue = simplexTable[0][i];
                    keyColumn = i;
                }
            }

            int keyRow = -1;
            minValue = Double.MAX_VALUE;
            for (int i = 1; i < m + 1; i++) {
                if (simplexTable[i][keyColumn] > 0 && simplexTable[i][n + m] / simplexTable[i][keyColumn] < minValue) {
                    minValue = simplexTable[i][n + m] / simplexTable[i][keyColumn];
                    keyRow = i;
                }
            }
            if (keyRow == -1) {
                System.out.println("The method is not applicable!");
                return;
            }

            double keyElement = simplexTable[keyRow][keyColumn];
            for (int i = 0; i < n + m + 1; i++) {
                simplexTable[keyRow][i] /= keyElement;
            }

            for (int i = 0; i < m + 1; i++) {
                if (i != keyRow) {
                    double c = -simplexTable[i][keyColumn];
                    for (int j = 0; j < n + m + 1; j++) {
                        simplexTable[i][j] += c * simplexTable[keyRow][j];
                    }
                }
            }

            // updating the basis variables
            basicVariables[keyRow - 1] = keyColumn;
        }

        double[] x = new double[n]; // Solution vector must be length of n
        for (int i = 0; i < m; i++) {
            if (basicVariables[i] < n) { // If it is variable from objective function(not slack variable)
                x[basicVariables[i]] = simplexTable[i + 1][n + m];
            }
        }
        double value = simplexTable[0][n + m];

        // output
        System.out.println("The vector of decision variables:");

        for (int i = 0; i < n; i++) {
            System.out.print("x" + (i+1) + "=" + String.format("%." + accuracy + "f", x[i]) + " ");
        }

        System.out.println();
        System.out.println("The value of the objective function:");
        System.out.println(String.format("%." + accuracy + "f", value));
    }

    private static boolean isApplicable(double[] b) {
        for (double v : b) {
            if (v < 0) return false;
        }
        return true;
    }

    private static boolean isOptimal(double[][] table, int n) {
        for (int i = 0; i < n; i++) {
            if (table[0][i] < 0) return false;
        }
        return true;
    }
}


