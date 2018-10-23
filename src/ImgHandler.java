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

    //just a thought of PJ
    public static void pj_s_throught(String[] files) throws Exception {
        int[][] matrix1 = GetterAndReader.getBBBData(files[0]);
        int[][] matrix2 = GetterAndReader.getBBBData(files[1]);
        int[][] matrix3 = GetterAndReader.getBBBData(files[2]);
        int[][][] temp = {matrix1, matrix2, matrix3};
        Writer.writeImage(Transformer.subOfPictureThree(temp), files[3]);
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
        Writer.writeImage(GetterAndReader.getData("F:\\pic\\qwe.jpg"),"F:\\pic\\tttOf11.bmp");


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
