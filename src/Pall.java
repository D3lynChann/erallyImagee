import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

public class Pall {

    private static String a(){
        try {
            Thread.sleep(1000);
        }catch (Exception e){

        }
        return "a";
    }

    private static String b(){
        try {
            //模拟业务执行时间
            Thread.sleep(2000);
        }catch (Exception e){

        }
        return "b";
    }
    private static BigInteger c(int a){
        try {
            //模拟业务执行时间
            Thread.sleep(5000);
        }catch (Exception e){

        }
        return new BigInteger(String.valueOf(a));
    }

    public static void main(String[] args) throws Exception {

        long startTime=System.currentTimeMillis();
        CompletableFuture<BigInteger> future3 = CompletableFuture.supplyAsync(() -> c(12));
        CompletableFuture<BigInteger> future1 = CompletableFuture.supplyAsync(() -> c(13));
        CompletableFuture<BigInteger> future2 = CompletableFuture.supplyAsync(() -> c(14));

        System.out.println(future3.get());
        System.out.println(future1.get());
        System.out.println(future2.get());
        long endTime=System.currentTimeMillis(); //获取结束时间

        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }

}
