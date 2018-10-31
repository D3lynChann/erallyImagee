import HE.Paillier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;

public class Writer {
    //pure writer
    public static void pureWriter(int[][] matrix, String fileName) throws Exception {
        //matrix = toThree(matrix);
        File ImageFile = new File(fileName);
        //edge detection
        //GsTrans(matrix);
        BufferedImage buff;
        buff = Transformer.IntArrayToBinImage(matrix);
        ImageIO.write(buff, "bmp", ImageFile);
    }

    //pure writer
    public static void pureWriterP(int[][] matrix, String fileName) throws Exception {
        //matrix = toThree(matrix);
        File ImageFile = new File(fileName);
        //edge detection
        //GsTrans(matrix);
        BufferedImage buff;
        buff = Transformer.IntArrayToGreyImage(matrix);
        ImageIO.write(buff, "bmp", ImageFile);
    }

    //a good write image function, the main function
    public static void writeImage(int[][] matrix, String fileName) throws Exception {
        //matrix = toThree(matrix);
        File ImageFile = new File(fileName);
        //edge detection
        //matrix = HybridSystem.inPeices(matrix, 2);
        matrix = HybridSystem.inPeices2(matrix, 2);
        //matrix = DoTheImg.whitePic(matrix);
        //GsTrans(matrix);
        BufferedImage buff;
        buff = Transformer.IntArrayToBinImage(matrix);
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
        buff = Transformer.IntArrayToGreyImage(matrix);
        ImageIO.write(buff, "jpg", ImageFile);
    }

    //a write image function that we can see the temp
    public static void writeImage(int[][] matrix, String fileName1, String fileName2, int p) throws Exception {
        File ImageFile1 = new File(fileName1);
        //File ImageFile2 = new File(fileName2);
        BufferedImage buff1;//, buff2;
        int[][] temp = HybridSystem.inPeices(matrix, p);
        //NormalBinaryImage(matrix, 220);
        //GsTrans(temp);
        buff1 = Transformer.IntArrayToGreyImage(temp);
        ImageIO.write(buff1, "jpg", ImageFile1);
        //GsTrans(matrix);

        //hough(temp);
        Houghh.make(temp, fileName2);
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
        DoTheImg.cover(matrix, "D://rexs.jpg", pp);
        int width = matrix.length;
        int height = matrix[0].length;
        File ImageFile = new File(fileName);

        BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        buff = Transformer.BigIntArrayToGreyImage(matrix, pp);

        ImageIO.write(buff, "jpg", ImageFile);
    }

    //three tunnel color matrix to a picture
    public static void writeImage(int[][][] matrix, String fileName) throws Exception {
        matrix[0] = HybridSystem.inPeices(matrix[0], 1);
        matrix[1] = HybridSystem.inPeices(matrix[1], 1);
        matrix[2] = HybridSystem.inPeices(matrix[2], 1);
        File ImageFile = new File(fileName);
        BufferedImage buff;
        buff = Transformer.IntArrayToGreyImage(Transformer.combineTheThreeTurnalToAnGteartOne(matrix));
        ImageIO.write(buff, "jpg", ImageFile);
        buff = Transformer.IntArrayToGreyImage(matrix[0]);
        ImageFile = new File("D:\\r.jpg");
        ImageIO.write(buff, "jpg", ImageFile);
        buff = Transformer.IntArrayToGreyImage(matrix[1]);
        ImageFile = new File("D:\\g.jpg");
        ImageIO.write(buff, "jpg", ImageFile);
        buff = Transformer.IntArrayToGreyImage(matrix[2]);
        ImageFile = new File("D:\\b.jpg");
        ImageIO.write(buff, "jpg", ImageFile);
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

    //write integer matrix into a file
    public static void writeToFile(String path, int[][] input) throws Exception {
        FileWriter fw = new FileWriter(new File(path));
        BufferedWriter bw = new BufferedWriter(fw);
        for (int ctr = 0; ctr < input.length; ctr++)
            for (int itr = 0; itr < input[0].length; itr++)
                bw.write(input[ctr][itr] + " ");
        bw.close();
        fw.close();
    }

}
