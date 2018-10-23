import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.io.*;

public class Kmeans002 {
    //to computer the EuclideanDistance
    private static double EuDistance(double array1[], double array2[]) {
        double Dist = 0.0;
        for (int i = 0; i < array2.length; i++)
                Dist = Dist + (array1[i] - array2[i]) * (array1[i] - array2[i]);
        //return Math.sqrt(Dist);
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

    private static boolean IsEqual(int Array1[],int Array2[]){
        for(int i=0; i<Array1.length; i++)
            if(Array1[i]!=Array2[i])
                return false;
        return true;
    }

    //get the location of min element from the Array
    private static int MinLocation(double Array[]){
        int Location;
        double Min;
        Min=Array[0];
        Location=0;
        for(int i=1; i<Array.length; i++){
            if(Array[i]<Min){
                Location=i;
                Min=Array[i];
            }
        }
        return Location;

    }

    public static int[] Kmeans002(double Matrix[][], int row, int col,int ClusterNum){
        int[]  CenterId=new int[ClusterNum];//中心点id
        int[]  Cid=new int[row];
        int[]  oldCid=new int[row];
        int[]  NumOfEveryCluster=new int[ClusterNum];//每个聚类中元素的数目
        double[][]  ClusterCenter=new double[ClusterNum][col];//中心点的质心形成的矩阵
        double[]  CenterDist=new double[ClusterNum];
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
                    CenterDist[j]=EuDistance(Matrix[i], ClusterCenter[j] );
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
                    ClusterCenter[j][k]=0.0;
                    for(int i=0; i<row; i++){
                        if(Cid[i]==j){
                            ClusterCenter[j][k]=ClusterCenter[j][k]+Matrix[i][k];
                        }
                    }
                }
            }
            for(int j=0; j<ClusterNum; j++){
                for(int k=0; k<col; k++){
                    ClusterCenter[j][k]=ClusterCenter[j][k]/NumOfEveryCluster[j];
                }
            }
            Iter=Iter+1;
        }

        return Cid;
    }

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
                ctr++;
            }
        }
        return result;
    }

    public static double[][] getData1D(String path) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(path));
        return imageToIntArray1D(bimg);
    }

    public static void fit(int[] input, double[][] output, int clusNum) {
        for (int ctr = 0; ctr < input.length; ctr++)
            output[ctr][0] = output[ctr][1] = output[ctr][2] = 255.0 * input[ctr] / clusNum;
    }

    public static double[][] OneD2TwoD(double[][] input, int col) {
        double[][] res = new double[input.length / col][col];
        System.out.println(input.length / col);
        int ctr2 = 0;
        for (int ctr = 0; ctr < input.length / col; ctr++)
            for (int itr = 0; itr < col; itr++)
                res[ctr][itr] = input[ctr2++][0];
        return res;
    }

    public static int decideCol(String fileName) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(fileName));
        return bimg.getWidth();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("!!!mission start");
        int Matrix_row;
        int Matrix_col;
        int ClusterNum;
        int origin_col = 0;
        String fileName = "D:\\240808fbbf4354cefc7b8f2f373dc3d0.jpg";
        Matrix_col=2;
        ClusterNum=3;
        double[][]  Matrix = getData1D(fileName);
        origin_col = decideCol(fileName);
        Matrix_row = Matrix.length;


        int[]  List;

        List=Kmeans002(Matrix, Matrix_row, Matrix_col,ClusterNum);
        System.out.println("The result of clustering, value of No.i means the ith belong to the No.value cluster");
        //printArray(List);
        fit(List, Matrix, ClusterNum);
        System.out.println("writing!");
        Writer.writeImage(OneD2TwoD(Matrix, origin_col), "D:\\test123.jpg");
        System.runFinalization();
    }
}


	
