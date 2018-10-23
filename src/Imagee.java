import java.awt.image.BufferedImage;
import java.util.ArrayList;

import circuit.h;

/**
 * Created by blister on 2018/1/24.
 */
public class Imagee{
	int aa;
    public int h; //高
    public int w; //宽
    public int[] data; //像素
    public boolean gray; //是否为灰度图像

    public Imagee(BufferedImage img){

        this.h = img.getHeight();
        this.w = img.getWidth();

        this.data = img.getRGB(0, 0, w, h, null, 0, w);
        this.gray = false;
        toGray(); //灰度化
    }

    public Imagee(BufferedImage img,int gray){

        this.h = img.getHeight();
        this.w = img.getWidth();
        this.data = img.getRGB(0, 0, w, h, null, 0, w);
        this.gray = false;

    }


    public Imagee(int[] data,int h,int w){
        this.data = (data == null) ? new int[w*h]:data;
        this.h = h;
        this.w = w;
        this.gray = false;
    }

    public Imagee(int h,int w){
        this(null,h,w);
    }

    public BufferedImage toImage(){
        BufferedImage image = new BufferedImage(this.w, this.h, BufferedImage.TYPE_INT_ARGB);
        int[] d= new int[w*h];
        for(int i=0;i<this.h;i++){
            for(int j=0;j<this.w;j++){
                if(this.gray){
                    d[j+i*this.w] = (255<<24)|(data[j+i*this.w]<<16)|(data[j+i*this.w]<<8)|(data[j+i*this.w]);
                }else{
                    d[j+i*this.w] = data[j+i*this.w];
                }
            }
        }
        image.setRGB( 0, 0, w, h, d, 0, w );
        return image;
    }

    public void toGray(){

        if(!gray){
            this.gray = true;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int c = this.data[x + y * w];
                    int R = (c >> 16) & 0xFF;
                    int G = (c >> 8) & 0xFF;
                     int B = (c >> 0) & 0xFF;
                    this.data[x + y * w] = (int)(0.3f*R + 0.59f*G + 0.11f*B); //to gray
                }
            }
        }
    }

    public void hough(){
        Imagee im = this;
        //im.sobel();
        //im.IterBinary();

        int ro = (int)Math.sqrt(h*h+w*w);
        int theta = 180;
        int[][] hist = new int[ro][theta];

        for(int k=0;k<theta;k++){
            for(int i=0;i<h;i++){
                for(int j=0;j<w;j++){
                    if(im.data[j+i*w] != 0){
                        int rho=(int)(j*Math.cos(k*Math.PI/(theta*2))+i*Math.sin(k*Math.PI/(theta*2)));
                        hist[rho][k]++;
                    }
                }
            }
        }
        ArrayList<h> index = ImgHandler.getArrayListOfHist(hist, 0.70, true); //找到大于最大值*0.7的二维直方图的点

        for(int k =0;k<index.size();k++){

            double resTheta = index.get(k).angle*Math.PI/(theta*2);

            for(int i=0;i<h;i++){
                for(int j=0;j<w;j++){
                    int rho = (int)(j*Math.cos(resTheta)+i*Math.sin(resTheta));
                    if(im.data[j+i*w] != 0 && rho == index.get(k).ro){
                        data[j+i*w] = 90; //在直线上的点设为红色
                    }else{
                        data[j+i*this.w] = (255<<24)|(data[j+i*this.w]<<16)|(data[j+i*this.w]<<8)|(data[j+i*this.w]);
                    }
                }
            }
        }

        this.gray = false;
    }

}


