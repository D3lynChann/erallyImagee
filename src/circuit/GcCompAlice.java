package circuit;
import yao.Utils;
import yao.gate.Wire;
import java.math.BigInteger;
import HE.Paillier;

import javax.rmi.CORBA.Util;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.StreamSupport;

/**
 * Created by Dlen on 2017/11/25.
 */
public class GcCompAlice {
    Wire c;
    int size;
    BigInteger Xx, Rr, X, R;
    Paillier pp;
    ArrayList<byte[]> inputY = new ArrayList<byte[]>();
    ArrayList<byte[]> inputR = new ArrayList<byte[]>();
    public GcCompAlice() throws Exception {
        c = new Wire();
        pp = new Paillier(2048);
        pp.generateKeys();
    }

    public void constructGc(ArrayList<byte[][]> inputLuts, ArrayList<byte[][]> inputKeys,
                            ArrayList<byte[]> inputY, ArrayList<byte[]> inputR, byte[] inputX,
                            byte[] inputr, ArrayList<byte[]> firstC) throws Exception {
        size = inputX.length;
        boolean[] BoolX = Utils.byteToBoolean(inputX, size);
        boolean[] BoolR = Utils.byteToBoolean(inputr, size);
        firstC.add(c.getValue0());
        for (int ctr = size - 1; ctr >= 0; ctr--) {
            FComparator tempFC = new FComparator();
            tempFC.getCircuit(c.getValue0(), c.getValue1());
            byte[][] tempText = tempFC.gainWireText();
            inputKeys.add(tempText);
            inputLuts.add(tempFC.lut_g1);
            inputLuts.add(tempFC.lut_g2);
            inputLuts.add(tempFC.lut_g3);
            inputLuts.add(tempFC.lut_g4);

            if (BoolX[ctr]) inputY.add(tempText[1]);
            else inputY.add(tempText[0]);
            if (BoolR[ctr]) inputR.add(tempText[3]);
            else inputR.add(tempText[2]);
        }
        System.out.println("Alice ready!");
    }

    public void constructGc(ArrayList<byte[][]> inputLuts, ArrayList<byte[][]> inputKeys,
                            String inputX, String inputr, ArrayList<byte[]> firstC) throws Exception {
        size = (Integer.valueOf(inputX) > Integer.valueOf(inputr)) ? String.valueOf(Utils.binaryToDecimal(Integer.valueOf(inputX))).length(): String.valueOf(Utils.binaryToDecimal(Integer.valueOf(inputr))).length();
        Xx = new BigInteger(inputX);
        Rr = new BigInteger(inputr);
        X = pp.encrypt(Xx);
        R = pp.encrypt(Rr);
        byte[] tempa = Utils.intTo2Byte(Integer.valueOf(inputX));
        byte[] tempb = Utils.intTo2Byte(Integer.valueOf(inputr));
        byte[][] temp = Utils.Wiid(tempa, tempb);
        tempa = temp[0];
        tempb = temp[1];
        firstC.add(c.getValue0());
        for (int ctr = size - 1; ctr >= 0; ctr--) {
            FComparator tempFC = new FComparator();
            tempFC.getCircuit(c.getValue0(), c.getValue1());
            byte[][] tempText = tempFC.gainWireText();
            inputKeys.add(tempText);
            inputLuts.add(tempFC.lut_g1);
            inputLuts.add(tempFC.lut_g2);
            inputLuts.add(tempFC.lut_g3);
            inputLuts.add(tempFC.lut_g4);
            if (tempa[ctr] == 1) inputY.add(tempText[1]);
            else inputY.add(tempText[0]);
            if (tempb[ctr] == 1) inputR.add(tempText[3]);
            else inputR.add(tempText[2]);
        }
        System.out.println("Alice ready!");
    }
    public int getSize() {
        return size;
    }

    public Paillier getP() {
        return pp;
    }

    public BigInteger getX() {
        return X;
    }

    public BigInteger getR() {
        return R;
    }

    public byte[] getXsGc(int ctr) {
        return inputY.get(ctr);
    }

    public byte[] getRsGc(int ctr) {
        return inputR.get(ctr);
    }
}
