package circuit;

import yao.Utils;
import yao.gate.AndGate;
import yao.gate.Gate;
import yao.gate.OrGate;
import yao.gate.Wire;
import yao.gate.XorGate;

/**
 * Created by Dlen on 2017/11/4.
 */
public class Mux {
    long r1_t, r2_t;
    long i1, i2, id_l;
    public Mux(long r1, long r2, long i11, long i22, boolean id) {
        r1_t = r1;
        r2_t = r2;
        i1 = i11;
        i2 = i22;
        id_l = (id == true) ? 1: 2;
    }
    public  long[] getAnswer(boolean input) {
        //false - 1, true - 2
        long[] res = new long[2];
        if (input == true) {
            res[0] = r2_t;
            res[1] = i2 * 10 + id_l;
        }
        else {
            res[0] = r1_t;
            res[1] = i1 * 10 + id_l;
        }
        return res;
    }
}
