package circuit;
import org.omg.CORBA.PUBLIC_MEMBER;
import yao.Utils;
import yao.gate.Wire;
import java.math.BigInteger;
import HE.Paillier;

import javax.rmi.CORBA.Util;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.StreamSupport;

public class Main {

    public static int ppow(int n) {
        int res = 10;
        while (n-- > 0) {
            res *= 10;
        }
        return res;
    }

    public static boolean convenientCompare(boolean[] input_a, boolean[] input_b, int sizee) throws Exception {
        boolean cc = false;
        boolean[] res = new boolean[sizee + 1];
        for (int ctr = sizee - 1; ctr >= 0; ctr--) {
            FComparator tempComparator = new FComparator();
            tempComparator.getCircuit();
            cc = tempComparator.evaluate(input_a[ctr], input_b[ctr], cc);
        }
        return cc;
    }
    /*
    public static boolean compBig(BigInteger inputA, BigInteger inputB) throws  Exception {
        return GcSubAndCompBigVer()
    }
*/
    public static int Alice_FComp(ArrayList<byte[][]> inputLuts, ArrayList<byte[][]> inputKeys, ArrayList<byte[]> inputY, ArrayList<byte[]> inputR, byte[] inputX, byte[] inputr, ArrayList<byte[]> firstC) throws Exception {
        int tempSize = inputX.length;
        boolean[] BoolX = Utils.byteToBoolean(inputX, tempSize);
        boolean[] BoolR = Utils.byteToBoolean(inputr, tempSize);
        Wire c = new Wire();
        firstC.add(c.getValue0());
        for (int ctr = tempSize - 1; ctr >= 0; ctr--) {
            FComparator tempFC = new FComparator();
            tempFC.getCircuit(c.getValue0(), c.getValue1());
            byte[][] tempText = tempFC.gainWireText();

            inputLuts.add(tempFC.lut_g1);
            inputLuts.add(tempFC.lut_g2);
            inputLuts.add(tempFC.lut_g3);
            inputLuts.add(tempFC.lut_g4);
            inputKeys.add(tempText);
            if (BoolX[ctr]) inputY.add(tempText[1]);
            else inputY.add(tempText[0]);

            if (BoolR[ctr]) inputR.add(tempText[3]);
            else inputR.add(tempText[2]);
        }
        System.out.println("Alice ready!");
        return tempSize;
    }

    public static boolean Bob_FComp(ArrayList<byte[][]> inputLuts, ArrayList<byte[][]> inputKeys, ArrayList<byte[]> inputY, ArrayList<byte[]> inputR, byte[] firstC, int size) throws Exception {
        byte[] c = firstC;
        for (int ctr = 0; ctr < size; ctr++) {
            FComparator tempFC = new FComparator();
            tempFC.getCircuit(inputLuts.get(4 * ctr), inputLuts.get(4 * ctr + 1), inputLuts.get(4 * ctr + 2), inputLuts.get(4 * ctr + 3));
            tempFC.getWireText(inputKeys.get(ctr));
            c = tempFC.evaluate(inputY.get(ctr), inputR.get(ctr), c);
        }
        System.out.println("Bob finished");
        if (c != firstC) return true;
        return false;
    }

    public static int Alice_Sub(ArrayList<byte[][]> inputLuts, ArrayList<byte[][]> inputKeys, ArrayList<byte[]> inputY, ArrayList<byte[]> inputR, byte[] inputX, byte[] inputr, ArrayList<byte[]> firstC) throws Exception {
        int tempSize = inputX.length;
        boolean[] BoolX = Utils.byteToBoolean(inputX, tempSize);
        boolean[] BoolR = Utils.byteToBoolean(inputr, tempSize);
        Wire c = new Wire();
        firstC.add(c.getValue1());
        for (int ctr = tempSize - 1; ctr >= 0; ctr--) {
            FullSubtractor tempFC = new FullSubtractor();
            tempFC.getCircuit(c.getValue0(), c.getValue1());
            byte[][] tempText = tempFC.gainWireText();
            inputLuts.add(tempFC.lut_g1);
            inputLuts.add(tempFC.lut_g2);
            inputLuts.add(tempFC.lut_g3);
            inputLuts.add(tempFC.lut_g4);
            inputLuts.add(tempFC.lut_g5);
            inputKeys.add(tempText);
            if (BoolX[ctr]) inputY.add(tempText[1]);
            else inputY.add(tempText[0]);
            if (BoolR[ctr]) inputR.add(tempText[3]);
            else inputR.add(tempText[2]);
        }
        System.out.println("Alice ready!");
        return tempSize;
    }

    public static byte[] Bob_Sub(ArrayList<byte[][]> inputLuts, ArrayList<byte[][]> inputKeys, ArrayList<byte[]> inputY, ArrayList<byte[]> inputR, byte[] firstC, int size) throws Exception {
        byte[] c = firstC;
        byte[] res = new byte[size + 1];
        for (int ctr = 0; ctr < size; ctr++) {
            FullSubtractor tempFC = new FullSubtractor();
            tempFC.getCircuit(inputLuts.get(5 * ctr), inputLuts.get(5 * ctr + 1), inputLuts.get(5 * ctr + 2), inputLuts.get(5 * ctr + 3), inputLuts.get(5 * ctr + 4));
            tempFC.getWireText(inputKeys.get(ctr));
            c = tempFC.evaluate(inputY.get(ctr), inputR.get(ctr), c, res, size - ctr)[1];
        }
        if (c != firstC) res[0] = 1;
        else res[0] = 0;
        System.out.println("Bob finished");
        return res;
    }

    public static boolean GcComp(byte[] input_a, byte[] input_b) throws Exception {
        ArrayList<byte[][]> luts = new ArrayList<byte[][]>();
        ArrayList<byte[][]> keys = new ArrayList<byte[][]>();
        ArrayList<byte[]> gc_y = new ArrayList<byte[]>();
        ArrayList<byte[]> gc_r = new ArrayList<byte[]>();
        ArrayList<byte[]> gc_c = new ArrayList<byte[]>();
        GcCompAlice tempCompAlice = new GcCompAlice();
        GcCompBob tempCompBob = new GcCompBob();
        tempCompAlice.constructGc(luts, keys, gc_y, gc_r, input_a, input_b, gc_c);
        System.out.println("Now here comes to Bob!");
        tempCompBob.constructGc(luts, keys, gc_y, gc_r, gc_c.get(0), tempCompAlice.getSize());
        return tempCompBob.getC();
    }

    public static BigInteger absSub(BigInteger input_a, BigInteger input_b, Paillier pp) throws Exception {
        if (GcSubAndCompBigVer(input_a, pp.decrypt(input_b).toString(), pp)) return Paillier.subBig(input_a, input_b, pp);
        return Paillier.subBig(input_b, input_a, pp);
    }

    public static boolean GcComp(String input_a, String input_b) throws Exception {
        ArrayList<byte[][]> luts = new ArrayList<byte[][]>();
        ArrayList<byte[][]> keys = new ArrayList<byte[][]>();
        ArrayList<byte[]> gc_c = new ArrayList<byte[]>();
        GcCompAlice tempCompAlice = new GcCompAlice();
        GcCompBob tempCompBob = new GcCompBob();
        tempCompAlice.constructGc(luts, keys, input_a, input_b, gc_c);
        System.out.println("Now here comes to Bob!");
        tempCompBob.constructGc(luts, keys, gc_c.get(0), tempCompAlice.getSize(), tempCompAlice);
        return tempCompBob.getC();
    }

    public static byte[] GcSub(String input_a, String input_b) throws Exception {
        ArrayList<byte[][]> luts = new ArrayList<byte[][]>();
        ArrayList<byte[][]> keys = new ArrayList<byte[][]>();
        ArrayList<byte[]> gc_c = new ArrayList<byte[]>();
        GcSubAlice tempSubAlice = new GcSubAlice();
        GcSubBob tempSubBob = new GcSubBob();
        tempSubAlice.constructGc(luts, keys, input_a, input_b, gc_c);
        System.out.println("Now here comes to Bob!");
        tempSubBob.constructGc(luts, keys, gc_c.get(0), tempSubAlice.getSize(), tempSubAlice);
        tempSubBob.showResult();
        return tempSubBob.getResult();
    }

    public static boolean GcSubAndCompBigVer(BigInteger input, String T, Paillier pp) throws Exception {
        ArrayList<byte[][]> luts = new ArrayList<byte[][]>();
        ArrayList<byte[][]> keys = new ArrayList<byte[][]>();
        ArrayList<byte[]> gc_c = new ArrayList<byte[]>();
        GcSubAlice tempSubAlice = new GcSubAlice();
        GcSubBob tempSubBob = new GcSubBob();
        tempSubAlice.constructGc(luts, keys, input, pp, gc_c);
        System.out.println("Here comes to Big version's Bob's Sub!");
        tempSubBob.constructGc(luts, keys, tempSubAlice.getSize(), gc_c.get(0), tempSubAlice, tempSubAlice.getYy());
        byte[] res = tempSubBob.getResult();
        ArrayList<byte[][]> luts0 = new ArrayList<byte[][]>();
        ArrayList<byte[][]> keys0 = new ArrayList<byte[][]>();
        ArrayList<byte[]> gc_c0 = new ArrayList<byte[]>();
        GcCompAlice tempCompAlice = new GcCompAlice();
        GcCompBob tempCompBob = new GcCompBob();
        tempCompAlice.constructGc(luts0, keys0, String.valueOf(Utils.bYTEToInt(res)), T, gc_c0);
        System.out.println("Here comes to Big version's Bob's Comp!");
        tempCompBob.constructGc(luts0, keys0, gc_c0.get(0), tempCompAlice.getSize(), tempCompAlice);
        System.out.println(tempCompBob.getC());
        return tempCompBob.getC();
    }

    public static void main(String [] args) throws Exception {

        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("All input are in decimal!!");
            System.out.println("1 for adder, 2 for subtractor, 3 for comparator, 4 for min in numbers and 5 for gc_sub_comp: ");
            int choice = sc.nextInt();
            if (choice == 5) {
                Paillier pp = new Paillier(16);
                pp.generateKeys();
                System.out.print("Input your number a: ");
                String temp_a = sc.next();
                System.out.print("Input your threshold T: ");
                String T = sc.next();
                GcSubAndCompBigVer(pp.encrypt(new BigInteger(temp_a)), T, pp);
                /*
                System.out.print("Input your number b: ");
                String temp_b = sc.next();
                int ssize = (temp_a.length() > temp_b.length()) ? temp_a.length() : temp_b.length();
                byte[] input_x = new byte[ssize];
                byte[] input_y = new byte[ssize];
                byte[][] temp = Utils.Wiid(input_x, input_y);
                byte[] output = GcSub(temp_a, temp_b);
                byte[] temp_zero = new byte[output.length];
                if(output[0] == 1) for (int ctr = 0; ctr < output.length; ctr++) temp_zero[ctr] = 1;
                else for (int ctr = 0; ctr < output.length; ctr++) temp_zero[ctr] = 0;
                System.out.println(GcComp(Utils.byte_to_string(output), Utils.byte_to_string(temp_zero)));
                */
            }
            if  (choice != 4 && choice != 5) {
                System.out.print("Input your number a: ");
                int temp_a = sc.nextInt();
                System.out.print("Input your number b: ");
                int temp_b = sc.nextInt();
                long temp_temp_a = Utils.binaryToDecimal(temp_a);
                long temp_temp_b = Utils.binaryToDecimal(temp_b);
                System.out.println("Your input_a " + temp_a + " is " + temp_temp_a + " in binary");
                System.out.println("Your input_b " + temp_b + " is " + temp_temp_b + " in binary");
                int ssize = Utils.getWid(Utils.maxx(temp_temp_a, temp_temp_b));
                boolean[] input_a = new boolean[ssize];
                boolean[] input_b = new boolean[ssize];
                boolean c;
                boolean[] resss = new boolean[ssize + 1];
                Utils.biToBoo(ssize, input_a, temp_temp_a);
                Utils.biToBoo(ssize, input_b, temp_temp_b);


                if (choice == 1) {
                    //Adder

                    c = false;
                    for (int ctr = ssize - 1; ctr >= 0; ctr--) {
                        FullAdder tempAdder = new FullAdder();
                        tempAdder.getCircuit();
                        boolean tempResult[] = tempAdder.evaluate(input_a[ctr], input_b[ctr], c);
                        c = tempResult[1];
                        resss[ctr + 1] = tempResult[0];
                    }
                    resss[0] = c;
                    Utils.printAddRes(ssize, input_a, "input_a: ", true);
                    Utils.printAddRes(ssize, input_b, "input_b: ", true);
                    Utils.printAddRes(ssize + 1, resss, "result: ", false);
                } else if (choice == 2) {
                    //Subtractor
                    c = true;
                    for (int ctr = ssize - 1; ctr >= 0; ctr--) {
                        FullSubtractor tempSubtractor = new FullSubtractor();
                        tempSubtractor.getCircuit();
                        boolean tempResult[] = tempSubtractor.evaluate(input_a[ctr], input_b[ctr], c);
                        c = tempResult[1];
                        resss[ctr + 1] = tempResult[0];
                    }
                    resss[0] = c ? false : true;
                    Utils.printAddRes(ssize, input_a, "input_a: ", true);
                    Utils.printAddRes(ssize, input_b, "input_b: ", true);
                    Utils.printAddRes(ssize + 1, resss, "result: ", false);
                } else if (choice == 3) {
                    c = false;
                    for (int ctr = ssize - 1; ctr >= 0; ctr--) {
                        FComparator tempComparator = new FComparator();
                        tempComparator.getCircuit();
                        c = tempComparator.evaluate(input_a[ctr], input_b[ctr], c);
                    }
                    if (c == true) System.out.println("input_a is bigger than input_b");
                    else System.out.println("input_a is not bigger than input_b");
                }
            }
            else if (choice == 4) {
                //can expand
                //just four to test
                String plaint = "abcdefghijklmnopqrstuvwxyz";
                System.out.println("Input the numbers of your input(must be the integral power):");
                int counter01 = sc.nextInt();
                int counter02 = (int)Math.log(counter01);
                long temp;
                int[] temp_num = new int[counter01];
                long[] temp_temp_num = new long[counter01];
                for (int ctr = 0; ctr < counter01; ctr++) {
                    System.out.print("Input your number " + plaint.charAt(ctr) + " :");
                    temp_num[ctr] = sc.nextInt();
                    temp_temp_num[ctr] = Utils.binaryToDecimal(temp_num[ctr]);
                }
                temp = temp_temp_num[0];
                for (int ctr = 1; ctr < counter01; ctr++)
                    temp = Utils.maxx(temp, temp_temp_num[ctr]);
                int sizee = Utils.getWid(temp);
                boolean[][] input_boo = new boolean[counter01][sizee];
                for (int ctr = 0; ctr < counter01; ctr++) {
                    System.out.println("Your input_" + plaint.charAt(ctr) + " " + temp_num[ctr] + " is " + temp_temp_num[ctr] + " in binary");
                    Utils.biToBoo(sizee, input_boo[ctr], temp_temp_num[ctr]);
                }
                boolean flag = false;
                int[] tempp = new int[counter01];
                long[] tempIndex = new long[counter01];
                long[] tempAns = new long[counter01 / 2];
                for (int ctr = 0; ctr < counter01; ctr++) {
                    tempp[ctr] = ctr;
                    if (ctr % 2 == 1) tempIndex[ctr] = 1;
                    else tempIndex[ctr] = 2;
                }
                while (counter01 != 1) {
                    counter01 /= 2;
                    for (int ctr = 0; ctr < counter01; ctr++) {
                        Mux tempMux = new Mux(temp_temp_num[tempp[2 * ctr]], temp_temp_num[tempp[2 * ctr + 1]], tempIndex[2 * ctr], tempIndex[2 * ctr + 1], flag);
                        flag = flag ? false : true;
                        boolean tres = convenientCompare(input_boo[tempp[2 * ctr]], input_boo[tempp[2 * ctr + 1]], sizee);
                        tempAns[ctr] = tempMux.getAnswer(tres)[0];
                        tempIndex[ctr] = tempMux.getAnswer(tres)[1];
                        tempp[ctr] = Utils.DecFromBin(tres ? (tempp[1 + 2 * ctr]) : (tempp[2 * ctr]));
                    }
                }
                Mux tempMux = new Mux(temp_temp_num[tempp[0]], temp_temp_num[tempp[1]], tempIndex[0], tempIndex[1], false);
                boolean tres = convenientCompare(input_boo[tempp[0]], input_boo[tempp[1]], sizee);
                tempAns[0] = tempMux.getAnswer(tres)[0];
                tempIndex[0] = tempMux.getAnswer(tres)[1] / ppow(counter02 - 1);
                String tIndex = String.valueOf(tempIndex[0]);
                char[] tempC = new char[tIndex.length()];
                for (int ctr = 0;ctr < tIndex.length(); ctr++) {
                    if (tIndex.charAt(ctr) == '2') tempC[ctr] = '0';
                    else tempC[ctr] = '1';
                }
                String Index = new String(tempC);
                System.out.println(tempAns[0] + " and index is " + Index);
            }
        }
	}
}
