/*								INDIAN SCHOOL OF MINES, DHANBAD.
This is the implementation of Hough-Transform as project by
1. Sajid Hussain, 2012JE0742
2. Avinash Bhagat, 2012JE0613
3. Deep Sah, 2012JE0634
4. Uttam Chaudry, 2012JE0823
5. Aditya Singh, 2012JE0604

Under the supervision of Dr.Shushanta Mukhopadhay, Asst Prof, CSE, ISM. Submitted on 5, May 2014.

Sample execution : HoughTransform image.png image1.png 1000 700 100(op)

*/

import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.*;
import java.awt.image.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

class Pointt {
    public int x;
    public int y;
    public Pointt(int a, int b) {
        this.x = a;
        this.y = b;
    }
}

class ImageData // The Data-Structure to store the image in a linear array and set of operations
{
    public final int[] dataArray; // Image is stored in this linear array.
    public final int width;
    public final int height;

    public ImageData(int width, int height/*theta, rho*/) //constructor of this class when given width and height
    {
      this(new int[width * height], width, height);
    }

    public ImageData(int[] dataArray, int width, int height) // constructor of this class when passed with a predefined ImageData
    {
      this.dataArray = dataArray;
      this.width = width;
      this.height = height;
    }

    public int get(int x, int y) // To get the pixel value at the coordinate (x,y)
    {  return dataArray[y * width + x];  }

    public void set(int x, int y, int value) // To set the pixel value at the co-ordinate (x,y)
    {  dataArray[y * width + x] = value;  }

    public void accumulate(int x, int y, int delta)// To increment the value of the pixel at co-ordinate (x,y) by a value delta
    {  set(x, y, get(x, y) + delta);  }

    public int getMax() // Returns the value of the largest pixel in the data array
    {
      int max = dataArray[0];
      for (int i = width * height - 1; i > 0; i--)
        if (dataArray[i] > max)
          max = dataArray[i];
      return max;
    }
}


public class Hough_Transform
{
  public static ImageData houghTransform(ImageData inputData, int thetaAxisSize, int rAxisSize, int minContrast)
  {
    int width = inputData.width;
    int height = inputData.height;
    int maxRadius = (int)Math.ceil(Math.hypot(width, height));
    int halfRAxisSize = rAxisSize >>> 1;
    ImageData outputData = new ImageData(thetaAxisSize, rAxisSize);
    // x output ranges from 0 to pi
    // y output ranges from -maxRadius to maxRadius
    double[] sinTable = new double[thetaAxisSize];
    double[] cosTable = new double[thetaAxisSize];
    for (int theta = thetaAxisSize - 1; theta >= 0; theta--)
    {
      double thetaRadians = theta * Math.PI / thetaAxisSize;
      sinTable[theta] = Math.sin(thetaRadians);
      cosTable[theta] = Math.cos(thetaRadians);
    }

    for (int y = height - 1; y >= 0; y--)
    {
      for (int x = width - 1; x >= 0; x--)
      {
        if (inputData.get(x, y) ==255)//inputData.contrast(x, y, minContrast))
        {
          for (int theta = thetaAxisSize - 1; theta >= 0; theta--)
          {
            double r = cosTable[theta] * x + sinTable[theta] * y;
            r = Math.abs(r);
            outputData.accumulate(theta, (int)r, 1);
          }
        }
      }
    }
    return outputData;
  }
    public static int[][] imageToBIntArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[width][height];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int rgb = image.getRGB(j, i);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int grey = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                result[j][i] = grey > 170 ? 255 : 0;
            }
        }
        return result;
    }

  public static ImageData getImageDataFromImage(String filename) throws IOException // To extract the image details into the ImageData after converting it into greyscale
  {
    BufferedImage inputImage = ImageIO.read(new File(filename));
    int width = inputImage.getWidth();
    int height = inputImage.getHeight();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ImageIO.write(inputImage,"jpg", out);
      int[][] temp = imageToBIntArray(inputImage);
      System.out.println(temp.length);
    ImageData arrayData = new ImageData(width, height);
    // Flip y axis when reading the image
    for (int y = 0; y < height; y++)
    {
      for (int x = 0; x < width; x++)
      {
        arrayData.set(x, y, temp[x][y]);
      }
    }
    return arrayData;
  }
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
  public static void writeOutputImage(String filename, ImageData arrayData, int[][] input) throws IOException // Draws the accumulator image on the screen
  {
      ArrayList<Pointt> Rs = new ArrayList<>();
    int max = arrayData.getMax();
      System.out.println(arrayData.height);
    int X = input.length, Y = input[0].length;
    for (int ctr = 0; ctr < arrayData.height; ctr++) {
        for (int itr = 0; itr < arrayData.width; itr++) {
            if (arrayData.get(itr, ctr) >=0.5* max) {
                Pointt tempP = new Pointt(itr, ctr);//theta, rho
                Rs.add(tempP);
            }
        }
    }
      for (int ctr = 0; ctr < Rs.size(); ctr++) {
          System.out.println(String.format("%.2f", (-(Rs.get(ctr).x / 45.0) + 1) / -2) + "pi " + (Rs.get(ctr).y));
          double k = Math.tan(((-(Rs.get(ctr).x / 45.0) + 1) / -2)*Math.PI), b = Rs.get(ctr).y / Math.cos(Math.abs((-(Rs.get(ctr).x / 45.0) + 1) / -2)*Math.PI);
          System.out.println("k: " + k  + " b: " + b);
          for (int itr = 0; itr < X; itr++) {
              int y = (int)(k * itr + b);
              if (y >= 0 && y < Y) input[itr][y] = 0xFF0000;
          }
      }

      BufferedImage outputImage1 = IntArrayToGreyImage(input);
      File ImageFile = new File("D:\\ououou.jpg");
      //GsTrans(matrix);
      ImageIO.write(outputImage1, "jpg", ImageFile);
    BufferedImage outputImage = new BufferedImage(arrayData.width, arrayData.height, BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < arrayData.height; y++)
    {
      for (int x = 0; x < arrayData.width; x++)
      {
        int n = Math.min((int)Math.round(arrayData.get(x, y) * 255.0 / max), 255);
        outputImage.setRGB(x, y, (n << 16) |(n << 8) |  -0x01000000);
      }
    }
    ImageIO.write(outputImage, "jpg", new File(filename));
    return;
  }

  public static void main(String[] args) throws IOException
  {
      BufferedImage inputImage = ImageIO.read(new File("D:\\tttt.jpg"));
      int temptemp[][] = imageToBIntArray(inputImage);
    ImageData inputData = getImageDataFromImage("D:\\tttt.jpg");
    int minContrast = (args.length <= 4) ? 64 : Integer.parseInt(args[4]);
    ImageData outputData = houghTransform(inputData, 90, (int)Math.sqrt(inputData.width*inputData.width + inputData.height*inputData.height), minContrast);
    writeOutputImage("D:\\output.jpg", outputData, temptemp);
    return;
  }
}
