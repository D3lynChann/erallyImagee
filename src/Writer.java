import HE.Paillier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigInteger;

public class Writer {

    //a good write image function, the main function
    public static void writeImage(int[][] matrix, String fileName) throws Exception {
        //matrix = toThree(matrix);
        File ImageFile = new File(fileName);
        //edge detection
        matrix = ImgHandler.inPeices(matrix, 2);
        matrix = ImgHandler.whitePic(matrix);
        //GsTrans(matrix);
        BufferedImage buff;
        buff = ImgHandler.IntArrayToBinImage(matrix);
        ImageIO.write(buff, "bmp", ImageFile);
        //2-d
        /*
        BufferedImage buff;
        buff = IntArrayToBinImage(matrix);
        ImageIO.write(buff, "jpg", ImageFile);
        */
/*
        File ImageFile = new File(fileName);
        BufferedImage buff;
        GsTrans(matrix); // 高斯其
        NormalBinaryImage(matrix, iterationGetThreshold(matrix)); //迭代二值化
        matrix = SobelIt(matrix); //Sobel其
        matrix = LplsIt(matrix);
        GsTrans(matrix); // 高斯其
        buff = IntArrayToGreyImage(matrix);
        ImageIO.write(buff, "jpg", ImageFile);
*/
    }

    //write image func. double version
    public static void writeImage(double[][] matrix, String fileName) throws Exception {
        File ImageFile = new File(fileName);
        BufferedImage buff;
        buff = ImgHandler.IntArrayToGreyImage(matrix);
        ImageIO.write(buff, "jpg", ImageFile);
    }

    //a write image function that we can see the temp
    public static void writeImage(int[][] matrix, String fileName1, String fileName2, int p) throws Exception {
        File ImageFile1 = new File(fileName1);
        //File ImageFile2 = new File(fileName2);
        BufferedImage buff1;//, buff2;
        int[][] temp = ImgHandler.inPeices(matrix, p);
        //NormalBinaryImage(matrix, 220);
        //GsTrans(temp);
        buff1 = ImgHandler.IntArrayToGreyImage(temp);
        ImageIO.write(buff1, "jpg", ImageFile1);
        //GsTrans(matrix);

        //hough(temp);
        ImgHandler.make(temp, fileName2);
        System.out.println("!!!");
        //buff2 = IntArrayToGreyImage(temp);
        //ImageIO.write(buff2, "jpg", ImageFile2);


        /*
        File ImageFile1 = new File(fileName1);
        File ImageFile2 = new File(fileName2);
        BufferedImage buff1, buff2;
        GsTrans(matrix); // 高斯其
        buff1 = IntArrayToGreyImage(matrix);
        ImageIO.write(buff1, "jpg", ImageFile1);
        NormalBinaryImage(matrix, iterationGetThreshold(matrix)); //迭代二值化
        matrix = SobelIt(matrix); //Sobel其
        matrix = LplsIt(matrix);
        GsTrans(matrix); // 高斯其
        buff2 = IntArrayToGreyImage(matrix);
        ImageIO.write(buff2, "jpg", ImageFile2);
*/
    }

    //a write image func. big integer version
    public static void writeImage(BigInteger[][] matrix, Paillier pp, String fileName) throws Exception {
        //GsTrans(matrix, pp);
        //NormalBinaryImage(matrix, 160, pp);
        ImgHandler.cover(matrix, "D://rexs.jpg", pp);
        int width = matrix.length;
        int height = matrix[0].length;
        File ImageFile = new File(fileName);

        BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        buff = ImgHandler.BigIntArrayToGreyImage(matrix, pp);

        ImageIO.write(buff, "jpg", ImageFile);
    }

}
