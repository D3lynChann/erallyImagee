import HE.Paillier;
import circuit.Main;
import circuit.h;

import java.math.BigInteger;
import java.util.ArrayList;

public class Houghh {
    //shit, i forgot it
    public static ArrayList<h> getArrayListOfHist(int[][] inputH, double rate, boolean flag) {
        ArrayList<h> res = new ArrayList<>();
        ArrayList<h> res0 = new ArrayList<>();
        int max = 0;
        for (int ctr = 0; ctr < inputH.length; ctr++)
            for (int itr = 0; itr < inputH[0].length; itr++)
                if (inputH[ctr][itr] > max)
                    max = inputH[ctr][itr];
        for (int ctr = 0; ctr < inputH.length; ctr++)
            for (int itr = 0; itr < inputH[0].length; itr++)
                if (inputH[ctr][itr] > max * rate) {
                    // h temp = new h(ctr, flag? itr: 180 - itr);
                    h temp = new h(ctr, itr);
                    res.add(temp);
                }
        System.out.println("res's size: " + res.size());
        int lastAngle = res.get(0).angle;
        res0.add(res.get(0));
        for (int ctr = 1; ctr < res.size(); ctr++) {
            if (res.get(ctr).angle >= lastAngle - 5 && res.get(ctr).angle <= lastAngle + 5) {
                continue;
            }
            else {
                lastAngle = res.get(ctr).angle;
                res0.add(res.get(ctr));
            }
        }
        return res0;
    }

    //hough main func.
    public static void hough(int[][] inputI) {
        int h = inputI.length, w = inputI[0].length;
        int ro = (int)Math.sqrt(h * h + w * w);
        int theta = 90;
        int[][] hist = new int[ro][theta];
        //int[][] hist2 = new int[ro][theta];

        for (int ctr = 0; ctr < theta; ctr++)
            for (int itr = 0; itr < h; itr++)
                for (int jtr = 0; jtr < w; jtr++)
                    if (inputI[itr][jtr] != 0) {
                        int rho = (int)(jtr * Math.cos(ctr * Math.PI / (theta * 2)) + itr * Math.sin(ctr * Math.PI / (theta * 2)));
                        hist[rho][ctr]++;
                    }
        /*
        int[][] tempInput = LeftRightMirror(inputI);
        for (int ctr = 0; ctr < theta; ctr++)
            for (int itr = 0; itr < h; itr++)
                for (int jtr = 0; jtr < w; jtr++)
                    if (tempInput[itr][jtr] != 0) {
                        int rho = (int)(jtr * Math.cos(ctr * Math.PI / (theta * 2)) + itr * Math.sin(ctr * Math.PI / (theta * 2)));
                        hist2[rho][ctr]++;
                    }*/
        //ArrayList<h> tempIn = getArrayListOfHist(hist2, 0.60, false);
        ArrayList<h> index = getArrayListOfHist(hist, 0.7, true);
        //for (int ctr = 0; ctr < tempIn.size(); ctr++)
        //    index.add(tempIn.get(ctr));
        System.out.println("hist ready! It has " + index.size() + " lines!");
        for (int ctr = 0; ctr < index.size(); ctr++) {
            System.out.println("Line" + ctr + "'s angle: " + index.get(ctr).angle + " and its rho is " + index.get(ctr).ro);
            double resTheta = index.get(ctr).angle * Math.PI / (theta * 2);
            System.out.println("resTheta: " + resTheta + " o");
            for (int i = 0; i < h; i++)
                for (int j = 0; j < w; j++) {
                    int rho = (int)(j * Math.cos(resTheta) + i * Math.sin(resTheta));
                    if (inputI[i][j] != 0 && rho == index.get(ctr).ro)
                        inputI[i][j] = 0xFF0000; //line set to red
                    else
                        inputI[i][j] = (255 << 24) | (inputI[i][j] << 16) | (inputI[i][j] << 8) | (inputI[i][j]);
                }
        }
    }

    //hough func. big integer version
    public static void hough(BigInteger[][] inputI, Paillier pp) throws Exception {
        BigInteger BRed = BigInteger.valueOf(0xFF0000);
        int h = inputI.length, w = inputI[0].length;
        int ro = (int)Math.sqrt(h * h + w * w);
        int theta = 90;
        int[][] hist = new int[ro][theta];

        for (int ctr = 0; ctr < theta; ctr++)
            for (int itr = 0; itr < h; itr++)
                for (int jtr = 0; jtr < w; jtr++)
                    if (/*inputI[itr][jtr] != 0*/Main.GcSubAndCompBigVer(inputI[itr][jtr], "0", pp)) {
                        int rho = (int)(jtr * Math.cos(ctr * Math.PI / (theta * 2)) + itr * Math.sin(ctr * Math.PI / (theta * 2)));
                        hist[rho][ctr]++;
                    }
        ArrayList<h> index = getArrayListOfHist(hist, 0.7, true);
        System.out.println("hist ready! It has " + index.size() + " lines!");
        for (int ctr = 0; ctr < index.size(); ctr++) {
            System.out.println("Line" + ctr + "'s angle: " + index.get(ctr).angle + " and its rho is " + index.get(ctr).ro);
            double resTheta = index.get(ctr).angle * Math.PI / (theta * 2);
            System.out.println("resTheta: " + resTheta + " o");
            for (int i = 0; i < h; i++)
                for (int j = 0; j < w; j++) {
                    int rho = (int)(j * Math.cos(resTheta) + i * Math.sin(resTheta));
                    if (/*inputI[i][j] != 0*/Main.GcSubAndCompBigVer(inputI[i][j], "0", pp) && rho == index.get(ctr).ro)
                        inputI[i][j] = BRed; //line set to red
                    /*
                    else
                        inputI[i][j] = (255 << 24) | (inputI[i][j] << 16) | (inputI[i][j] << 8) | (inputI[i][j]);*/
                }
        }
    }

    //get 2-pass of a matrix
    public static void make(int[][] input, String fileName) throws Exception {
        for (int ctr = 0; ctr < input.length; ctr++) input[ctr][0] = input[ctr][input[0].length - 1] = 255;
        for (int ctr = 0; ctr < input[0].length; ctr++) input[0][ctr] = input[input.length - 1][ctr] = 255;
        //writeImage(input, fileName);
        twoDCross temp2d = new twoDCross(input);
        temp2d.init();
        temp2d.firstPass();
        temp2d.drawColor(fileName);
        //temp2d.showLabel();
    }

    //make func. big integer version
    public static void makeB(BigInteger[][] input, String fileName, Paillier pp) throws Exception {
        for (int ctr = 0; ctr < input.length; ctr++) input[ctr][0] = input[ctr][input[0].length - 1] = new BigInteger("255");
        for (int ctr = 0; ctr < input[0].length; ctr++) input[0][ctr] = input[input.length - 1][ctr] = new BigInteger("255");
        //writeImage(input, fileName);
        twoDCross temp2d = new twoDCross(input, pp);
        temp2d.binit();
        temp2d.bFirstPass();
        temp2d.bDrawColor(fileName);
    }

    //gamma the matrix
    static public void GammaSch(int[][] input) {
        for (int ctr = 0; ctr < input.length; ctr++)
            for (int itr = 0; itr < input[0].length; itr++)
                input[ctr][itr] = (int)Math.sqrt(input[ctr][itr]);
    }

    //gamma the matrix and get the double res
    static public double[][] GammaSchD(int[][] input) {
        double[][] res = new double[input.length][input[0].length];
        for (int ctr = 0; ctr < input.length; ctr++)
            for (int itr = 0; itr < input[0].length; itr++)
                res[ctr][itr] = Math.sqrt(input[ctr][itr]);
        return res;
    }

    //get the gradient of a matrix(abd)
    static public void getGradient(int[][] input, int[][] output1, double[][] output2) {
        int gx, gy;
        for (int ctr = 0; ctr < output1.length; ctr++)
            for (int itr = 0; itr < output1[0].length; itr++) output1[ctr][itr] = 0;
        for (int ctr = 0; ctr < output2.length; ctr++)
            for (int itr = 0; itr < output2[0].length; itr++) output2[ctr][itr] = 0.0;
        for (int ctr = 1; ctr < input.length - 1; ctr++) {
            for (int itr = 1; itr < input[0].length - 1; itr++) {
                gx = input[ctr + 1][itr] - input[ctr - 1][itr];
                gy = input[ctr][itr + 1] - input[ctr][itr - 1];
                output1[ctr][itr] = (int)Math.sqrt(gx * gx + gy * gy);
                output2[ctr][itr] = Math.atan(1.0 * gx / gy);
            }
        }
    }

    //get the double gradient of a matrix(abd)
    static public void getGradientD(double[][] input, double[][] output1, double[][] output2) {
        double gx, gy;
        for (int ctr = 0; ctr < output1.length; ctr++)
            for (int itr = 0; itr < output1[0].length; itr++) output1[ctr][itr] = 0;
        for (int ctr = 0; ctr < output2.length; ctr++)
            for (int itr = 0; itr < output2[0].length; itr++) output2[ctr][itr] = 0.0;
        for (int ctr = 1; ctr < input.length - 1; ctr++) {
            for (int itr = 1; itr < input[0].length - 1; itr++) {
                gx = input[ctr + 1][itr] - input[ctr - 1][itr];
                gy = input[ctr][itr + 1] - input[ctr][itr - 1];
                output1[ctr][itr] = Math.sqrt(gx * gx + gy * gy);
                output2[ctr][itr] = Math.atan(gx / gy);
            }
        }
    }

    //turn the hos into a radio
    static public int getHosOfHog(double input) {
        if (input <= Math.PI / 8 && input > -Math.PI / 8) return 0;
        else if (input <= 3 * Math.PI / 8 && input > Math.PI / 8) return 1;
        else if (input <= 5 * Math.PI / 8 && input > 3 * Math.PI / 8) return 2;
        else if (input <= 7 * Math.PI / 8 && input > 5 * Math.PI / 8) return 3;
        else if (input <= -5 * Math.PI / 8 && input > -7 * Math.PI / 8) return 5;
        else if (input <= -3 * Math.PI / 8 && input > -5 * Math.PI / 8) return 6;
        else if (input <= -1 * Math.PI / 8 && input > -3 * Math.PI / 8) return 7;
        else return 4;
    }

    //put the whole matrix into a bin
    static public int[][][] binIt(int[][] input1, double[][] input2) {
        //eight dem
        int sizeOC = 3;
        int h = input1.length / sizeOC;
        int w = input1[0].length/sizeOC;
        int[][][] bin = new int[input1.length / sizeOC][input1[0].length/sizeOC][8];
        for (int ctr = 0; ctr < h; ctr++)
            for (int itr = 0; itr < w; itr++)
                for (int otr = 0; otr < 8; otr++)
                    bin[ctr][itr][otr] = 0;
        for (int ctr = 0; ctr < h; ctr++) {
            for (int itr = 0; itr < w; itr++) {
                for (int i = 0; i < sizeOC; i++) {
                    for (int j = 0; j < sizeOC; j++) {
                        bin[ctr][itr][getHosOfHog(input2[ctr * sizeOC + i][itr * sizeOC + j])] += input1[ctr * sizeOC + i][itr * sizeOC + j];
                    }
                }
            }
        }
        return bin;
    }

    //binIt func. big integer version
    static public double[][][] binIt(double[][] input1, double[][] input2) {
        //eight dem
        int sizeOC = 3;
        int h = input1.length / sizeOC;
        int w = input1[0].length/sizeOC;
        double[][][] bin = new double[input1.length / sizeOC][input1[0].length/sizeOC][8];
        for (int ctr = 0; ctr < h; ctr++)
            for (int itr = 0; itr < w; itr++)
                for (int otr = 0; otr < 8; otr++)
                    bin[ctr][itr][otr] = 0;
        for (int ctr = 0; ctr < h; ctr++) {
            for (int itr = 0; itr < w; itr++) {
                for (int i = 0; i < sizeOC; i++) {
                    for (int j = 0; j < sizeOC; j++) {
                        bin[ctr][itr][getHosOfHog(input2[ctr * sizeOC + i][itr * sizeOC + j])] += input1[ctr * sizeOC + i][itr * sizeOC + j];
                    }
                }
            }
        }
        return bin;
    }

    //block the matrix
    static public int[][][] blockIt(int[][][] matrix) {
        //9-cell block
        int[][][] res = new int[matrix.length - 2][matrix[0].length - 2][8];
        for (int ctr = 0; ctr < res.length; ctr++){
            for (int itr = 0; itr < res[0].length; itr++) {
                int temp = 0;
                for (int dx = 0; dx < 3; dx++)
                    for (int dy = 0; dy < 3; dy++)
                        for (int i = 0; i < 8; i++)
                            temp += matrix[ctr + dx][itr + dy][i];
            }
        }
        return res;
    }

    //blockIt func. big integer version
    static public double[][][] blockIt(double[][][] matrix) {
        //9-cell block
        double[][][] res = new double[matrix.length - 2][matrix[0].length - 2][8];
        for (int ctr = 0; ctr < res.length; ctr++){
            for (int itr = 0; itr < res[0].length; itr++) {
                int temp = 0;
                for (int dx = 0; dx < 3; dx++)
                    for (int dy = 0; dy < 3; dy++)
                        for (int i = 0; i < 8; i++)
                            temp += matrix[ctr + dx][itr + dy][i];
            }
        }
        return res;
    }

    //the 8-pass matrix into a 1-pass matrix
    static public double[][][] toOne(int[][][] matrix) {
        double[][][] res = new double[matrix.length][matrix[0].length][8];
        for (int ctr = 0; ctr < matrix.length; ctr++)
            for (int itr = 0; itr < matrix[0].length; itr++) {
                int min = 1000000, max = -1;
                for (int i = 0; i < 8; i++) {
                    if (matrix[ctr][itr][i] > max) max = matrix[ctr][itr][i];
                    if (matrix[ctr][itr][i] < min) min = matrix[ctr][itr][i];
                }
                for (int i = 0; i < 8; i++) res[ctr][itr][i] = (matrix[ctr][itr][i] - min) * 1.0 / (max - min);
            }
        return res;
    }

    //toOne func. double version
    static public double[][][] toOne(double[][][] matrix) {
        double[][][] res = new double[matrix.length][matrix[0].length][8];
        for (int ctr = 0; ctr < matrix.length; ctr++)
            for (int itr = 0; itr < matrix[0].length; itr++) {
                double min = 1000000, max = -1;
                for (int i = 0; i < 8; i++) {
                    if (matrix[ctr][itr][i] > max) max = matrix[ctr][itr][i];
                    if (matrix[ctr][itr][i] < min) min = matrix[ctr][itr][i];
                }
                for (int i = 0; i < 8; i++) res[ctr][itr][i] = (matrix[ctr][itr][i] - min) * 1.0 / (max - min);
            }
        return res;
    }

    //what the fuck
    public static int[][] toThree(int[][] matrix) {
        int h = matrix.length / 3;
        int w = matrix[0].length / 3;
        int[][] res = new int[h * 3][w * 3];
        for (int ctr = 0; ctr < h * 3; ctr++)
            for (int itr = 0; itr < w * 3; itr++)
                res[ctr][itr] = matrix[ctr][itr];
        return res;
    }

}
