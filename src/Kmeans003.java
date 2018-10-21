import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.io.*;
import java.math.BigInteger;
import java.rmi.server.ExportException;

import HE.*;

/**
 * Created by blister on 2018/5/7.
 */
public class Kmeans003 {
    //to computer the EuclideanDistance
    private static DDouble EuDistance(DDouble array1[], DDouble array2[], Paillier pp) throws Exception {
    DDouble Dist = new DDouble(0, new BigInteger("0"), pp);
    for (int i = 0; i < array2.length; i++)
            Dist = Dist.Padd( array1[i].Psub(array2[i]).Pmul(array1[i].Psub(array2[i])));
    return Dist;
    }

    private static int[] Randperm(int N,int M){
        double[]  PermF=new double[N];
        int[]     PermI=new int[N];
        int[]     RetArray=new int[M];
        double tempF;
        int    tempI;
        for(int i=0; i<N; i++){
            PermF[i]=Math.random();
            PermI[i]=i;
        }
        //sort choosing the big to forward
        for(int i=0; i<N-1; i++){
            for(int j=i+1; j<N; j++){
                if(PermF[i]<PermF[j]){
                    tempF=PermF[i];
                    tempI=PermI[i];
                    PermF[i]=PermF[j];
                    PermI[i]=PermI[j];
                    PermF[j]=tempF;
                    PermI[j]=tempI;
                }
            }
        }

        for(int i=0; i<M; i++){
            RetArray[i]=PermI[i];
        }
        return RetArray;
    }

    private static boolean IsEqual(BigInteger Array1[],BigInteger Array2[], Paillier pp) throws Exception {
        for(int i=0; i<Array1.length; i++)
            if(Paillier.isEq(Array1[i], Array2[i], pp) == false)
                return false;
        return true;
    }

    private static boolean IsEqual(int Array1[],int Array2[]){
        for(int i=0; i<Array1.length; i++)
            if(Array1[i]!=Array2[i])
                return false;
        return true;
    }

    //get the location of min element from the Array
    private static int MinLocation(DDouble Array[]) throws Exception {
        int Location;
        DDouble Min;
        Min=Array[0];
        Location=0;
        for(int i=1; i<Array.length; i++){
            if(Min.PLargerThan(Array[i]) == true){
                Location=i;
                Min=Array[i];
            }
        }
        return Location;

    }

    public static int[] Kmeans002(DDouble Matrix[][], int row, int col,int ClusterNum, Paillier pp) throws Exception {
        int[]  CenterId=new int[ClusterNum];//中心点id
        int[]  Cid=new int[row];
        int[]  oldCid=new int[row];
        int[]  NumOfEveryCluster=new int[ClusterNum];//每个聚类中元素的数目
        DDouble[][]  ClusterCenter=new DDouble[ClusterNum][col];//中心点的质心形成的矩阵
        DDouble[]  CenterDist=new DDouble[ClusterNum];
        CenterId=Randperm(row,ClusterNum);
        for(int i=0; i<ClusterNum; i++)
            for(int j=0; j<col; j++)
                ClusterCenter[i][j]=Matrix[ CenterId[i] ][j];
        for(int i=0; i<row; i++)
            oldCid[i]=1;
        int MaxIter=200;
        int Iter=1;


        while( !IsEqual(Cid,oldCid) || Iter<MaxIter){
            for(int i=0;i<row;i++){
                oldCid[i]=Cid[i];
            }
            for(int i=0;i<row;i++){
                for(int j=0; j<ClusterNum;j++){
                    CenterDist[j]=EuDistance(Matrix[i], ClusterCenter[j], pp );
                }
                Cid[i]=MinLocation(CenterDist);
            }

            for(int j=0; j<ClusterNum; j++){
                NumOfEveryCluster[j]=0;
                for(int i=0; i<row; i++){
                    if(Cid[i]==j){
                        NumOfEveryCluster[j]=NumOfEveryCluster[j]+1;
                    }
                }
            }

            for(int j=0; j<ClusterNum; j++){
                for(int k=0; k<col; k++){
                    ClusterCenter[j][k]=new DDouble(0, pp.encrypt(new BigInteger("0")), pp);
                    for(int i=0; i<row; i++){
                        if(Cid[i]==j){
                            ClusterCenter[j][k]=ClusterCenter[j][k].Padd(Matrix[i][k]);
                        }
                    }
                }
            }
            for(int j=0; j<ClusterNum; j++){
                for(int k=0; k<col; k++){
                    ClusterCenter[j][k]=ClusterCenter[j][k].Pdiv(NumOfEveryCluster[j]);
                }
            }
            Iter=Iter+1;
        }

        return Cid;
    }

    public static DDouble[][] imageToIntArray1D(BufferedImage image, Paillier pp) throws Exception {
        int width = image.getWidth();
        int height = image.getHeight();
        DDouble[][] result = new DDouble[width * height][3];
        int ctr = 0;
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int rgb = image.getRGB(j, i);

                result[ctr][0] = new DDouble(0, new BigInteger(String.valueOf((rgb >> 16) & 0xFF)), pp);
                result[ctr][1] = new DDouble(0, new BigInteger(String.valueOf((rgb >> 8) & 0xFF)), pp);
                result[ctr][2] = new DDouble(0, new BigInteger(String.valueOf(rgb & 0xFF)), pp);
                ctr++;
            }
        }
        return result;
    }

    public static DDouble[][] getData1D(String path, Paillier pp) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(path));
        //double[][] temp = {{12, 23, 34},{45, 56, 67},{12, 23, 34},{12, 45, 67},{56, 122, 70},{32, 51, 62},{15, 27, 49},{62, 73, 95},{11, 11, 11}};
        //DDouble[][] res = new DDouble[][]
        return imageToIntArray1D(bimg, pp);
    }

    public static void fit(int[] input, DDouble[][] output, int clusNum, Paillier pp) throws Exception {
        for (int ctr = 0; ctr < input.length; ctr++)
            output[ctr][0] = output[ctr][1] = output[ctr][2] = new DDouble(255.0 * input[ctr] / clusNum, pp);
    }

    public static double[][] OneD2TwoD(DDouble[][] input, int col) throws Exception {
        double[][] res = new double[input.length / col][col];
        System.out.println(input.length / col);
        int ctr2 = 0;
        for (int ctr = 0; ctr < input.length / col; ctr++)
            for (int itr = 0; itr < col; itr++)
                res[ctr][itr] = input[ctr2++][0].trans();
        return res;
    }

    public static int decideCol(String fileName) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(fileName));
        return bimg.getWidth();
    }

    public static void main(String[] args) throws Exception {
        int Matrix_row;
        int Matrix_col;
        int ClusterNum;
        int origin_col = 0;
        Paillier pp = new Paillier(32);
        String fileName = "D:\\tttt.jpg";
        Matrix_col=3;
        ClusterNum=4;
        DDouble[][]  Matrix = getData1D(fileName, pp);
        origin_col = decideCol(fileName);
        Matrix_row = Matrix.length;


        int[]  List;

        List=Kmeans002(Matrix, Matrix_row, Matrix_col,ClusterNum, pp);
        System.out.println("The result of clustering, value of No.i means the ith belong to the No.value cluster");
        //printArray(List);
        fit(List, Matrix, ClusterNum, pp);
        ImgHandler.writeImage(OneD2TwoD(Matrix, origin_col), "D:\\test12.jpg");
        System.runFinalization();
    }
}