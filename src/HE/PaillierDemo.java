package HE;

import java.math.BigInteger;

/**
 * Created by Pepper on 2016/4/5.
 */
public class PaillierDemo {
    public static void main(String[] args) {
        long startTime=System.currentTimeMillis();   //获取开始时间
        try {
            Paillier pp = new Paillier(64);
            BigInteger a = pp.encrypt(new BigInteger("120"));
            BigInteger b = pp.encrypt(new BigInteger("50"));
            DDouble aa = new DDouble(2, a, pp);
            DDouble bb = new DDouble(1, b, pp);
            aa.Pmul(bb).show();
            pp.generateKeys();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
