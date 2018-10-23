import HE.Paillier;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigInteger;

public class Transformer {
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
        DoTheImg.NormalBinaryImage(sourceArray, 127);
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

    //decrypt the big integer matrix to a double matrix
    public static double[][] intMatrixToDoubleMatrix(BigInteger[][] matrix, Paillier pp) throws Exception {
        HybridSystem.GsTrans(matrix, pp);
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

}
