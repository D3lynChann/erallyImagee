import HE.Paillier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;

public class GetterAndReader {

    //turn an image into a matrix
    public static int[][] getData(String path) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(path));
        return Transformer.imageToIntArray(bimg);
    }

    //use the function show below
    public static int[][] getBBBData(String path) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(path));
        return Transformer.imageToIntArrayBBBBBBBBBBBB(bimg);
    }

    //a use of function imageToIntArrayInThreeTurnalSuchAsRGB
    public static int[][][] getData3333(String path) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(path));
        return Transformer.imageToIntArrayInThreeTurnalSuchAsRGB(bimg);
    }

    //a use of function imageToIntArray1D
    public static double[][] getData1D(String path) throws Exception {
        BufferedImage bimg = ImageIO.read(new File(path));
        return Transformer.imageToIntArray1D(bimg);
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
        return Transformer.imageToBigIntArray(bimg, pp);
    }

}
