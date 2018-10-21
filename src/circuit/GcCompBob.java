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
public class GcCompBob {
    byte[] c, firstc;
    byte[] x, r;
    BigInteger X, R;
    Paillier ppp;
    public void constructGc(ArrayList<byte[][]> inputLuts, ArrayList<byte[][]> inputKeys,
                            ArrayList<byte[]> inputY, ArrayList<byte[]> inputR,
                            byte[] firstC, int size) throws Exception {
        c = firstc = firstC;
        for (int ctr = 0; ctr < size; ctr++) {
            FComparator tempFC = new FComparator();
            tempFC.getWireText(inputKeys.get(ctr));
            tempFC.getCircuit(inputLuts.get(4 * ctr), inputLuts.get(4 * ctr + 1), inputLuts.get(4 * ctr + 2), inputLuts.get(4 * ctr + 3));
            c = tempFC.evaluate(inputY.get(ctr), inputR.get(ctr), c);
        }
        //System.out.println("Bob finished");
    }

    public void constructGc(ArrayList<byte[][]> inputLuts, ArrayList<byte[][]> inputKeys,
                            byte[] firstC, int size, GcCompAlice alice) throws Exception {
        c = firstc = firstC;
        ppp = alice.getP();
        X = alice.getX();
        R = alice.getR();
        x = Utils.intTo2Byte(Integer.valueOf(ppp.decrypt(X).toString()));
        r = Utils.intTo2Byte(Integer.valueOf(ppp.decrypt(R).toString()));
        byte[][] temp = Utils.Wiid(x, r);
        x = temp[0];
        r = temp[1];
        //Utils.print_byte(x);
        //Utils.print_byte(r);
        for (int ctr = 0; ctr < size; ctr++) {
            FComparator tempFC = new FComparator();
            tempFC.getWireText(inputKeys.get(ctr));
            tempFC.getCircuit(inputLuts.get(4 * ctr), inputLuts.get(4 * ctr + 1), inputLuts.get(4 * ctr + 2), inputLuts.get(4 * ctr + 3));
            c = tempFC.evaluate(alice.getXsGc(ctr), alice.getRsGc(ctr), c);
        }
        //System.out.println("Bob finished");
    }

    public boolean getC() {
        return (c != firstc) ? true : false;
    }
}
