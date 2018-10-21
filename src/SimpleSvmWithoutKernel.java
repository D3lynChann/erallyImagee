import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by blister on 2018/4/15.
 */

//create by Delyn Chann

public class SimpleSvmWithoutKernel {
    private int num;
    private int dim;

    //for gradient descent
    private double[] w;
    private double lambda;
    private double jd = 0.00001;
    private double threshold = 0.0001;
    private double cost;
    private double[] grad;
    private int[] Yp; //prediction Y

    public SimpleSvmWithoutKernel(double inputLambda) {
        lambda = inputLambda;
    }

    private void CostAndGradient(double[][] X, int[] y) {
        cost = 0;
        for (int ctr = 0; ctr < num; ctr++) {
            Yp[ctr] = 0;
            for (int itr = 0; itr < dim; itr++)
                Yp[ctr] += X[ctr][itr] * w[itr];
            if (y[ctr] * Yp[ctr] < 1)
                cost += (1 - y[ctr] * Yp[ctr]);
        }

        for (int ctr = 0; ctr < dim; ctr++)
            cost += 0.5 * lambda * w[ctr] * w[ctr];

        for (int ctr = 0; ctr < dim; ctr++) {
            grad[ctr] = Math.abs(lambda * w[ctr]);
            for (int itr = 0; itr < num; itr++)
                if (y[itr] * Yp[itr] < 1)
                    grad[ctr] -= y[itr] * X[itr][ctr];
        }
    }

    private void update() {
        for (int ctr = 0; ctr < dim; ctr++)
            w[ctr] -= jd * grad[ctr];
    }

    public void Train(double[][] X, int[] y, int nums) {
        num = X.length; //get numbers of example
        if (num <= 0) {
            System.out.println("Invalid input: number of examples less than 1!");
            return ;
        }
        dim = X[0].length; //suppose that there are the same dimension
        w = new double[dim];
        grad = new double[dim];
        Yp = new int[num];
        for (int ctr = 0; ctr < nums; ctr++) {
            CostAndGradient(X, y);
            if (cost < threshold) break;
            update();
        }
    }

    private int predict(double[] x) {
        double temp = 0;
        for (int ctr = 0; ctr < x.length; ctr++)
            temp += x[ctr] * w[ctr];
        return (temp >= 0) ? 1 : -1;
    }

    private void Test(double[][] inputX, int[] inputY) {
        int wrong = 0;
        for (int ctr = 0; ctr < inputX.length; ctr++)
            if (predict(inputX[ctr]) != inputY[ctr]) wrong++;
        System.out.println("Number of test examples: " + inputX.length);
        System.out.println("Number of wrong examples: " + wrong);
        System.out.println("Wrong rate: " + (1.0 * wrong / inputX.length));
        System.out.println("Right rate: " + (1 - (1.0 * wrong / inputX.length)));
    }

    public static void load(double[][] X, int[] y, String fileName) {
        File file = new File(fileName);
        //Depending on the specific circumstances
        BufferedReader lines = null;
        try {
            lines = new BufferedReader(new FileReader(file));
            String temp = null;
            int index = 0;
            while ((temp = lines.readLine()) != null) {
                String[] ss = temp.split(" ");
                y[index] = (Integer.valueOf(ss[0].toString()) == 2) ? 1 : -1;
                for (int ctr = 0; ctr < ss.length - 1; ctr++)
                    X[index][ctr] = Double.valueOf(ss[ctr + 1].split(":")[1]);
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (lines != null) {
                try {
                    lines.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int testSize = 18 , trainSize = 665;
        int[] y = new int[trainSize];
        int[] testY = new int[testSize];
        double[][] X = new double[trainSize][11];
        double[][] testX = new double[testSize][11];
        String trainSet = "C:\\Users\\blister\\Desktop\\Train.txt";
        String testSet = "C:\\Users\\blister\\Desktop\\Test.txt";
        load(X, y, trainSet);
        load(testX, testY, testSet);
        SimpleSvmWithoutKernel ssvm = new SimpleSvmWithoutKernel(0.0001);
        ssvm.Train(X, y, 100000);
        ssvm.Test(testX, testY);
        return ;
    }
}
