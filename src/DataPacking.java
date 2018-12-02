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
        for (int ctr = 0; ctr < 20; ctr++) {
            System.out.print("{");
            for (int itr = 0; itr < 20; itr++)
                System.out.print(((int)(random() * 255)) + ", ");
            System.out.println("},");
        }
        System.out.println("};");
*/
        int tt[][] = new int[][] {
                {19, 149, 123, 165, 128, 73, 162, 136, 88, 202, 62, 45, 108, 208, 130, 67, 89, 182, 40, 63, },
                {177, 150, 14, 116, 1, 248, 191, 233, 158, 5, 144, 229, 117, 45, 79, 168, 203, 96, 73, 186, },
                {141, 230, 235, 133, 226, 176, 78, 137, 161, 242, 252, 201, 170, 97, 116, 101, 114, 18, 62, 83, },
                {190, 129, 253, 147, 20, 181, 139, 210, 128, 166, 231, 183, 24, 114, 248, 134, 19, 236, 251, 207, },
                {98, 43, 250, 63, 126, 159, 12, 70, 110, 47, 160, 182, 73, 105, 6, 217, 229, 194, 43, 136, },
                {33, 43, 172, 80, 205, 227, 138, 87, 103, 221, 62, 5, 181, 36, 113, 244, 177, 11, 72, 225, },
                {161, 0, 28, 134, 153, 203, 106, 82, 165, 8, 7, 15, 109, 91, 221, 53, 99, 184, 17, 200, },
                {155, 167, 235, 230, 222, 231, 220, 66, 224, 115, 182, 222, 101, 16, 110, 40, 160, 235, 251, 135, },
                {116, 249, 70, 32, 234, 198, 176, 58, 222, 111, 140, 55, 21, 90, 162, 151, 148, 12, 1, 188, },
                {235, 45, 127, 156, 213, 89, 190, 123, 220, 250, 230, 36, 187, 176, 27, 196, 169, 46, 110, 206, },
                {71, 177, 22, 183, 120, 72, 190, 105, 123, 56, 56, 166, 89, 183, 34, 77, 36, 21, 198, 161, },
                {148, 21, 62, 101, 76, 14, 176, 107, 85, 205, 204, 195, 151, 206, 161, 120, 138, 165, 127, 238, },
                {233, 224, 14, 174, 7, 173, 112, 93, 31, 212, 53, 4, 76, 125, 203, 200, 192, 32, 102, 63, },
                {119, 230, 140, 71, 235, 107, 4, 112, 65, 98, 123, 74, 231, 83, 18, 226, 16, 235, 209, 12, },
                {16, 206, 86, 156, 133, 23, 27, 250, 33, 194, 65, 190, 5, 142, 84, 35, 250, 120, 54, 63, },
                {35, 68, 72, 189, 114, 245, 236, 230, 243, 0, 182, 79, 195, 119, 244, 124, 175, 32, 201, 4, },
                {191, 133, 162, 145, 37, 181, 210, 63, 190, 96, 223, 88, 219, 66, 107, 121, 250, 199, 180, 110, },
                {3, 208, 96, 239, 222, 79, 132, 146, 107, 48, 15, 190, 92, 202, 62, 163, 58, 116, 24, 93, },
                {105, 127, 24, 103, 27, 33, 132, 168, 42, 41, 135, 242, 7, 142, 213, 234, 110, 249, 128, 179, },
                {249, 112, 144, 128, 136, 47, 76, 123, 46, 153, 52, 28, 151, 213, 107, 252, 166, 78, 190, 199, },
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

    private static BigInteger[][] getJudge(BigInteger[][] matrix) throws Exception {
        BigInteger[][] temp = getJudge(0, 1);
        BigInteger[][] res = new BigInteger[temp.length][temp[0].length];
        for (int ctr = 0; ctr < res.length; ctr++)
            for (int itr = 0; itr < res[0].length; itr++)
                res[ctr][itr] = temp[ctr][itr].add(matrix[ctr][itr]);
        return res;
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
        System.out.println("compRes:");
        show(compRes);
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

    private static BigInteger[][] sobelIt(BigInteger[][] matrix) throws Exception {
        BigInteger[][] s1_as = copyMatrix(matrix);
        BigInteger[][] s1_bs = copyMatrix(matrix);
        BigInteger[][] s2_as = copyMatrix(matrix);
        BigInteger[][] s2_bs = copyMatrix(matrix);
        BigInteger[][] res = copyMatrix(matrix);
        for (int ctr = 1; ctr < res.length - 1; ctr++) {
            for (int itr = 1; itr < res[0].length - 1; itr++) {
                BigInteger s1_a = new BigInteger("0");
                BigInteger s1_b = new BigInteger("0");
                BigInteger s2_a = new BigInteger("0");
                BigInteger s2_b = new BigInteger("0");
                s1_a = s1_a.add(matrix[ctr - 1][itr + 1]).add(matrix[ctr][itr + 1])
                           .add(matrix[ctr][itr + 1]).add(matrix[ctr + 1][itr + 1]);
                s1_b = s1_b.add(matrix[ctr - 1][itr - 1]).add(matrix[ctr][itr - 1])
                        .add(matrix[ctr][itr - 1]).add(matrix[ctr + 1][itr - 1]);
                s2_a = s2_a.add(matrix[ctr - 1][itr - 1]).add(matrix[ctr - 1][itr])
                        .add(matrix[ctr - 1][itr]).add(matrix[ctr - 1][itr + 1]);
                s2_b = s2_b.add(matrix[ctr + 1][itr - 1]).add(matrix[ctr + 1][itr])
                        .add(matrix[ctr + 1][itr]).add(matrix[ctr + 1][itr + 1]);
                s1_as[ctr][itr] = s1_a;
                s1_bs[ctr][itr] = s1_b;
                s2_as[ctr][itr] = s2_a;
                s2_bs[ctr][itr] = s2_b;
            }
        }
        BigInteger[][] resS1 = comp(getJudge(s1_as), s1_bs);
        BigInteger[][] resS2 = comp(getJudge(s2_as), s2_bs);
        /*
        System.out.println("s1_as:");
        show(s1_as);
        System.out.println("s1_bs:");
        show(s1_bs);
        System.out.println("s2_as:");
        show(s2_as);
        System.out.println("s2_bs:");
        show(s2_bs);
        System.out.println("resS1:");
        show(resS1);
        System.out.println("resS2:");
        show(resS2);
        */
        for (int ctr = 0; ctr < res.length; ctr++)
            for (int itr = 0; itr < res[0].length; itr++)
                res[ctr][itr] = absSub(s1_as[ctr][itr], s1_bs[ctr][itr], resS1[ctr][itr])
                              .add(absSub(s2_as[ctr][itr], s2_bs[ctr][itr], resS2[ctr][itr]));
        //System.out.println("res:");
        //show(res);
        return res;
    }

    private static BigInteger absSub(BigInteger a, BigInteger b, BigInteger compRes) throws Exception {
        BigInteger res = new BigInteger("0");
        int power = 0;
        while (compRes.toString() != "0") {
            BigInteger now = compRes.mod(new BigInteger("10"));
            compRes = compRes.divide(new BigInteger("10"));
            BigInteger nowA = a.mod(new BigInteger(step));
            BigInteger nowB = b.mod(new BigInteger(step));
            a = a.divide(new BigInteger(step));
            b = b.divide(new BigInteger(step));
            //System.out.println("____________________");
            //System.out.println("now: " + now.toString());
            //System.out.println("nowA: " + nowA.toString());
            //System.out.println("nowB: " + nowB.toString());
            BigInteger rr;
            if (now.toString().equals("2"))
                 rr = new BigInteger(step).pow(power).multiply(nowA.subtract(nowB));
            else rr = new BigInteger(step).pow(power).multiply(nowB.subtract(nowA));
            //System.out.println("rr:" + rr.toString());
            res = res.add(rr);
            power += 1;
        }
        res = res.add(new BigInteger(step).pow(power));
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
        BigInteger img[][] = initialImg(1000000, 20, 20, 2, 2);
        BigInteger packed[][] = packTheImg(img);
        //System.out.println("packed:");
        //show(packed);
        BigInteger res[][] = Gaussian(packed);
        //System.out.println("Gs filter:");
        //show(res);
        //System.out.println("a: " + res[0][0].toString() + "\nb: " + res[1][0].toString());
        //System.out.print("r: ");
        //System.out.println(absSub(res[0][0], res[1][0], new BigInteger("2111")));
        int thres = getThreshold(127, copyMatrix(res), 16, 3);
        BigInteger binRes[][] = binarization(thres, 16, res);
        BigInteger sRes[][] = sobelIt(binRes);
        System.out.println("SobelRes:");
        show(sRes);
    }

}
