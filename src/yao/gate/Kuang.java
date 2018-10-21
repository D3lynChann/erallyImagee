package yao.gate;

/**
 * Created by Dlen on 2017/12/5.
 */
public class Kuang {
    public int x;
    public int y;
    public int dx;
    public int dy;
    public Kuang(int a, int b, int c, int d) {
        x = a;
        y  = b;
        dx = c;
        dy = d;
    }
    public void show() {
        System.out.println(x + " " + y + " " + dx + " " + dy);
    }
}
