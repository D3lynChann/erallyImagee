package circuit;
import com.sun.media.sound.UlawCodec;
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
public class GcSubBob {
    byte[] c, firstc, res;
    byte[] x, r, y;
    BigInteger X, R, Y;
    Paillier ppp;
    public void constructGc(ArrayList<byte[][]> inputLuts, ArrayList<byte[][]> inputKeys,
                            ArrayList<byte[]> inputY, ArrayList<byte[]> inputR,
                            byte[] firstC, int size) throws Exception {
        c = firstc = firstC;
        res = new byte[size + 1];
        for (int ctr = 0; ctr < size; ctr++) {
            FullSubtractor tempFC = new FullSubtractor();
            tempFC.getWireText(inputKeys.get(ctr));
            tempFC.getCircuit(inputLuts.get(5 * ctr), inputLuts.get(5 * ctr + 1), inputLuts.get(5 * ctr + 2), inputLuts.get(5 * ctr + 3), inputLuts.get(5 * ctr + 4));
            c = tempFC.evaluate(inputY.get(ctr), inputR.get(ctr), c, res, size - ctr)[1];
        }
        if (c != firstC) res[0] = 1;
        else res[0] = 0;
    }

    public void constructGc(ArrayList<byte[][]> inputLuts, ArrayList<byte[][]> inputKeys,
                            byte[] firstC, int size, GcSubAlice alice) throws Exception {
        c = firstc = firstC;
        res = new byte[size + 1];
        ppp = alice.getP();
        X = alice.getX();
        R = alice.getR();
        x = Utils.intTo2Byte(Integer.valueOf(ppp.decrypt(X).toString()));
        r = Utils.intTo2Byte(Integer.valueOf(ppp.decrypt(R).toString()));
        byte[][] temp = Utils.Wiid(x, r);
        x = temp[0];
        r = temp[1];
        for (int ctr = 0; ctr < size; ctr++) {
            FullSubtractor tempFC = new FullSubtractor();
            tempFC.getWireText(inputKeys.get(ctr));
            tempFC.getCircuit(inputLuts.get(5 * ctr), inputLuts.get(5 * ctr + 1), inputLuts.get(5 * ctr + 2), inputLuts.get(5 * ctr + 3), inputLuts.get(5 * ctr + 4));
            c = tempFC.evaluate(alice.getXsGc(ctr), alice.getRsGc(ctr), c, res, size - ctr)[1];
        }
        if (c != firstC) res[0] = 1;
        else res[0] = 0;
        //System.out.println("Bob finished");
    }

    public void constructGc(ArrayList<byte[][]> inputLuts, ArrayList<byte[][]> inputKeys,
                            int size, byte[] firstC, GcSubAlice alice, BigInteger inputY) throws Exception {
        //input is just Y
        c = firstc = firstC;
        res = new byte[size + 1];
        ppp = alice.getP();
        Y = inputY;
        y = Utils.intTo2Byte(new Integer(String.valueOf(ppp.decrypt(Y))));
        for (int ctr = 0; ctr < size; ctr++) {
            FullSubtractor tempC = new FullSubtractor();
            tempC.getCircuit(inputLuts.get(5 * ctr), inputLuts.get(5 * ctr + 1), inputLuts.get(5 * ctr + 2), inputLuts.get(5 * ctr + 3), inputLuts.get(5 * ctr + 4));
            tempC.getWireText(inputKeys.get(ctr));
            c = tempC.evaluate(alice.getXsGc(ctr), alice.getRsGc(ctr), c, res, size - ctr)[1];
        }
        if (c != firstC) res[0] = 1;
        else res[0] = 0;
        //System.out.println("Bob finished");
    }

    public void showResult() throws Exception {
        Utils.print_byte(res);
    }

    public byte[] getResult() {
        return res;
    }
}
