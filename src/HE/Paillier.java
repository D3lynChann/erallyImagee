package HE;

/**
 * Created by Pepper on 2016/4/5.
 * 我自己照着上面手打了下面的代码，后面发现少了很多注释
 * 使得代码看起来很难看懂，于是把下面的代码全注释了，
 * 把原作者的代码复制了过来。注释掉代码在最后面。
 */

import java.math.BigInteger;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

/*  Copyright (c) 2009 Omar Hasan (omar dot hasan at insa-lyon dot fr)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class Paillier {
    private final int CERTAINTY = 64;       // certainty with which primes are generated: 1-2^(-CERTAINTY)
    private int modLength;                  // length in bits of the modulus n
    private BigInteger p;                   // a random prime
    private BigInteger q;                   // a random prime (distinct from p)
    private BigInteger lambda;              // lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1)
    private BigInteger n;                   // n = p*q
    private BigInteger nsquare;             // nsquare = n*n
    private BigInteger g;                   // a random integer in Z*_{n^2}
    private BigInteger mu;                  // mu = (L(g^lambda mod n^2))^{-1} mod n, where L(u) = (u-1)/n

    public Paillier(int modLengthIn) throws Exception {
        if (modLengthIn < 8)
            throw new Exception("Paillier(int modLength): modLength must be >= 8");

        modLength = modLengthIn;

        generateKeys();
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getLambda() {
        return lambda;
    }

    public int getModLength() {
        return modLength;
    }

    public static BigInteger addBig(BigInteger input_a, BigInteger input_b, Paillier pp) throws Exception {
        return input_a.multiply(input_b).mod(pp.getNsquare());
    }

    public static BigInteger subBig(BigInteger input_a, BigInteger input_b, Paillier pp) throws Exception {
        return input_a.multiply(input_b.modPow(pp.getN().subtract(new BigInteger("1")), pp.getNsquare())).mod(pp.getNsquare());
    }

    public static BigInteger mulBig(BigInteger input_a, BigInteger input_b, Paillier pp) throws Exception {
        return input_a.modPow(input_b, pp.getNsquare()).mod(pp.getNsquare());
    }

    public static Boolean isEq(BigInteger input_a, BigInteger input_b, Paillier pp) throws Exception {
        //return subBig(input_a, input_b, pp) == pp.encrypt(new BigInteger("0"));
        return pp.decrypt(input_a) == pp.decrypt(input_b);
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getNsquare() {
        return nsquare;
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger getMu() {
        return mu;
    }

    public void generateKeys() {
        p = new BigInteger(modLength / 2, CERTAINTY, new Random());     // a random prime

        do {
            q = new BigInteger(modLength / 2, CERTAINTY, new Random()); // a random prime (distinct from p)
        }
        while (q.compareTo(p) == 0);

        // lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1)
        lambda = (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))).divide(
                p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));

        n = p.multiply(q);              // n = p*q
        nsquare = n.multiply(n);        // nsquare = n*n

        do {
            // generate g, a random integer in Z*_{n^2}
            g = randomZStarNSquare();
        }
        // verify g, the following must hold: gcd(L(g^lambda mod n^2), n) = 1, where L(u) = (u-1)/n
        while (g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).gcd(n).intValue() != 1);

        // mu = (L(g^lambda mod n^2))^{-1} mod n, where L(u) = (u-1)/n
        mu = g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).modInverse(n);
    }

    public BigInteger encrypt(BigInteger m) throws Exception {
        // if m is not in Z_n
        if (m.compareTo(BigInteger.ZERO) < 0 || m.compareTo(n) >= 0) {
            throw new Exception("Paillier.encrypt(BigInteger m): plaintext m is not in Z_n");
        }

        // generate r, a random integer in Z*_n
        BigInteger r = randomZStarN();

        // c = g^m * r^n mod n^2
        return (g.modPow(m, nsquare).multiply(r.modPow(n, nsquare))).mod(nsquare);
    }

    public BigInteger encrypt(BigInteger m, BigInteger r) throws Exception {
        // if m is not in Z_n
        if (m.compareTo(BigInteger.ZERO) < 0 || m.compareTo(n) >= 0) {
            throw new Exception("Paillier.encrypt(BigInteger m, BigInteger r): plaintext m is not in Z_n");
        }

        // if r is not in Z*_n
        if (r.compareTo(BigInteger.ZERO) < 0 || r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1) {
            throw new Exception("Paillier.encrypt(BigInteger m, BigInteger r): random integer r is not in Z*_n");
        }

        // c = g^m * r^n mod n^2
        return (g.modPow(m, nsquare).multiply(r.modPow(n, nsquare))).mod(nsquare);
    }

    public BigInteger decrypt(BigInteger c) throws Exception {
        // if c is not in Z*_{n^2}
        if (c.compareTo(BigInteger.ZERO) < 0 || c.compareTo(nsquare) >= 0 || c.gcd(nsquare).intValue() != 1) {
            throw new Exception("Paillier.decrypt(BigInteger c): ciphertext c is not in Z*_{n^2}");
        }

        // m = L(c^lambda mod n^2) * mu mod n, where L(u) = (u-1)/n
        return c.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).multiply(mu).mod(n);
    }

    public static BigInteger PTimes(BigInteger a, BigInteger b, Paillier pp) throws Exception {
        String R1 = String.valueOf((int) (Math.random() * 100));
        String R2 = String.valueOf((int) (Math.random() * 100));
        BigInteger r1 = pp.encrypt(new BigInteger(R1));
        BigInteger r2 = pp.encrypt(new BigInteger(R2));
        BigInteger temp1 = Paillier.addBig(a, r1, pp);
        BigInteger temp2 = Paillier.addBig(b, r2, pp);
        BigInteger DTemp1 = pp.decrypt(temp1);
        BigInteger DTemp2 = pp.decrypt(temp2);
        BigInteger temp3 = DTemp1.multiply(DTemp2);
        BigInteger ar2 = pp.decrypt(Paillier.mulBig(a, new BigInteger(R2), pp));
        BigInteger br1 = pp.decrypt(Paillier.mulBig(b, new BigInteger(R1), pp));
        BigInteger r1r2 = (new BigInteger(R1)).multiply(new BigInteger(R2));
        return pp.encrypt(temp3.subtract(ar2).subtract(br1).subtract(r1r2));
    }

    public static BigInteger Bob(BigInteger a, BigInteger b, Paillier pp) throws Exception {
        BigInteger aPlusR1, bPlusR2;
        aPlusR1 = pp.decrypt(a);
        bPlusR2 = pp.decrypt(b);
        return aPlusR1.multiply(bPlusR2);
    }

    public static BigInteger Alice(BigInteger a, BigInteger b, Paillier pp) throws Exception {
        String R1 = String.valueOf((int) (Math.random() * 100));
        String R2 = String.valueOf((int) (Math.random() * 100));
        BigInteger r1 = pp.encrypt(new BigInteger(R1));
        BigInteger r2 = pp.encrypt(new BigInteger(R2));
        BigInteger temp1 = Paillier.addBig(a, r1, pp);
        BigInteger temp2 = Paillier.addBig(b, r2, pp);
        BigInteger temp3 = Bob(temp1, temp2, pp);
        BigInteger ar2 = pp.decrypt(Paillier.mulBig(a, new BigInteger(R2), pp));
        BigInteger br1 = pp.decrypt(Paillier.mulBig(b, new BigInteger(R1), pp));
        BigInteger r1r2 = (new BigInteger(R1)).multiply(new BigInteger(R2));
        return temp3.subtract(ar2).subtract(br1).subtract(r1r2);
    }

    public static BigInteger PtimesCom(BigInteger a, BigInteger b, Paillier pp) throws Exception {
        return Alice(a, b, pp);
    }

    public void printValues() {
        System.out.println("p:       " + p);
        System.out.println("q:       " + q);
        System.out.println("lambda:  " + lambda);
        System.out.println("n:       " + n);
        System.out.println("nsquare: " + nsquare);
        System.out.println("g:       " + g);
        System.out.println("mu:      " + mu);
    }

    // return a random integer in Z_n
    public BigInteger randomZN() {
        BigInteger r;

        do {
            r = new BigInteger(modLength, new Random());
        }
        while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(n) >= 0);

        return r;
    }

    // return a random integer in Z*_n
    public BigInteger randomZStarN() {
        BigInteger r;

        do {
            r = new BigInteger(modLength, new Random());
        }
        while (r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1);

        return r;
    }

    // return a random integer in Z*_{n^2}
    public BigInteger randomZStarNSquare() {
        BigInteger r;

        do {
            r = new BigInteger(modLength * 2, new Random());
        }
        while (r.compareTo(nsquare) >= 0 || r.gcd(nsquare).intValue() != 1);

        return r;
    }

    // return public key as the vector <n, g>
    public Vector publicKey() {
        Vector pubKey = new Vector();
        pubKey.add(n);
        pubKey.add(g);

        return pubKey;
    }
}




/**
 * 下面是我自己打的代码
 *
 *
public class Paillier {
    private final int CERTAINTY = 64;
    private int modLength;
    private BigInteger p, q, lambda, n, nsquare, g, mu;

    public Paillier(int modLengthIn) throws Exception {
        if (modLengthIn < 8)
            throw new Exception("Paillier(int modLength): modLength must be >= 8");
        modLength = modLengthIn;
        generateKeys();
    }

    public void generateKeys() {
        p = new BigInteger(modLength / 2, CERTAINTY, new Random());

        do {
            q = new BigInteger(modLength / 2, CERTAINTY, new Random());
        } while (q.compareTo(p) == 0);

        lambda = (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))).divide(
                p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));

        n = p.multiply(q);
        nsquare = n.multiply(n);

        do {
            g = randomZStarNSquare();
        } while (g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).gcd(n).intValue() != 1);

        mu = g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).modInverse(n);
    }

    public BigInteger encrypt(BigInteger m) throws Exception {
        // if m is not in Z_n
        if (m.compareTo(BigInteger.ZERO) < 0 || m.compareTo(n) >= 0) {
            throw new Exception("Paillier.encrypt(BigInteger m): plaintext m is not in Z_n");
        }

        // generate r, a random integer in Z*_n
        BigInteger r = randomZStarN();

        // c = g^m * r^n mod n^2
        return (g.modPow(m, nsquare).multiply(r.modPow(n, nsquare))).mod(nsquare);
    }

    public BigInteger encrypt(BigInteger m, BigInteger r) throws Exception {
        // if m is not in Z_n
        if (m.compareTo(BigInteger.ZERO) < 0 || m.compareTo(n) >= 0) {
            throw new Exception("Paillier.encrypt(BigInteger m, BigInteger r): plaintext m is not in Z_n");
        }

        // if r is not in Z*_n
        if (r.compareTo(BigInteger.ZERO) < 0 || r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1) {
            throw new Exception("Paillier.encrypt(BigInteger m, BigInteger r): random integer r is not in Z*_n");
        }

        // c = g^m * r^n mod n^2
        return (g.modPow(m, nsquare).multiply(r.modPow(n, nsquare))).mod(nsquare);
    }

    public BigInteger decrypt(BigInteger c) throws Exception {
        // if c is not in Z*_{n^2}
        if (c.compareTo(BigInteger.ZERO) < 0 || c.compareTo(nsquare) >= 0 || c.gcd(nsquare).intValue() != 1) {
            throw new Exception("Paillier.decrypt(BigInteger c): ciphertext c is not in Z*_{n^2}");
        }

        // m = L(c^lambda mod n^2) * mu mod n, where L(u) = (u-1)/n
        return c.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).multiply(mu).mod(n);
    }

    public void printValues() {
        System.out.println("p:       " + p);
        System.out.println("q:       " + q);
        System.out.println("lambda:  " + lambda);
        System.out.println("n:       " + n);
        System.out.println("nsquare: " + nsquare);
        System.out.println("g:       " + g);
        System.out.println("mu:      " + mu);
    }

    public BigInteger randomZN() {
        BigInteger r;

        do {
            r = new BigInteger(modLength, new Random());
        } while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(n) >= 0);

        return r;
    }

    public BigInteger randomZStarN() {
        BigInteger r;

        do {
            r = new BigInteger(modLength, new Random());
        }
        while (r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1);

        return r;
    }

    public BigInteger randomZStarNSquare() {
        BigInteger r;

        do {
            r = new BigInteger(modLength * 2, new Random());
        }
        while (r.compareTo(nsquare) >= 0 || r.gcd(nsquare).intValue() != 1);

        return r;
    }

    public Vector publicKey() {
        Vector pubKey = new Vector();
        pubKey.add(n);
        pubKey.add(g);

        return pubKey;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getLambda() {
        return lambda;
    }

    public int getModLength() {
        return modLength;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getNsquare() {
        return nsquare();
    }

    public BigInteger nsquare() {
        return nsquare;
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger getMu() {
        return mu;
    }
}

*/