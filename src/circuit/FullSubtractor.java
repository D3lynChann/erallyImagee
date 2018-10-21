package circuit;

import yao.Utils;
import yao.gate.AndGate;
import yao.gate.Gate;
import yao.gate.OrGate;
import yao.gate.Wire;
import yao.gate.XorGate;

/**
 * Created by Delyn on 2017/11/1.
 */
public class FullSubtractor {
    byte[][] lut_g1, lut_g2, lut_g3, lut_g4, lut_g5;
    Wire a, b, c, r1, r2, r3, r4, r5;

    public void getCircuit() throws Exception {
        a = new Wire();
        b = new Wire();
        c = new Wire();

        r1 = new Wire();
        r2 = new Wire();
        r3 = new Wire();
        r4 = new Wire();
        r5 = new Wire();

        Gate g1 = new XorGate(a, c, r1);
        Gate g2 = new XorGate(b, c, r2);
        Gate g3 = new AndGate(r1, r2, r3);
        Gate g4 = new XorGate(a, r2, r4);
        Gate g5 = new XorGate(a, r3, r5);

        lut_g1 = g1.getLut();
        lut_g2 = g2.getLut();
        lut_g3 = g3.getLut();
        lut_g4 = g4.getLut();
        lut_g5 = g5.getLut();
    }

    public void getCircuit(byte[] c_zero, byte[] c_one) throws Exception {
        a = new Wire();
        b = new Wire();
        c = new Wire(c_zero, c_one);

        r1 = new Wire();
        r2 = new Wire();
        r3 = new Wire();
        r4 = new Wire();
        r5 = new Wire();

        Gate g1 = new XorGate(a, c, r1);
        Gate g2 = new XorGate(b, c, r2);
        Gate g3 = new AndGate(r1, r2, r3);
        Gate g4 = new XorGate(a, r2, r4);
        Gate g5 = new XorGate(a, r3, r5);

        lut_g1 = g1.getLut();
        lut_g2 = g2.getLut();
        lut_g3 = g3.getLut();
        lut_g4 = g4.getLut();
        lut_g5 = g5.getLut();
    }

    public void getCircuit(byte[][] input_lut_g1, byte[][] input_lut_g2, byte[][] input_lut_g3, byte[][] input_lut_g4, byte[][] input_lut_g5) throws Exception {
        Gate g1 = new Gate(input_lut_g1);
        Gate g2 = new Gate(input_lut_g2);
        Gate g3 = new Gate(input_lut_g3);
        Gate g4 = new Gate(input_lut_g4);
        Gate g5 = new Gate(input_lut_g5);

        lut_g1 = g1.getLut();
        lut_g2 = g2.getLut();
        lut_g3 = g3.getLut();
        lut_g4 = g4.getLut();
        lut_g5 = g5.getLut();
    }

    public void printt() throws Exception {
        System.out.println("Luts:");
        System.out.println(lut_g1 + " " + lut_g2 + " " + lut_g3 + " " + lut_g4 + " " + lut_g5);
        System.out.println("Keys' values:");
        System.out.println(a.getValue0() + " " + a.getValue1() + " " + b.getValue0() + " " + b.getValue1() + " " + c.getValue0() + " " + c.getValue1()
                + " " + r1.getValue0() + " " + r1.getValue1() + " " + r2.getValue0() + " " + r2.getValue1() + " " + r3.getValue0() + " " + r3.getValue1()
                + " " + r4.getValue0() + " " + r4.getValue1() + " " + r5.getValue0() + " " + r5.getValue1());
    }
    public void getWireText(byte[][] inputKey) throws Exception {
        a = new Wire(inputKey[0], inputKey[1]);
        b = new Wire(inputKey[2], inputKey[3]);
        c = new Wire(inputKey[4], inputKey[5]);
        r4 = new Wire(inputKey[6], inputKey[7]);
        r5 = new Wire(inputKey[8], inputKey[9]);

        r1 = new Wire(inputKey[10], inputKey[11]);
        r2 = new Wire(inputKey[12], inputKey[13]);
        r3 = new Wire(inputKey[14], inputKey[15]);
    }

    public byte[][] gainWireText() throws Exception {
        byte[][] res = new byte[16][];
        res[0] = a.getValue0();
        res[1] = a.getValue1();
        res[2] = b.getValue0();
        res[3] = b.getValue1();
        res[4] = c.getValue0();
        res[5] = c.getValue1();
        res[6] = r4.getValue0();
        res[7] = r4.getValue1();
        res[8] = r5.getValue0();
        res[9] = r5.getValue1();
        res[10] = r1.getValue0();
        res[11] = r1.getValue1();
        res[12] = r2.getValue0();
        res[13] = r2.getValue1();
        res[14] = r3.getValue0();
        res[15] = r3.getValue1();
        return res;
    }

    public boolean[] evaluate(boolean a_input, boolean b_input, boolean c_input ) throws Exception {
        byte[] in_a=(a_input?a.getValue1():a.getValue0());
        byte[] in_b=(b_input?b.getValue1():b.getValue0());
        byte[] in_c=(c_input?c.getValue1():c.getValue0());

        Gate gate1=new Gate(lut_g1);
        Gate gate2=new Gate(lut_g2);
        Gate gate3=new Gate(lut_g3);
        Gate gate4=new Gate(lut_g4);
        Gate gate5=new Gate(lut_g5);

        byte[] ret1=gate1.operate(in_c, in_a);
        byte[] ret2=gate2.operate(in_c, in_b);
        byte[] ret3=gate3.operate(ret2, ret1);
        byte[] ret4=gate4.operate(ret2, in_a);
        byte[] ret5=gate5.operate(ret3, in_a);

        boolean [] results= new boolean[2];
        //res[0] is s, res[1] is ci+1

        if(Utils.arraysAreEqual(ret4,r4.getValue1()))
            results[0]= false;
        else if(Utils.arraysAreEqual(ret4,r4.getValue0()))
            results[0]= true;


        if(Utils.arraysAreEqual(ret5,r5.getValue0()))
            results[1]= false;
        else if(Utils.arraysAreEqual(ret5,r5.getValue1()))
            results[1]= true;

        return results;
    }

    public byte[][] evaluate(byte[] a_input, byte[] b_input, byte[] c_input , byte[] ress, int index) throws Exception {
        //System.out.println("bob.input: a " + a_input + " b " + b_input + " c " + c_input);
        byte[][] res = new byte[2][];
        Gate gate1 = new Gate(lut_g1);
        Gate gate2 = new Gate(lut_g2);
        Gate gate3 = new Gate(lut_g3);
        Gate gate4 = new Gate(lut_g4);
        Gate gate5 = new Gate(lut_g5);

        byte[] ret1 = gate1.operate(c_input, a_input);
        byte[] ret2 = gate2.operate(c_input, b_input);
        byte[] ret3 = gate3.operate(ret2, ret1);
        byte[] ret4 = gate4.operate(ret2, a_input);
        byte[] ret5 = gate5.operate(ret3, a_input);

        //res[0] is s, res[1] is ci+1

        if (Utils.arraysAreEqual(ret4, r4.getValue1())) {
            res[0] = r4.getValue1();
            ress[index] = 0;
        }
        else if(Utils.arraysAreEqual(ret4,r4.getValue0())) {
            res[0] = r4.getValue0();
            ress[index] = 1;
        }

        if(Utils.arraysAreEqual(ret5,r5.getValue0())) {
            //System.out.println("True");
            res[1] = c.getValue0();
        }
        else if(Utils.arraysAreEqual(ret5,r5.getValue1())) {
            //System.out.println("False");
            res[1] = c.getValue1();
        }

        return res;
    }
}