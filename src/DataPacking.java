import HE.Paillier;

import java.math.BigInteger;

import static java.lang.Math.random;

public class DataPacking {
    private static int oriH, oriW, pieceH, pieceW;
    private static String step;
    public static Paillier pp;
    private static BigInteger[][] initialImg(int stp, int oH, int oW, int pH, int pW) throws Exception {
        oriH = oH; oriW = oW; pieceH = pH; pieceW = pW;
        step = String.valueOf(stp);
        pp = new Paillier(1024);
        BigInteger img[][] = new BigInteger[oriH][oriW];
        /*
        System.out.println("{");
        for (int ctr = 0; ctr < 10; ctr++) {
            System.out.print("{");
            for (int itr = 0; itr < 10; itr++)
                System.out.print(((int)(random() * 255)) + ", ");
            System.out.println("},");
        }
        System.out.println("};");
        */
        int tt[][] = new int[][] {
                {40, 201, 64, 31, 214, 81, 165, 36, 159, 70, },
                {177, 164, 142, 196, 36, 109, 41, 217, 174, 171, },
                {36, 248, 97, 239, 14, 79, 187, 91, 223, 233, },
                {25, 112, 251, 134, 115, 47, 188, 17, 21, 1, },
                {66, 46, 65, 161, 236, 213, 110, 169, 149, 44, },
                {55, 167, 52, 175, 205, 156, 153, 203, 78, 160, },
                {106, 55, 7, 222, 138, 14, 127, 171, 202, 154, },
                {169, 83, 210, 174, 143, 129, 194, 46, 121, 125, },
                {104, 192, 116, 67, 105, 22, 222, 132, 44, 243, },
                {168, 226, 231, 67, 182, 66, 188, 221, 229, 20, },
        };
        for (int ctr = 0; ctr < oriH; ctr++)
            for (int itr = 0; itr < oriW; itr++)
                img[ctr][itr] = new BigInteger(String.valueOf(tt[ctr][itr]));
        return img;
    }

    private static BigInteger[][] packTheImg(BigInteger[][] matrix) throws Exception {
        BigInteger packedImg[][][] = new BigInteger[pieceH * pieceW][oriH / pieceH][oriW / pieceW];
        for (int ctr = 0; ctr < oriH; ctr++)
            for (int itr = 0; itr < oriW; itr++)
                packedImg[ctr / (oriW / pieceW) * pieceW + itr / (oriH / pieceH)][ctr % (oriH / pieceH)][itr % (oriW / pieceW)] =
                        matrix[ctr][itr];
        BigInteger packed[][] = new BigInteger[oriH / pieceH][oriW / pieceW];
        for (int i = 0; i < packed.length; i++)
            for (int j = 0; j < packed[0].length; j++) {
                BigInteger temp = new BigInteger("0");
                for (int k = 0; k < pieceH * pieceW; k++) {
                    BigInteger rr = new BigInteger(step).pow(k).multiply(packedImg[k][i][j]);
                    temp = temp.add(rr);
                }
                temp = temp.add(new BigInteger(step).pow(pieceH * pieceW));
                packed[i][j] = temp;
            }
        return packed;
    }

    private static BigInteger[][] getJudge(int target, int scale) throws Exception {
        BigInteger judge[][] = new BigInteger[oriH / pieceH][oriW / pieceW];
        int st = Integer.valueOf(step) / 10;
        target = st / scale + target;
        for (int i = 0; i < judge.length; i++)
            for (int j = 0; j < judge[0].length; j++) {
                BigInteger temp = new BigInteger("0");
                for (int k = 0; k < pieceH * pieceW; k++) {
                    BigInteger rr = new BigInteger(step).pow(k).multiply(new BigInteger(String.valueOf(target)));
                    temp = temp.add(rr);
                }
                temp = temp.add(new BigInteger(step).pow(pieceH * pieceW));
                temp = temp.multiply(new BigInteger(String.valueOf(scale)));
                judge[i][j] = temp;
            }
        return judge;
    }

    private static BigInteger[][] Gaussian(BigInteger[][] matrix) throws Exception {
        BigInteger res[][] = new BigInteger[oriH / pieceH][oriW / pieceW];
        for (int ctr = 0; ctr < res.length; ctr++)
            for (int itr = 0; itr < res[0].length; itr++)
                res[ctr][itr] = matrix[ctr][itr].multiply(new BigInteger("16"));
        int sii = 1;
        for (int i = sii; i < matrix.length - sii; i++) {
            for (int j = sii;  j < matrix[0].length - sii; j++) {
                BigInteger temp = new BigInteger("0");
                temp = temp.add(matrix[i][j].multiply(new BigInteger("4")));
                temp = temp.add(matrix[i - 1][j - 1]);
                temp = temp.add(matrix[i + 1][j - 1]);
                temp = temp.add(matrix[i + 1][j + 1]);
                temp = temp.add(matrix[i - 1][j + 1]);
                temp = temp.add(matrix[i][j - 1].multiply(new BigInteger("2")));
                temp = temp.add(matrix[i][j + 1].multiply(new BigInteger("2")));
                temp = temp.add(matrix[i - 1][j].multiply(new BigInteger("2")));
                temp = temp.add(matrix[i + 1][j].multiply(new BigInteger("2")));
                res[i][j] = temp;
            }
        }
        return res;
    }

    //judge, target
    public static BigInteger[][] comp(BigInteger[][] a, BigInteger[][] b) throws Exception {
        BigInteger subRes[][] = new BigInteger[oriH / pieceH][oriW / pieceW];
        BigInteger judgeRes[][] = new BigInteger[oriH / pieceH][oriW / pieceW];
        for (int ctr = 0; ctr < subRes.length; ctr++)
            for (int itr = 0; itr < subRes[0].length; itr++)
                subRes[ctr][itr] = a[ctr][itr].subtract(b[ctr][itr]);
        for (int ctr = 0; ctr < judgeRes.length; ctr++) {
            for (int itr = 0; itr < judgeRes[0].length; itr++) {
                BigInteger temp = new BigInteger("0"), now = subRes[ctr][itr];
                for (int jtr = 0; jtr < pieceH * pieceW; jtr++) {
                    now = now.divide(new BigInteger(step.substring(0, step.length() - 1)));
                    temp = temp.multiply(new BigInteger("10"));
                    temp = temp.add(now.mod(new BigInteger("10")).add(new BigInteger("1")));
                    now = now.divide(new BigInteger("10"));
                }
                judgeRes[ctr][itr] = temp;
            }
        }
        for (int ctr = 0; ctr < judgeRes.length; ctr++)
            for (int itr = 0; itr < judgeRes[0].length; itr++)
                judgeRes[ctr][itr] = new BigInteger(new StringBuilder(judgeRes[ctr][itr].toString()).reverse().toString());
        return judgeRes;
    }

    private static void show(BigInteger[][] matrix) {
        for (BigInteger[] line: matrix) {
            for (BigInteger item: line)
                System.out.print(item + "'\t");
            System.out.println(" ");
        }
    }

    private static void showInFormat(BigInteger[][] matrix) {
        for (BigInteger[] line: matrix) {
            for (BigInteger item: line)
                System.out.format("%40s", item + " ");
            System.out.println(" ");
        }
    }

    private static int getThreshold(int thres0, BigInteger[][] packedImg, int scale, int delta) throws Exception {
        BigInteger[][] judge = getJudge(thres0, scale);
        BigInteger[][] compRes = comp(judge, packedImg);
        BigInteger oriPack[][] = new BigInteger[packedImg.length][packedImg[0].length];
        BigInteger sumFront = new BigInteger("0"), sumBack = new BigInteger("0");
        BigInteger numFront = new BigInteger("0"), numBack = new BigInteger("0");
        for (int ctr = 0; ctr < compRes.length; ctr++) {
            for (int itr = 0; itr < compRes[0].length; itr++) {
                oriPack[ctr][itr] = packedImg[ctr][itr];
                for (int jtr = 0; jtr < pieceW * pieceH; jtr++) {
                    BigInteger now = compRes[ctr][itr].mod(new BigInteger("10")).subtract(new BigInteger("1"));
                    compRes[ctr][itr] = compRes[ctr][itr].divide(new BigInteger("10"));
                    BigInteger now2 = packedImg[ctr][itr].mod(new BigInteger(step));
                    packedImg[ctr][itr] = packedImg[ctr][itr].divide(new BigInteger(step));
                    if (now.toString().equals("1")) {
                        sumFront = sumFront.add(now2);
                        numFront = numFront.add(new BigInteger("1"));
                    }
                    else {
                        numBack = numBack.add(new BigInteger("1"));
                        sumBack = sumBack.add(now2);
                    }
                }
            }
        }
        /*
        System.out.println("sumFront:" + sumFront);
        System.out.println("sumBack:" + sumBack);
        System.out.println("numFront:" + numFront);
        System.out.println("numBack:" + numBack);
        */
        BigInteger thresFront = sumFront.divide(numFront);
        BigInteger thresBack = sumBack.divide(numBack);
        int newThres = Integer.valueOf((thresFront.add(thresBack)).divide(new BigInteger("2")).toString()) / scale;
        //System.out.println(newThres);
        if (Math.abs(thres0 - newThres) < delta) return newThres;
        else return getThreshold(newThres, oriPack, scale, delta);
    }

    private static BigInteger[][] binarization(int threshold, int scale, BigInteger[][] matrix) throws Exception {
        BigInteger[][] judge = getJudge(threshold, scale);
        BigInteger[][] compRes = comp(judge, matrix);
        /*
        System.out.println("matrix:");
        show(matrix);
        */
        System.out.println("compRes:");
        show(compRes);
        /*
        System.out.println("judge:");
        show(judge);
        */
        BigInteger res[][] = new BigInteger[matrix.length][matrix[0].length];
        for (int ctr = 0; ctr < res.length; ctr++)
            for (int itr = 0; itr < res[0].length; itr++) {
                BigInteger temp = new BigInteger("0");
                for (int k = 0; k < pieceH * pieceW; k++) {
                    BigInteger now = compRes[ctr][itr].mod(new BigInteger("10")).subtract(new BigInteger("1"));
                    compRes[ctr][itr] = compRes[ctr][itr].divide(new BigInteger("10"));
                    BigInteger rr = new BigInteger(step).pow(k).multiply(now).multiply(new BigInteger("255"));
                    temp = temp.add(rr);
                }
                temp = temp.add(new BigInteger(step).pow(pieceH * pieceW));
                res[ctr][itr] = temp;
            }
        return res;
    }

    private static BigInteger[][] copyMatrix(BigInteger[][] matrix) throws Exception {
        BigInteger res[][] = new BigInteger[matrix.length][matrix[0].length];
        for (int ctr = 0; ctr < matrix.length; ctr++)
            for (int itr = 0; itr < matrix[0].length; itr++)
                res[ctr][itr] = matrix[ctr][itr];
        return res;
    }

    public static void main(String[] args) throws Exception {
        BigInteger img[][] = initialImg(1000000, 10, 10, 2, 2);
        BigInteger packed[][] = packTheImg(img);
        //System.out.println("packed:");
        //show(packed);
        BigInteger res[][] = Gaussian(packed);
        //System.out.println("Gs filter:");
        //show(res);
        int thres = getThreshold(127, copyMatrix(res), 16, 3);
        BigInteger binRes[][] = binarization(thres, 16, res);
        System.out.println("binRes:");
        show(binRes);
    }

}
