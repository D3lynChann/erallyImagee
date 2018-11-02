import HE.Paillier;
import circuit.Main;
import yao.gate.Kuang;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;

public class DoTheImg {

    //just a test
    public static void testBinImage(String fileName, String output) throws Exception {
        int[][] res = GetterAndReader.getBBBData(fileName);
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


    //give the picture a white edge
    public static int[][] whitePic(int[][] input) {
        int h = input.length, w = input[0].length;
        for (int ctr = 0; ctr < h; ctr++) input[ctr][0] = input[ctr][w - 1] = 255;
        for (int ctr = 0; ctr < w; ctr++) input[0][ctr] = input[h - 1][ctr] = 255;
        return input;
    };

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
        return Transformer.imageToIntArray(Transformer.toBufferedImage(Itemp));
    }

    //zoom a matrix to a size
    public static int[][] zoomImage(int[][] data,int w,int h) throws Exception {
        double wr=0,hr=0;
        BufferedImage bufImg = Transformer.IntArrayToGreyImage(data);
        Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);//设置缩放目标图片模板

        wr=w*1.0/bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bufImg, null);
        //ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        return Transformer.imageToIntArray(Transformer.toBufferedImage(Itemp));
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
        return Transformer.imageToBigIntArray(Transformer.toBufferedImage(Itemp), pp);
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

    //binary an image with a threshold T
    public static void NormalBinaryImagePro(int[][] matrix, int T) throws Exception {
        int width = matrix.length;
        int height = matrix[0].length;
        for (int ctr = 0; ctr < width; ctr++)
            for (int itr = 0; itr < height; itr++) {
                if (matrix[ctr][itr] > T) matrix[ctr][itr] = 16777215;
                else matrix[ctr][itr] = 0;
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


    //test whether the matrix is two value
    public static void totoallyBinThePic(int[][] input) {
        int h = input.length, w = input[0].length;
        int temp = 0;
        for (int ctr = 0; ctr < h; ctr++)
            for (int itr = 0; itr < w; itr++)
                if (input[ctr][itr] > 0 && input[ctr][itr] < 255) temp++;
        System.out.println(temp);
    }

    //binary the image in a special way
    public static void binaryTheImage(String file1, String file2) throws Exception {
        int[][] temp = GetterAndReader.getData(file1);
        for (int ctr = 0; ctr < temp.length; ctr++) {
            for (int itr = 0; itr < temp[0].length; itr++) {
                //System.out.print(temp[ctr][itr] + " ");
                if (temp[ctr][itr] < 220) temp[ctr][itr] = 0;
            }
        }
        Writer.pureWriter(temp, file2);
    }

    //square the image, file1 is the two-value picture, file2 is colorful picture, file3 is res
    public static void getColorImage(String file1, String file2, String file3) throws Exception {
        int[][] temp = GetterAndReader.getData(file1);
        int[][] color = GetterAndReader.getData(file2);
        ArrayList place = new ArrayList();
        for (int ctr = 0; ctr < temp.length; ctr++)
            for (int itr = 0; itr < temp[0].length; itr++)
                if(temp[ctr][itr] != 255)
                    place.add(color[ctr][itr]);
        int[][] res = new int[(int)(Math.sqrt(place.size()))][(int)(Math.sqrt(place.size()))];
        int co = 0;
        for (int ctr = 0; ctr < (int)(Math.sqrt(place.size())); ctr++)
            for (int itr = 0; itr < (int)(Math.sqrt(place.size())); itr++)
                res[ctr][itr] = Integer.valueOf(place.get(co++).toString());
        Writer.pureWriterP(res, file3);
    }

    //print the matrix
    public static void print(int[][] matrix) {
        for (int ctr = 0; ctr < matrix.length; ctr++) {
            for (int itr = 0; itr < matrix[0].length; itr++)
                System.out.print(matrix[ctr][itr] + " ");
            System.out.println(" ");
        }
        return ;
    }

    //combine two image
    public static void combineTwoImage(String file1, String file2, String file3) throws Exception {
        int[][] origin = GetterAndReader.getData(file1);
        int[][] place = GetterAndReader.getData(file2);
        int[][] res = new int[origin.length][origin[0].length];
        for (int ctr = 0; ctr < origin.length; ctr++) {
            for (int itr = 0; itr < origin[0].length; itr++) {
                if (origin[ctr][itr] == 255 && place[ctr][itr] == 255)
                    res[ctr][itr] = 255;
                else res[ctr][itr] = 0;
            }
        }

        for (int ctr = 0; ctr < res.length; ctr++) {
            for (int itr = 0; itr < res[0].length; itr++) {
                if (res[ctr][itr] == 0 && place[ctr][itr] == 255)
                    res[ctr][itr] = 0;
                else res[ctr][itr] = 255;
            }
        }
        Writer.pureWriter(res, file3);
    }

    //get the rest pp
    public static void getTheRestPp(String file1, String file2, String file3, String file4) throws Exception {
        int[][] origin = GetterAndReader.getData(file1);
        int[][] place1 = GetterAndReader.getData(file2);
        int[][] place2 = GetterAndReader.getData(file1);
        int[][] res = new int[origin.length][origin[0].length];
        for (int ctr = 0; ctr < origin.length; ctr++) {
            for (int itr = 0; itr < origin[0].length; itr++) {
                if (origin[ctr][itr] == 255 && place1[ctr][itr] == 255 && place2[ctr][itr] == 255)
                    res[ctr][itr] = 255;
                else res[ctr][itr] = 0;
            }
        }
        /*
        for (int ctr = 0; ctr < res.length; ctr++) {
            for (int itr = 0; itr < res[0].length; itr++) {
                if (res[ctr][itr] == 0 && place1[ctr][itr] == 255 || res[ctr][itr] == 0 && place2[ctr][itr] == 255)
                    res[ctr][itr] = 0;
                else res[ctr][itr] = 255;
            }
        }
        */
        Writer.pureWriter(res, file4);
    }

    //reverse the image
    public static void reverseBwImage(String file1, String file2) throws Exception {
        int[][] origin = GetterAndReader.getData(file1);
        for (int ctr = 0; ctr < origin.length; ctr++)
            for (int itr = 0; itr < origin[0].length; itr++)
                origin[ctr][itr] = origin[ctr][itr] == 255 ? 0 : 255;
        Writer.pureWriter(origin, file2);
    }

    //get the shadow out
    public static void removeShadow(String file1, String file2, String file3) throws Exception {
        int[][] origin = GetterAndReader.getDataP(file1);
        int[][] place = GetterAndReader.getData(file2);
        int[][] res = new int[origin.length][origin[0].length];
        for (int ctr = 0; ctr < res.length; ctr++)
            for (int itr = 0; itr < res[0].length; itr++)
                res[ctr][itr] = (place[ctr][itr] == 0) ? origin[ctr][itr] : 255;
        Writer.pureWriterP(res, file3);
    }
}
