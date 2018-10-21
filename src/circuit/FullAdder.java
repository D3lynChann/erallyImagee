package circuit;

import yao.Utils;
import yao.gate.AndGate;
import yao.gate.Gate;
import yao.gate.OrGate;
import yao.gate.Wire;
import yao.gate.XorGate;

public class FullAdder {
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
        Gate g3 = new XorGate(a, r2, r3);
        Gate g4 = new AndGate(r1, r2, r4);
        Gate g5 = new XorGate(r4, c, r5);

        lut_g1 = g1.getLut();
        lut_g2 = g2.getLut();
        lut_g3 = g3.getLut();
        lut_g4 = g4.getLut();
        lut_g5 = g5.getLut();
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
        byte[] ret3=gate3.operate(ret2, in_a);
        byte[] ret4=gate4.operate(ret2, ret1);
        byte[] ret5=gate5.operate(in_c, ret4);

        boolean [] results= new boolean[2];
        //res[0] is s, res[1] is ci+1

        if(Utils.arraysAreEqual(ret3,r3.getValue1()))
            results[0]= true;
        else if(Utils.arraysAreEqual(ret3,r3.getValue0()))
            results[0]= false;


        if(Utils.arraysAreEqual(ret5,r5.getValue0()))
            results[1]= false;
        else if(Utils.arraysAreEqual(ret5,r5.getValue1()))
            results[1]= true;

        return results;
    }
}