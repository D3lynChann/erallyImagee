import HE.Paillier;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

import circuit.Main;
import yao.gate.Kuang;
import circuit.h;

/**
 * Created by Dlen on 2017/11/29.
 */
public class ImgHandler {
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

    //turn sourceArray into BufferedImage
    public static BufferedImage IntArrayToGreyImage(int[][] sourceArray) {
        int width = sourceArray[0].length;
        int height = sourceArray.length;
        BufferedImage targetImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int greyRGB = sourceArray[i][j];
                int rgb = (greyRGB << 16) | (greyRGB << 8) | greyRGB;
                targetImage.setRGB(i, j, rgb);
            }
        }
        return targetImage;
    }

    //turn matrix into 1D array
    public static int[] matrixToArray(int[][] matrix) {
        int h = matrix.length, w = matrix[0].length;
        int[] res = new int[h * w];
        int co = 0;
        for (int itr = 0; itr < w; itr++)
            for (int ctr = 0; ctr < h; ctr++)
                res[co++] = (byte)(matrix[ctr][itr]);
        return res;
    }

    //turn matrix into an image
    public static void IntArrayToColorFulImage(int[][] sourceArray, String path) throws Exception{
        BufferedImage image1 = new BufferedImage(sourceArray.length, sourceArray[0].length, BufferedImage.TYPE_INT_RGB);
        image1.setRGB(0, 0, sourceArray.length, sourceArray[0].length, matrixToArray(sourceArray), 0, sourceArray.length);
        File ImageFile = new File(path);
        ImageIO.write(image1, "jpg", ImageFile);
        System.out.println("Make Picture success,Please find image in " + path);
    }

    //turn matrix into binary BufferedImage
    public static BufferedImage IntArrayToBinImage(int[][] sourceArray) throws Exception {
        NormalBinaryImage(sourceArray, 127);
        int width = sourceArray[0].length;
        int height = sourceArray.length;
        BufferedImage targetImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        for (int j = 0; j < width; j++)
            for (int i = 0; i < height; i++)
                targetImage.setRGB(i, j, sourceArray[i][j]);
        return targetImage;
    }

    //turn matrix into grey BufferedImage
    public static BufferedImage IntArrayToGreyImage(double[][] sourceArray) {
        int width = sourceArray[0].length;
        int height = sourceArray.length;
        BufferedImage targetImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int greyRGB = (int)(sourceArray[i][j]);
                int rgb = (greyRGB << 16) | (greyRGB << 8) | greyRGB;
                targetImage.setRGB(i, j, rgb);
            }
        }
        return targetImage;
    }

    //turn sourceArray into BufferedImage in HE
    public static BufferedImage BigIntArrayToGreyImage(BigInteger[][] sourceArray, Paillier pp) throws Exception {
        int width = sourceArray[0].length;
        int height = sourceArray.length;
        BufferedImage targetImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);

        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int greyRGB = Integer.valueOf(pp.decrypt(sourceArray[i][j]).toString());
                int rgb = (greyRGB << 16) | (greyRGB << 8) | greyRGB;
                targetImage.setRGB(i, j, rgb);
            }
        }

        return targetImage;
    }

    //turn a buffered image into a 1-d array in tree color turnel
    public static double[][] imageToIntArray1D(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] result = new double[width * height][3];
        int ctr = 0;
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int rgb = image.getRGB(j, i);

                result[ctr][0] = (rgb >> 16) & 0xFF;
                result[ctr][1] = (rgb >> 8) & 0xFF;
                result[ctr][2] = rgb & 0xFF;
            }
        }
        return result;
    }

    //turn an image into int Array
    public static int[][] imageToIntArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[width][height];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int rgb = image.getRGB(j, i);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                //at here !!!

                int grey = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                result[j][i] = grey;
            }
        }
        return result;
    }

    //turn a buffered image into a 2-d array in tree color turnel
    public static int[][][] imageToIntArrayInThreeTurnalSuchAsRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] result = new int[3][width][height];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int rgb = image.getRGB(j, i);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                result[0][j][i] = r;
                result[1][j][i] = g;
                result[2][j][i] = b;
            }
        }
        return result;
    }

    //turn a three color tunnel image into a binary image
    public static int[][] combineTheThreeTurnalToAnGteartOne(int[][][] input) {
        int threshold = 127;
        //use and gate
        int[][] res = new int[input[0].length][input[0][0].length];
        for (int ctr = 0; ctr < input[0].length; ctr++)
            for (int itr = 0; itr < input[0][0].length; itr++)
                if (input[0][ctr][itr] < threshold && input[1][ctr][itr] < threshold && input[2][ctr][itr] < threshold)
                    res[ctr][itr] = 0;
                else res[ctr][itr] = 255;
        return res;
    }

    //subsection of three image
    public static int[][] subOfPictureThree(int[][][] inputs) {
        int[][] res = new int[inputs[0].length][inputs[0][0].length];
        for (int ctr = 0; ctr < inputs[0].length; ctr++)
            for (int itr = 0; itr < inputs[0][0].length; itr++)
                if (inputs[0][ctr][itr] - inputs[1][ctr][itr] >= -10 &&
                        inputs[0][ctr][itr] - inputs[1][ctr][itr] <= 10 ||
                        inputs[0][ctr][itr] - inputs[2][ctr][itr] >= -10 &&
                        inputs[0][ctr][itr] - inputs[2][ctr][itr] <= 10 )
                    res[ctr][itr] = 0;
                else res[ctr][itr] = 255;
        return res;
    }

    //just a thought of PJ
    public static void pj_s_throught(String[] files) throws Exception {
        int[][] matrix1 = getBBBData(files[0]);
        int[][] matrix2 = getBBBData(files[1]);
        int[][] matrix3 = getBBBData(files[2]);
        int[][][] temp = {matrix1, matrix2, matrix3};
        Writer.writeImage(subOfPictureThree(temp), files[3]);
    }

    //turn an image into a BigInt Array
    public static BigInteger[][] imageToBigIntArray(BufferedImage image, Paillier pp) throws Exception {
        int width = image.getWidth();
        int height = image.getHeight();

        BigInteger[][] result = new BigInteger[width][height];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int rgb = image.getRGB(j, i);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int grey = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                result[j][i] = pp.encrypt(new BigInteger(String.valueOf(grey)));
            }
        }
        //writeBigToFile("D://dontTouch.txt", result);
        return result;
    }

    //turn an image into a matrix
    public static int[][] getData(String path) throws Exception {
            BufferedImage bimg = ImageIO.read(new File(path));
            return imageToIntArray(bimg);
    }

    //use the function show below
    public static int[][] getBBBData(String path) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(path));
        return imageToIntArrayBBBBBBBBBBBB(bimg);
    }

    //turn an image into a two-value matrix
    public static int[][] imageToIntArrayBBBBBBBBBBBB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int threshold = 127;
        int[][] result = new int[width][height];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int rgb = image.getRGB(j, i);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                //at here !!!

                int grey = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                result[j][i] = (grey < threshold) ? 0 : 255;
            }
        }
        return result;
    }

    //just a test
    public static void testBinImage(String fileName, String output) throws Exception {
        int[][] res = getBBBData(fileName);
        int width = res[0].length;
        int height = res.length;
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                System.out.print(res[i][j] + " ");
            }
            System.out.println("");
        }
        BufferedImage targetImage = new BufferedImage(height, width, BufferedImage.TYPE_BYTE_BINARY);
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int rgb = new Color(0,0,0).getRGB();
                if (res[i][j] > 127) rgb = new Color(255,255,255).getRGB();
                targetImage.setRGB(i, j, rgb);
            }
        }
        File ImageFile = new File(output);
        ImageIO.write(targetImage, "bmp", ImageFile);
    }

    //a use of function imageToIntArrayInThreeTurnalSuchAsRGB
    public static int[][][] getData3333(String path) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(path));
        return imageToIntArrayInThreeTurnalSuchAsRGB(bimg);
    }

    //a use of function imageToIntArray1D
    public static double[][] getData1D(String path) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(path));
        return imageToIntArray1D(bimg);
    }

    //write big integer matrix into a file
    public static void writeBigToFile(String path, BigInteger[][] input) throws Exception {
        FileWriter fw = new FileWriter(new File(path));
        BufferedWriter bw = new BufferedWriter(fw);
        for (int ctr = 0; ctr < input.length; ctr++)
            for (int itr = 0; itr < input[0].length; itr++)
                bw.write(input[ctr][itr].toString() + "\t\n");
        bw.close();
        fw.close();
    }

    //get a big integer matrix from a picture
    public static BigInteger[][] readBigFromFile(String path, int wid, int hei, Paillier pp) throws Exception {
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        String[] arrs = null;
        BigInteger[][] res = new BigInteger[wid][hei];
        for (int ctr = 0; ctr < wid; ctr++)
            for (int itr = 0; itr < hei; itr++)
                res[ctr][itr] = new BigInteger(br.readLine());
        br.close();
        fr.close();
        return res;
    }

    //as above, get a big integer matrix
    public static BigInteger[][] getBData(String path, Paillier pp) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(path));
        return imageToBigIntArray(bimg, pp);
    }

    //three tunnel color matrix to a picture
    public static void writeImage(int[][][] matrix, String fileName) throws Exception {
        matrix[0] = inPeices(matrix[0], 1);
        matrix[1] = inPeices(matrix[1], 1);
        matrix[2] = inPeices(matrix[2], 1);
        File ImageFile = new File(fileName);
        BufferedImage buff;
        buff = IntArrayToGreyImage(combineTheThreeTurnalToAnGteartOne(matrix));
        ImageIO.write(buff, "jpg", ImageFile);
        buff = IntArrayToGreyImage(matrix[0]);
        ImageFile = new File("D:\\r.jpg");
        ImageIO.write(buff, "jpg", ImageFile);
        buff = IntArrayToGreyImage(matrix[1]);
        ImageFile = new File("D:\\g.jpg");
        ImageIO.write(buff, "jpg", ImageFile);
        buff = IntArrayToGreyImage(matrix[2]);
        ImageFile = new File("D:\\b.jpg");
        ImageIO.write(buff, "jpg", ImageFile);
    }


    //give the picture a white edge
    public static int[][] whitePic(int[][] input) {
        int h = input.length, w = input[0].length;
        for (int ctr = 0; ctr < h; ctr++) input[ctr][0] = input[ctr][w-1]=255;
        for (int ctr = 0; ctr < w; ctr++) input[0][ctr] = input[h-1][ctr] = 255;
        return input;
    }


    //decrypt the big integer matrix to a double matrix
    public static double[][] intMatrixToDoubleMatrix(BigInteger[][] matrix, Paillier pp) throws Exception {
        GsTrans(matrix, pp);
        int width = matrix.length;
        int height = matrix[0].length;
        double[][] res = new double[width][height];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int greyRGB = Integer.valueOf(pp.decrypt(matrix[i][j]).toString());
                int rgb = (greyRGB << 16) | (greyRGB << 8) | greyRGB;
                res[j][i] = (double)greyRGB;
            }
        }
        return res;
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

    //turn image to bufferedImage
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        //boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
           /* if (hasAlpha) {
             transparency = Transparency.BITMASK;
             }*/

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            //int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
            /*if (hasAlpha) {
             type = BufferedImage.TYPE_INT_ARGB;
             }*/
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    //zoom an image to a size
    public static int[][] zoomImage(String src,int w,int h) throws Exception {
        double wr=0,hr=0;
        File srcFile = new File(src);

        BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
        Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);//设置缩放目标图片模板

        wr=w*1.0/bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bufImg, null);
        //ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        return imageToIntArray(toBufferedImage(Itemp));
    }

    //zoom a matrix to a size
    public static int[][] zoomImage(int[][] data,int w,int h) throws Exception {
        double wr=0,hr=0;
        BufferedImage bufImg = IntArrayToGreyImage(data);
        Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);//设置缩放目标图片模板

        wr=w*1.0/bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bufImg, null);
        //ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        return imageToIntArray(toBufferedImage(Itemp));
    }

    //zoom an image to a size big integer version
    public static BigInteger[][] zoomImage(String src, int w, int h, Paillier pp) throws Exception {
        double wr=0,hr=0;
        File srcFile = new File(src);

        BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
        Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);//设置缩放目标图片模板

        wr=w*1.0/bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bufImg, null);
        return imageToBigIntArray(toBufferedImage(Itemp), pp);
    }

    //oh no, i forgot it
    public static boolean detectMini(int[][] matrix, int x, int y) {
        for (int ctr = 1; ctr < 20; ctr++)
            if (Math.abs(matrix[x + ctr][y] - matrix[x][y]) > 20) return false;
        for (int ctr = 1; ctr < 20; ctr++)
            if (Math.abs(matrix[x][y + ctr] - matrix[x][y]) > 20) return false;
        for (int ctr = 1; ctr < 20; ctr++)
            if (Math.abs(matrix[x + ctr][y + ctr] - matrix[x][y]) < 20) return false;
        return true;
    }

    //detectMini big integer version
    public static boolean detectMini(BigInteger[][] matrix, int x, int y, Paillier pp) throws Exception {
        for (int ctr = 1; ctr < 20; ctr++)
            if (Main.GcSubAndCompBigVer(Main.absSub(matrix[x + ctr][y], matrix[x][y], pp), "20", pp)) return false;
        for (int ctr = 1; ctr < 20; ctr++)
            if (Main.GcSubAndCompBigVer(Main.absSub(matrix[x][y + ctr], matrix[x][y], pp), "20", pp)) return false;
        for (int ctr = 1; ctr < 20; ctr++)
            if (Main.GcSubAndCompBigVer(Main.absSub(matrix[x + ctr][y + ctr], matrix[x][y], pp), "20", pp) == false) return false;
        return true;
    }

    //oh no, i forgot it
    public static int[] detectDx(int[][] matrix, int x, int y) {
        int[] res = new int[2];
        int xx;
        int yy;
        for (xx = 0; x + xx < matrix.length - 21; xx++)
            if (Math.abs(matrix[x + xx][y] - matrix[x][y]) < 20 && Math.abs(matrix[x + xx + 1][y] - matrix[x][y]) > 20)
                break;
        for (yy = 0; y + yy < matrix[0].length - 21; yy++)
            if (Math.abs(matrix[x][y + yy] - matrix[x][y]) < 20 && Math.abs(matrix[x][y + yy + 1] - matrix[x][y]) > 20)
                break;
        res[0] = xx;
        res[1] = yy;
        return res;
    }

    //detectDx big integer version
    public static int[] detectDx(BigInteger[][] matrix, int x, int y, Paillier pp) throws Exception {
        int[] res = new int[2];
        int xx, yy;
        for (xx = 0; xx < matrix.length - 21; xx++)
            if (Main.GcSubAndCompBigVer(Main.absSub(matrix[x + xx][y], matrix[x][y], pp), "20", pp) == false &&
                    Main.GcSubAndCompBigVer(Main.absSub(matrix[x + xx + 1][y], matrix[x][y], pp), "20", pp))
                        break;
        for (yy = 0; yy < matrix.length - 21; yy++)
            if (Main.GcSubAndCompBigVer(Main.absSub(matrix[x][y + yy], matrix[x][y], pp), "20", pp) == false &&
                    Main.GcSubAndCompBigVer(Main.absSub(matrix[x][y + yy + 1], matrix[x][y], pp), "20", pp))
                break;
        res[0] = xx; res[1] = yy;
        return res;
    }

    //oh no, i forgot it
    public static ArrayList<Kuang> detect(int[][] matrix) throws Exception {
        ArrayList<Kuang> res = new ArrayList<Kuang>();
        int w = matrix.length;
        int h = matrix[0].length;
        for (int ctr = 0; ctr < w - 20; ctr++)
            for (int itr = 0; itr < h - 20; itr++) {
                if (detectMini(matrix, ctr, itr)) {
                    res.add(new Kuang(ctr, itr, detectDx(matrix, ctr, itr)[0], detectDx(matrix, ctr, itr)[1]));
                }
            }
        return res;
    }

    //detect big integer version
    public static ArrayList<Kuang> detect(BigInteger[][] matrix, Paillier pp) throws Exception {
        ArrayList<Kuang> res = new ArrayList<Kuang>();
        int w = matrix.length, h = matrix[0].length;
        for (int ctr = 0; ctr < w - 20; ctr++)
            for (int itr = 0; itr < h - 20; itr++) {
                if (detectMini(matrix, ctr, itr, pp)) {
                    int[] temp = detectDx(matrix, ctr, itr, pp);
                    res.add(new Kuang(ctr, itr, temp[0], temp[1]));
                }
            }
        return res;
    }

    //cover a picture
    public static void cover(int[][] matrix, String path) throws Exception {
        ArrayList<Kuang> inputK = detect(matrix);
        for (int all = 0; all < inputK.size(); all++) {
            inputK.get(all).show();
            int[][] temp = zoomImage(path, inputK.get(all).dx + 3, inputK.get(all).dy + 3);
            for (int ctr = 0; ctr < inputK.get(all).dx + 3; ctr++)
                for (int itr = 0; itr < inputK.get(all).dy + 3; itr++) {
                    matrix[inputK.get(all).x + ctr][inputK.get(all).y + itr] = temp[ctr][itr];
                }
        }
    }

    //cover func. big integer version
    public static void cover(BigInteger[][] matrix, String path, Paillier pp) throws Exception {
        ArrayList<Kuang> inputK = detect(matrix, pp);
        for (int all = 0; all < inputK.size(); all++) {
            inputK.get(all).show();
            BigInteger[][] temp = zoomImage(path, inputK.get(all).dx + 3, inputK.get(all).dy + 3, pp);
            for (int ctr = 0; ctr < inputK.get(all).dx + 3; ctr++)
                for (int itr = 0; itr < inputK.get(all).dy + 3; itr++) {
                    matrix[inputK.get(all).x + ctr][inputK.get(all).y + itr] = temp[ctr][itr];
                }
        }
    }

    //binary an image with a threshold T
    public static void NormalBinaryImage(int[][] matrix, int T) throws Exception {
        int width = matrix.length;
        int height = matrix[0].length;
        for (int ctr = 0; ctr < width; ctr++)
            for (int itr = 0; itr < height; itr++) {
                if (matrix[ctr][itr] > T) matrix[ctr][itr] = new Color(255,255,255).getRGB();
                else matrix[ctr][itr] = new Color(0,0,0).getRGB();
            }
    }

    //NormalBinaryImage func. big integer version
    public static void NormalBinaryImage(BigInteger[][] matrix, int T, Paillier pp) throws Exception {
        BigInteger black = pp.encrypt(new BigInteger("16777215"));
        BigInteger white = pp.encrypt(new BigInteger("0"));
        int width = matrix.length;
        int height = matrix[0].length;
        for (int ctr = 0; ctr < width; ctr++)
            for (int itr = 0; itr < height; itr++) {
                if (Main.GcSubAndCompBigVer(matrix[ctr][itr], String.valueOf(T), pp)) matrix[ctr][itr] = black;
                else matrix[ctr][itr] = white;
                System.out.println((ctr * width + itr) + "of" + ((width + 1) * (height + 1)));
            }
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
                NormalBinaryImage(temppMatrix[ctr][itr], iterationGetThreshold(temppMatrix[ctr][itr]));
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

    //test whether the matrix is two value
    public static void totoallyBinThePic(int[][] input) {
        int h = input.length, w = input[0].length;
        int temp = 0;
        for (int ctr = 0; ctr < h; ctr++)
            for (int itr = 0; itr < w; itr++)
                if (input[ctr][itr] > 0 && input[ctr][itr] < 255) temp++;
        System.out.println(temp);
    }

    public static void main(String []args) throws Exception {
        //Paillier pp = new Paillier(64);
        //System.out.println("Paillier加密的module的平方是: "+pp.getNsquare()+"\n-----------------------");
        //pp.generateKeys();
        //zoomImage("D:\\xxx.jpg", "D:\\asdasd.jpg", 400, 1120);
        //writeImage(getData("D:\\用于图像分割的测试图像\\1.jpg"), "D:\\用于图像分割的测试图像\\11.jpg", "D:\\final7.jpg");                  //normal version
        //writeImage(getData("D:\\用于图像分割的测试图像\\2.jpg"), "D:\\用于图像分割的测试图像\\22.jpg", "D:\\final7.jpg");
        //writeImage(getData("D:\\用于图像分割的测试图像\\5.jpg"), "D:\\用于图像分割的测试图像\\55.jpg", "D:\\final7.jpg");
        //writeImage(getData("D:\\five.jpg"), "D:\\66.jpg", "D:\\final7.jpg");

        //testBinImage("D:\\pic\\test.jpg", "D:\\pic\\asd.bmp");
        Writer.writeImage(getData("F:\\pic\\11.jpg"),"F:\\pic\\tttOf11.bmp");


        //writeImage(getBBBData("D:\\ff\\2.jpg"),"D:\\ff\\resOf2.jpg");
        //writeImage(getBBBData("D:\\ff\\3.jpg"),"D:\\ff\\resOf3.jpg");
        //writeImage(getBBBData("D:\\ff\\res1.jpg"),"D:\\ff\\resOfres1.jpg");
        //String[] fn = {"D:\\ff\\resOf1.jpg", "D:\\ff\\resOf2.jpg", "D:\\ff\\resOf3.jpg", "D:\\ff\\resooooooooo1.jpg"};
        //pj_s_throught(fn);
        //System.out.println(Math.atan(100000000));
        //writeImage(getData("D:\\用于图像分割的测试图像\\7.jpg"), "D:\\用于图像分割的测试图像\\77.jpg", "D:\\final7.jpg");
        //writeImage(getData("D:\\用于图像分割的测试图像\\4.jpg"), "D:\\用于图像分割的测试图像\\44.jpg", "D:\\final7.jpg");
        //writeImage(getBData("D:\\xl.jpg", pp), pp, "D:\\res1l160v.jpg");        //Paillier version
    }
}
