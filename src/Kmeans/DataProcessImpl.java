package Kmeans;

public class DataProcessImpl {

    /**
     * 计算d1和d2之间的距离
     * */
    public String getCat(Data d1,Data A,Data B,Data C){
        double disA=0.0,disB=0.0,disC=0.0;
        disA=Math.sqrt(Math.pow(d1.getX()-A.getX(),2)+Math.pow(d1.getY()-A.getY(),2)+Math.pow(d1.getZ()-A.getZ(),2));
        disB=Math.sqrt(Math.pow(d1.getX()-B.getX(),2)+Math.pow(d1.getY()-B.getY(),2)+Math.pow(d1.getZ()-B.getZ(),2));
        disC=Math.sqrt(Math.pow(d1.getX()-C.getX(),2)+Math.pow(d1.getY()-C.getY(),2)+Math.pow(d1.getZ()-C.getZ(),2));
        //System.out.println(d1.getName()+":"+disA+" , "+disB+" , "+disC+" , ");
        if(disA<disB && disA<disC)
        {
            return "A";
        }else if(disB<disA && disB<disC)
        {
            return "B";
        }else if(disC<disA && disC<disB)
        {
            return "C";
        }else
        {
            return "error";
        }
    }

}
