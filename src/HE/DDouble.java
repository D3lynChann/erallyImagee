package HE;

import circuit.Main;
import jdk.nashorn.internal.runtime.ECMAException;

import java.math.BigInteger;

/**
 * Created by blister on 2018/5/7.
 */
public class DDouble {
    public int b;
    public BigInteger t;
    public Paillier pp;
    public DDouble(int base, BigInteger text) {
        b = base; t = text; pp = null;
    }
    public DDouble(int base, BigInteger text, Paillier ppa) {
        b = base; t = text; pp = ppa;
    }
    public DDouble(double input, Paillier p) {
        int base = 0;
        while (((int)input) * 1.0 != input) {
            base++;
            input *= 10;
        }
        b = base;
        t = new BigInteger(String.valueOf((int)input));
        pp = p;
    }
    public void show() throws Exception {
        if (pp == null) {
            double mt = new Double(t.toString()) / Math.pow(10, b);
            System.out.println(mt);
        }
        else {
            double mt = new Double(pp.decrypt(t).toString()) / Math.pow(10, b);
            System.out.println(mt);
        }
    }
    public double trans() throws Exception {
        double mt = 0;
        if (pp == null) {
            mt = new Double(t.toString()) / Math.pow(10, b);
        }
        else {
            mt = new Double(pp.decrypt(t).toString()) / Math.pow(10, b);
        }
        return mt;
    }
    public DDouble add(DDouble bin) {
        if (b < bin.b) return new DDouble(bin.b, t.multiply((new BigInteger("10")).pow(bin.b - b)).add(bin.t));
        return new DDouble(b, bin.t.multiply((new BigInteger("10")).pow(b - bin.b)).add(t));
    }
    public DDouble mul(DDouble bin) {
        return new DDouble(b + bin.b, t.multiply(bin.t));
    }
    public DDouble Padd(DDouble bin) throws Exception {
        if (b < bin.b) return new DDouble(bin.b, Paillier.addBig(Paillier.mulBig(t, (new BigInteger("10")).pow(bin.b - b), pp), bin.t, pp), pp);
        return new DDouble(b, Paillier.addBig(Paillier.mulBig(bin.t, (new BigInteger("10")).pow(b - bin.b), pp), t, pp), pp);
    }
    public DDouble Pmul(DDouble bin) throws Exception {
        return new DDouble(this.trans() * bin.trans(), this.pp);
        //return new DDouble(b + bin.b, Paillier.PTimes(t, bin.t, pp), pp);
    }
    public DDouble Psub(DDouble bin) throws Exception {
        if (this.trans() < bin.trans()) return bin.Psub(this);
        if (b < bin.b) return new DDouble(bin.b, Paillier.subBig(Paillier.mulBig(t, (new BigInteger("10")).pow(bin.b - b), pp), bin.t, pp), pp);
        return new DDouble(b, Paillier.subBig(t, bin.t, pp), pp);
    }
    public DDouble Pdiv(DDouble bin) throws Exception {
        return new DDouble(b - bin.b + 7, pp.encrypt(pp.decrypt(t).multiply(new BigInteger("10000000")).divide(pp.decrypt(bin.t))), pp);
    }
    public DDouble Pdiv(int inn) throws Exception {
        return new DDouble(b + 7, pp.encrypt(pp.decrypt(t).multiply(new BigInteger("10000000")).divide(new BigInteger(String.valueOf(inn)))), pp);
    }
    public boolean PLargerThan(DDouble bin) throws Exception {
        //if (b == bin.b) return Main.GcSubAndCompBigVer(Paillier.subBig(t, bin.t, pp), "0", pp);
        return this.trans() > bin.trans();//b > bin.b;
    }
}
