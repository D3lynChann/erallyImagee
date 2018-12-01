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
import java.util.LinkedList;
import java.util.List;

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

    //just another thouht of PJ
    public static void pj_s_thought(String[] files) throws Exception {
        int[][] matrix1 = HybridSystem.inPeices(GetterAndReader.getData(files[0]), 1);
        int[][] matrix2 = HybridSystem.inPeices(GetterAndReader.getData(files[1]), 1);
        int[][] matrix3 = HybridSystem.inPeices(GetterAndReader.getData(files[2]), 1);
        int[][][] temp = {matrix1, matrix2, matrix3};
        Writer.pureWriter(matrix1, "F:\\pic\\pjs\\l1s.bmp");
        Writer.pureWriter(matrix2, "F:\\pic\\pjs\\l2s.bmp");
        Writer.pureWriter(matrix3, "F:\\pic\\pjs\\l3s.bmp");
        Writer.pureWriter(Transformer.subOfPictureThree(temp), files[3]);
    }

    //another test
    public static void pjs3(String file2, String file1, String file3) throws Exception {
        int[][] labels = GetterAndReader.getTwoDLabelf(file1);
        int[][] image = GetterAndReader.getDataP(file2);
        /*
        for (int ctr = 0; ctr < image.length; ctr++) {
            for (int itr = 0; itr < image[0].length; itr++) {
                System.out.print(image[ctr][itr] + " ");
            }
            System.out.println(" ");
        }
        */
        LinkedList ls = new LinkedList();
        for (int ctr = 0; ctr < labels.length; ctr++) {
            for (int itr = 0; itr < labels[0].length; itr++) {
                if (labels[ctr][itr] > 1)
                    ls.addLast(image[ctr][itr]);
            }
        }
        int[] valids = new int[ls.size()];
        for (int ctr = 0; ctr < valids.length; ctr++) {
            valids[ctr] = new Integer(ls.get(ctr).toString());
            //System.out.print(valids[ctr] + " ");
        }
        int[][] res = HybridSystem.inPeices3(image, valids, 2);
        Writer.pureWriter(res, file3);
    }

    //another test
    public static void pjs4(String file2, String file1, String file3) throws Exception {
        int[][] labels = GetterAndReader.getTwoDLabelf(file1);
        int[][] image = GetterAndReader.getDataPro(file2);
        /*
        for (int ctr = 0; ctr < image.length; ctr++) {
            for (int itr = 0; itr < image[0].length; itr++) {
                System.out.print(image[ctr][itr] + " ");
            }
            System.out.println(" ");
        }
        */
        LinkedList ls = new LinkedList();
        for (int ctr = 0; ctr < labels.length; ctr++) {
            for (int itr = 0; itr < labels[0].length; itr++) {
                if (labels[ctr][itr] > 1)
                    ls.addLast(image[ctr][itr]);
            }
        }
        int[] valids = new int[ls.size()];
        for (int ctr = 0; ctr < valids.length; ctr++) {
            valids[ctr] = new Integer(ls.get(ctr).toString());
            //System.out.print(valids[ctr] + " ");
        }
        int[][] res = HybridSystem.inPeices4(image, valids, 2);
        for (int ctr = 0; ctr < res.length; ctr++) {
            for (int itr = 0; itr < res[0].length; itr++) {
                res[ctr][itr] = (-1 * res[ctr][itr] - 1);
            }
        }
        Writer.pureWriter(res, file3);
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
        //Writer.writeImage(GetterAndReader.getDataP("F:\\pic\\pjs\\l1.jpg"),"F:\\pic\\pjs\\tttOf11.bmp");
        //String[] res = {"F:\\pic\\pjs\\l1.jpg", "F:\\pic\\pjs\\l2.jpg", "F:\\pic\\pjs\\l3.jpg", "F:\\pic\\pjs\\res.bmp",};
        //pj_s_thought(res);
        //System.out.println(new Color(255,255,255).getRGB());
        //DoTheImg.binaryTheImage("F:\\pic\\a.jpg", "F:\\pic\\ls\\b.bmp");
        //int[][] res = GetterAndReader.getData("F:\\pic\\Ls\\L2.jpg");
        //DoTheImg.print(res);
        //Houghh.GammaSch(res);
        //DoTheImg.print(res);
        //Writer.pureWriterP(res, "F:\\pic\\Ls\\L2r.bmp");
        //DoTheImg.combineTwoImage("F:\\pic\\ls\\gzz123.jpg", "F:\\pic\\ls\\gz2.jpg", "F:\\pic\\ls\\gzz1234.bmp");
        //DoTheImg.reverseBwImage("F:\\pic\\ls\\gz3revssaasddaasds.jpg", "F:\\pic\\ls\\gz3revdsfsdasdddasd.bmp");
        DoTheImg.colorThePpYGR("F:\\pic\\ls\\yellow.jpg", "F:\\pic\\ls\\green.jpg", "F:\\pic\\ls\\red.jpg", "F:\\pic\\ls\\three.bmp");
        //DoTheImg.removeShadow("F:\\pic\\ls\\a_o.jpg", "F:\\pic\\ls\\L1.jpg", "F:\\pic\\ls\\a_without_shadowk.bmp");
        //DoTheImg.getColorImage("F:\\pic\\ls\\L1.jpg", "F:\\pic\\ls\\akkk.jpg", "F:\\pic\\ls\\kmeansTergemp.bmp");
        //DoTheImg.getTheRestPp("F:\\pic\\ls\\L1.jpg", "F:\\pic\\ls\\gz1.jpg", "F:\\pic\\ls\\gz2.jpg", "F:\\pic\\ls\\gz3.jpg");
        //pjs3("F:\\pic\\pjs\\l2.jpg", "F:\\pic\\pjs\\l1.jpg", "F:\\pic\\pjs\\res2.bmp");
        //pjs3("F:\\pic\\four.jpg", "F:\\pic\\pjs\\res1.jpg", "F:\\pic\\pjs\\res2.jpg");
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
