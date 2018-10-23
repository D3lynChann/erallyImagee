import HE.Paillier;

import java.math.BigInteger;

public class HybridSystem {
    //GTS
    public static void GsTrans(int[][] input) throws Exception{
        int sii = 2;
        int[][] GT= {{1,4,7,4,1},{4,16,26,16,4},{7,26,41,26,7},{4,16,26,16,4},{1,4,7,4,1}};

        for (int i = sii; i < input.length - sii; i++) {
            for (int j = sii;  j < input[0].length - sii; j++) {
                double temp = 0;
                for (int k = -1 * sii; k <= sii; k++)
                    for (int l = -1 * sii; l <= sii; l++) {
                        temp += input[i + k][j + l] * GT[k + sii][l + sii];
                    }
                input[i][j] = (int)temp / 273;
            }
        }
        //a small GST
        /*
        int[][] GT= {{1, 2, 1},{2, 4, 2},{1, 2, 1}};
        for (int i = 1; i < input.length - 1; i++) {
            for (int j = 1;  j < input[0].length - 1; j++) {
                int temp = 0;
                for (int k = -1; k < 2; k++)
                    for (int l = -1; l < 2; l++) {
                        temp += input[i + k][j + l] * GT[k + 1][l + 1];
                    }
                input[i][j] = temp / 16;
            }
        }*/
    }

    //GTS with HE
    public static BigInteger[][] GsTrans(BigInteger[][] input, Paillier pp) throws Exception{
        String[][] GT= {{"1","4","7","4","1"},{"4","16","26","16","4"},{"7","26","41","26","7"},{"4","16","26","16","4"},{"1","4","7","4","1"}};
        BigInteger[][] FiltedImage = new BigInteger[input.length][input[0].length];
        for (int i = 2; i < input.length - 2; i++) {
            for (int j = 2;  j < input[0].length - 2; j++) {
                BigInteger temp = pp.encrypt(new BigInteger("0"));
                for (int k = -2; k < 3; k++)
                    for (int l = -2; l < 3; l++)
                        temp = temp.multiply(input[i + k][j + l].modPow(new BigInteger(GT[k + 2][l + 2]), pp.getNsquare()).mod(pp.getNsquare())).mod(pp.getNsquare());
                FiltedImage[i][j] = pp.encrypt(pp.decrypt(temp).divide(new BigInteger("273")));
            }
        }
        return FiltedImage;
    }

    //Sobel operator
    public static int[][] SobelIt(int[][] data) throws Exception {
        int w = data[0].length;
        int h = data.length;
        int[][] d= new int[h][w];

        for(int ctr=1;ctr<h-1;ctr++){
            for(int itr=1;itr<w-1;itr++){
                int s1 = data[ctr - 1][itr + 1] + 2 * data[ctr][itr + 1] + data[ctr + 1][itr + 1] -
                        data[ctr - 1][itr - 1] - 2 * data[ctr][itr - 1] - data[ctr + 1][itr - 1];
                int s2 = data[ctr - 1][itr - 1] +  2 * data[ctr - 1][itr] + data[ctr - 1][itr + 1] -
                        data[ctr + 1][itr - 1] - 2 * data[ctr + 1][itr] - data[ctr + 1][itr + 1];
                int s  = Math.abs(s1)+Math.abs(s2);
                if(s < 0)
                    s =0;
                if(s > 255)
                    s = 255;
                d[ctr][itr] = s;
            }
        }
        /*
        for (int ctr = 2; ctr < h - 2; ctr++)
            for (int itr = 2; itr < w - 2;  itr++) {
                d[ctr][itr] = -2 * data[ctr - 2][itr - 2] - 4 * data[ctr - 2][itr - 1] - 4 * data[ctr - 2][itr] - 4 * data[ctr - 2][itr + 1] - 2 * data[ctr - 2][itr + 2]
                             - 4 * data[ctr - 1][itr - 2] + 8 * data[ctr - 1][itr] - 4 * data[ctr - 1][itr + 2]
                             - 4 * data[ctr][itr - 2] + 8 * data[ctr][itr - 1] + 24 * data[ctr][itr] + 8 * data[ctr][itr + 1] - 4 * data[ctr][itr + 2]
                             - 4 * data[ctr + 1][itr - 2] + 8 * data[ctr + 1][itr] - 4 * data[ctr + 1][itr + 2]
                             - 2 * data[ctr + 2][itr - 2] - 4 * data[ctr + 2][itr - 1] - 4 * data[ctr + 2][itr] - 4 * data[ctr + 2][itr + 1] - 2 * data[ctr + 2][itr + 2];
            }
            */
        return d;
    }

    //Laplas operator
    public static int[][] LplsIt(int[][] data) throws Exception {
        int[][] res = new int[data.length][data[0].length];
        int[][] GT= {{0, -1, 0},{-1, 4, -1},{0, -1, 0}};
        for (int i = 1; i < data.length - 1; i++) {
            for (int j = 1;  j < data[0].length - 1; j++) {
                int temp = 0;
                for (int k = -1; k < 2; k++)
                    for (int l = -1; l < 2; l++) {
                        temp += data[i + k][j + l] * GT[k + 1][l + 1];
                    }
                res[i][j] = temp;
            }
        }
        return res;
    }


    //get the histo of a matrix
    public static int[] getHisto(int[][] data) throws Exception {
        int histo[] = new int[256];
        //int sizz = data.length * data[0].length;
        for (int ctr = 0; ctr < data.length; ctr++)
            for (int itr = 0; itr < data[0].length; itr++) {
                if (data[ctr][itr] > 255) data[ctr][itr] = 255;
                if (data[ctr][itr] < 0) data[ctr][itr] = 0;
                histo[data[ctr][itr]]++;
            }
        return histo;
    }

    //get the double histo of a matrix
    public static double[] getHistoo(int[][] data) throws Exception {
        double histo[] = new double[256];
        int sizz = data.length * data[0].length;
        for (int ctr = 0; ctr < data.length; ctr++)
            for (int itr = 0; itr < data[0].length; itr++) {
                if (data[ctr][itr] > 255) data[ctr][itr] = 255;
                if (data[ctr][itr] < 0) data[ctr][itr] = 0;
                histo[data[ctr][itr]]++;
            }
        for (int ctr = 0; ctr < 255; ctr++)
            histo[ctr] = (double)histo[ctr]/sizz;
        return histo;
    }

    //get the threshold of a matrix in an iteration way
    public static int iterationGetThreshold(int[][] data) throws Exception {
        int min = data[0][0], max = data[0][0];
        int sizz = data.length * data[0].length;
        for (int ctr = 0; ctr < data.length; ctr++)
            for (int itr = 0; itr < data[0].length; itr++) {
                if (data[ctr][itr] > 255) data[ctr][itr] = 255;
                if (data[ctr][itr] < 0) data[ctr][itr] = 0;
                if (min > data[ctr][itr]) min = data[ctr][itr];
                if (max < data[ctr][itr]) max = data[ctr][itr];
            }
        int histo[] = getHisto(data);
        int threshold = 0;
        int newThreshold = (min + max) / 2;
        while (threshold != newThreshold) {
            int sum1 = 0, sum2 = 0, w1 = 0, w2 = 0;
            int avg1, avg2;
            for (int ctr = min; ctr < newThreshold; ctr++) {
                sum1 += histo[ctr] * ctr;
                w1 += histo[ctr];
            }
            avg1 = (int)(sum1/ w1);
            for (int ctr = newThreshold; ctr < max; ctr++) {
                sum2 += histo[ctr] * ctr;
                w2 += histo[ctr];
            }
            avg2 = (int)(sum2/ w2);
            threshold = newThreshold;
            newThreshold = (avg1+avg2)/2;
        }
        return newThreshold;
    }

    //take apart the matrix and handle them
    public static int[][] inPeices(int[][] matrix, int pie) throws Exception {
        //int pie = 2;
        int sX = (matrix.length - 6) / pie, sY = (matrix[0].length - 6) / pie;
        int[][][][] temppMatrix = new int[pie + 1][pie + 1][sX + 6][sY + 6];
        int[][][][] resMatrix = new int[pie + 1][pie + 1][sX][sY];
        for (int ctr = 0; ctr < pie; ctr++)
            for (int itr = 0; itr < pie; itr++)
                for (int x = 0; x < sX + 6; x++)
                    for (int y = 0; y < sY + 6; y++)
                        temppMatrix[ctr][itr][x][y] = matrix[ctr * sX + x][itr * sY + y];
        for (int ctr = 0; ctr < pie; ctr++)
            for (int itr = 0; itr < pie; itr++) {
                GsTrans(temppMatrix[ctr][itr]);
                DoTheImg.NormalBinaryImage(temppMatrix[ctr][itr], iterationGetThreshold(temppMatrix[ctr][itr]));
                temppMatrix[ctr][itr] = SobelIt(temppMatrix[ctr][itr]);
                for (int x = 0; x < sX; x++)
                    for (int y = 0; y < sY; y++)
                        resMatrix[ctr][itr][x][y] = temppMatrix[ctr][itr][x + 3][y + 3];
            }

        int[][] newMatrix = new int[pie * sX][pie * sY];
        for (int a = 0; a < pie; a++)
            for (int b = 0; b < pie; b++)
                for (int c = 0; c < sX; c++)
                    for (int d = 0; d < sY; d++)
                        newMatrix[a * sX + c][b * sY + d] = resMatrix[a][b][c][d];
        return newMatrix;
    }


}
