import HE.Paillier;
import circuit.Main;
import sun.plugin.services.WIExplorerBrowserService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by D3lyn on 2018/3/6.
 */
public class twoDCross {
    public class Runs {
        public int start;
        public int end;
        public Runs(int s, int e) {
            start = s;
            end = e;
        }
    }
    public int[] colors = {0x000055, 0x000044, 0x000099};
    public int[][] matrix;
    public Paillier p;
    public BigInteger[][] bmatrix;
    public int[][] labels;
    public int labb;
    public ArrayList<ArrayList<Runs>> runsPerCol;
    static public int VALID = 0;
    static public int INVALID = 255;
    static public BigInteger BVALID = new BigInteger("0");
    static public BigInteger BINVALID = new BigInteger("255");
    public twoDCross(int[][] input) throws Exception {
        matrix = input;
    }
    public twoDCross(BigInteger[][] input, Paillier pp) throws Exception {
        bmatrix = input;
        p = pp;
    }
    public twoDCross(BufferedImage input) throws Exception {
        matrix = Transformer.imageToIntArray(input);
    }
    public twoDCross(String fileName) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(fileName));
        matrix = Transformer.imageToIntArray(bimg);
    }
    public void init() {
        int height = matrix.length;
        int width = matrix[0].length;
        labels = new int[height][width];
        runsPerCol = new ArrayList<>();
        for (int ctr = 0; ctr < height; ctr++) {
            for (int itr = 0; itr < width; itr++)
                labels[ctr][itr] = 999999;
            runsPerCol.add(new ArrayList<Runs>());
        }
    }
    public void binit() {
        int height = bmatrix.length;
        int width = bmatrix[0].length;
        labels = new int[height][width];
        runsPerCol = new ArrayList<>();
        for (int ctr = 0; ctr < height; ctr++) {
            for (int itr = 0; itr < width; itr++)
                labels[ctr][itr] = 999999;
            runsPerCol.add(new ArrayList<Runs>());
        }
    }
    public void firstPass() throws Exception{
        for (int ctr = 0; ctr < matrix.length; ctr++) {
            for (int itr = 0; itr < matrix[0].length - 1; itr++) {
                if (matrix[ctr][itr] == VALID) {
                    int st = itr;
                    while (matrix[ctr][itr + 1] == VALID) itr++;
                    runsPerCol.get(ctr).add(new Runs(st, itr));
                }
            }
        }
        int label = 1;
        for (int ctr = 0; ctr < runsPerCol.get(0).size(); ctr++) {
            labelIt(0, runsPerCol.get(0).get(ctr).start, runsPerCol.get(0).get(ctr).end, label);
            label++;
        }
        for (int ctr = 1; ctr < matrix.length; ctr++) {
            for (int itr = 0; itr < runsPerCol.get(ctr).size(); itr++) {
                int minLabel = 999999;
                for (int j = runsPerCol.get(ctr).get(itr).start; j <= runsPerCol.get(ctr).get(itr).end; j++)
                    if (matrix[ctr - 1][j] == VALID && minLabel > labels[ctr - 1][j]) minLabel = labels[ctr - 1][j];//here
                if (minLabel != 999999) labelIt(ctr, runsPerCol.get(ctr).get(itr).start, runsPerCol.get(ctr).get(itr).end, minLabel);
                else {
                    labelIt(ctr, runsPerCol.get(ctr).get(itr).start, runsPerCol.get(ctr).get(itr).end, label);
                    label++;
                }
            }
        }
        labb = label;
        fixLabels();
    }
    public void bFirstPass() throws Exception{
        for (int ctr = 0; ctr < bmatrix.length; ctr++) {
            for (int itr = 0; itr < bmatrix[0].length - 1; itr++) {
                if (!Main.GcSubAndCompBigVer(bmatrix[ctr][itr], String.valueOf(VALID), p)){//matrix[ctr][itr] == VALID) {
                    int st = itr;
                    while (!Main.GcSubAndCompBigVer(bmatrix[ctr][itr + 1], String.valueOf(VALID), p)) itr++;
                    runsPerCol.get(ctr).add(new Runs(st, itr));
                }
            }
        }
        int label = 1;
        for (int ctr = 0; ctr < runsPerCol.get(0).size(); ctr++) {
            labelIt(0, runsPerCol.get(0).get(ctr).start, runsPerCol.get(0).get(ctr).end, label);
            label++;
        }
        for (int ctr = 1; ctr < bmatrix.length; ctr++) {
            for (int itr = 0; itr < runsPerCol.get(ctr).size(); itr++) {
                int minLabel = 999999;
                for (int j = runsPerCol.get(ctr).get(itr).start; j <= runsPerCol.get(ctr).get(itr).end; j++)
                    if (!Main.GcSubAndCompBigVer(bmatrix[ctr - 1][itr], String.valueOf(VALID), p)/*matrix[ctr - 1][j] == VALID*/ && minLabel > labels[ctr - 1][j]) minLabel = labels[ctr - 1][j];//here
                if (minLabel != 999999) labelIt(ctr, runsPerCol.get(ctr).get(itr).start, runsPerCol.get(ctr).get(itr).end, minLabel);
                else {
                    labelIt(ctr, runsPerCol.get(ctr).get(itr).start, runsPerCol.get(ctr).get(itr).end, label);
                    label++;
                }
            }
        }
        fixLabels();
    }
    public void fixLabels() {
        for (int ctr = 0; ctr < labels.length; ctr++)
            for (int itr = 0; itr < labels[0].length; itr++)
                if (labels[ctr][itr] == 999999) labels[ctr][itr] = 0;
    }
    public void labelIt(int row, int start, int end, int label) {
        for (int ctr = start; ctr <= end; ctr++)
            labels[row][ctr] = label;
    }
    public void showLabel() {
        //labels = matrix;
        for (int ctr = 0; ctr < labels.length; ctr++) {
            for (int itr = 0; itr < labels[0].length; itr++)
                System.out.print(labels[ctr][itr] + "  ");
            System.out.println("");
        }
    }
    public void showNumOfRunsPerRow() {
        for (int ctr = 0; ctr < matrix.length; ctr++)
            System.out.println("Runs of R" + ctr + ": " + runsPerCol.get(ctr).size());
    }
    public void drawColor(String fileName) throws Exception {
        int[][] res = new int[labels.length][labels[0].length];
        for (int ctr = 0; ctr < labels.length; ctr++)
            for (int itr = 0; itr < labels[0].length; itr++)
                res[ctr][itr] = labels[ctr][itr];
        for (int ctr = 0; ctr < labels.length; ctr++)
            for (int itr = 0; itr < labels[0].length; itr++)
                if (res[ctr][itr] > 1)
                    res[ctr][itr] = colors[labels[ctr][itr] % colors.length];

        //doIt(matrix, res);
        BG(res);
        Transformer.IntArrayToColorFulImage(res, fileName);
    }
    public void bDrawColor(String fileName) throws Exception {
        //BigInteger[][] res = new BigInteger[labels.length][labels[0].length];
        int[][] res = labels;
        for (int ctr = 0; ctr < labels.length; ctr++)
            for (int itr = 0; itr < labels[0].length; itr++)
                //res[ctr][itr] = p.encrypt(new BigInteger(String.valueOf(colors[labels[ctr][itr] % colors.length])));
                res[ctr][itr] = colors[labels[ctr][itr] % colors.length];
        Writer.writeImage(res, fileName);
    }
    public void BG(int[][] input) throws Exception {
        int max = 0, res = 0;
        ArrayList rrb = getYS();
        labb = rrb.size();/*
        for (int ctr = 0; ctr < labb; ctr++) {
            int temp = 0;
            for (int itr = 0; itr < labels.length; itr++) {
                for (int jtr = 0; jtr < labels[0].length; jtr++) {
                    if (labels[itr][jtr] == Integer.valueOf(rrb.get(ctr).toString())) temp++;
                }
            }
            System.out.println(temp + "!!!!!");
            if (max < temp) {
                max = temp;
                res = Integer.valueOf(rrb.get(ctr).toString());
            }
        }
        //get black
        System.out.println(res);
        */
        for (int ctr = 0; ctr < input.length; ctr++) {
            for (int itr = 0; itr < input[0].length; itr++)
                if (input[ctr][itr] == 1)
                    input[ctr][itr] = 0x000000;
        }
        for (int ctr = 0; ctr < input.length; ctr++) {
            for (int itr = 0; itr < input[0].length; itr++)
                if (matrix[ctr][itr] == 255)
                    input[ctr][itr] = 0xffffff;
        }
        //the edges of image
        for (int ctr = 0; ctr < input.length; ctr++) input[ctr][0] = input[ctr][input[0].length - 1] = 0x000000;
        for (int ctr = 0; ctr < input[0].length; ctr++) input[0][ctr] = input[input.length - 1][ctr] = 0x000000;
    }
    public ArrayList getYS() throws Exception {
        ArrayList res = new ArrayList();
        for (int ctr = 0; ctr < labels.length; ctr++) {
            for (int itr = 0; itr < labels[0].length; itr++) {
                if (res.isEmpty() || !res.contains(labels[ctr][itr])) res.add(labels[ctr][itr]);
            }
        }
        return res;
    }

    static public void main(String []args) throws Exception {
        int[][] temp = {{0, 1, 0, 1, 0, 1, 0, 0},
                        {0, 1, 0, 1, 1, 1, 1, 0},
                        {0, 1, 0, 0, 1, 0, 1, 0},
                        {0, 1, 0, 0, 0, 1, 1, 0},
                        {0, 1, 0, 1, 1, 1, 0, 0}};
        //twoDCross temp2D = new twoDCross(temp);
        Paillier pp = new Paillier(8);
        BigInteger[][] bTemp = {{new BigInteger("0"), new BigInteger("1"), new BigInteger("0")},
                                {new BigInteger("1"), new BigInteger("1"), new BigInteger("0")},
                                {new BigInteger("0"), new BigInteger("0"), new BigInteger("1")}};
        for (int ctr = 0; ctr < 3; ctr++)
            for (int itr = 0; itr < 3; itr++)
                bTemp[ctr][itr] = pp.encrypt(bTemp[ctr][itr]);
        twoDCross temp2D = new twoDCross("D:\\pic\\tttOf1.jpg");
        //twoDCross temp2D = new twoDCross(bTemp, pp);
        temp2D.init();
        temp2D.firstPass();
        //temp2D.showNumOfRunsPerRow();
        //temp2D.showLabel();
        temp2D.drawColor("D:\\pic\\res2018.jpg");
    }
}
