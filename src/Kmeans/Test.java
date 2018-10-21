package Kmeans;

import java.util.ArrayList;

public class Test {
    /**
     * k均值算法的计算过程非常直观：
     *
     * 1、从D中随机取k个元素，作为k个簇的各自的中心。
     *
     * 2、分别计算剩下的元素到k个簇中心的相异度，将这些元素分别划归到相异度最低的簇。
     *
     * 3、根据聚类结果，重新计算k个簇各自的中心，计算方法是取簇中所有元素各自维度的算术平均数。
     *
     * 4、将D中全部元素按照新的中心重新聚类。
     *
     * 5、重复第4步，直到聚类结果不再变化。
     *
     * 6、将结果输出。
     */

    static ArrayList<Data> dataList = new ArrayList<Data>();
    // 初始化
    static {
        Data d1 = new Data();
        d1.setX(1);
        d1.setY(1);
        d1.setZ(0.5);
        d1.setName("中国");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(0.3);
        d1.setY(0);
        d1.setZ(0.19);
        d1.setName("日本");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(0);
        d1.setY(0.15);
        d1.setZ(0.13);
        d1.setName("韩国");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(0.24);
        d1.setY(0.76);
        d1.setZ(0.25);
        d1.setName("伊朗");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(0.3);
        d1.setY(0.76);
        d1.setZ(0.06);
        d1.setName("沙特");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(1);
        d1.setY(1);
        d1.setZ(0);
        d1.setName("伊拉克");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(1);
        d1.setY(0.76);
        d1.setZ(0.5);
        d1.setName("卡塔尔");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(1);
        d1.setY(0.76);
        d1.setZ(0.5);
        d1.setName("阿联酋");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(0.7);
        d1.setY(0.76);
        d1.setZ(0.25);
        d1.setName("乌兹别克斯坦");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(1);
        d1.setY(1);
        d1.setZ(0.5);
        d1.setName("泰国");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(1);
        d1.setY(1);
        d1.setZ(0.25);
        d1.setName("越南");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(1);
        d1.setY(1);
        d1.setZ(0.5);
        d1.setName("阿曼");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(0.7);
        d1.setY(0.76);
        d1.setZ(0.5);
        d1.setName("巴林");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(0.7);
        d1.setY(0.68);
        d1.setZ(0.1);
        d1.setName("朝鲜");
        dataList.add(d1);
        d1 = new Data();
        d1.setX(1);
        d1.setY(1);
        d1.setZ(0.5);
        d1.setName("印尼");
        dataList.add(d1);

        System.out.println("共有数据（个）：" + dataList.size());
    }

    public static void main(String[] args) {
        // 定义簇的原始中心
        Data A = new Data(0.3, 0, 0.19, "日本");
        Data B = new Data(0.7, 0.76, 0.5, "巴林");
        Data C = new Data(1, 1, 0.5, "泰国");

        DataProcessImpl dpimpl = new DataProcessImpl();
        String[] cat = new String[dataList.size()];
        for (int j = 0; j < 5; j++) {
            int countA = 0;
            int countB = 0;
            int countC = 0;
            Data A1 = new Data(0, 0, 0, "");
            Data B1 = new Data(0, 0, 0, "");
            Data C1 = new Data(0, 0, 0, "");
            for (int i = 0; i < dataList.size(); i++) {
                // 获取第i个国家的分类
                cat[i] = dpimpl.getCat(dataList.get(i), A, B, C);
                System.out.print(dataList.get(i).getName() + cat[i]+ " ");
                if (cat[i].equals("A")) {
                    countA++;
                    A1.setX(A1.getX() + dataList.get(i).getX());
                    A1.setY(A1.getY() + dataList.get(i).getY());
                    A1.setZ(A1.getZ() + dataList.get(i).getZ());
                } else if (cat[i].equals("B")) {
                    countB++;
                    B1.setX(B1.getX() + dataList.get(i).getX());
                    B1.setY(B1.getY() + dataList.get(i).getY());
                    B1.setZ(B1.getZ() + dataList.get(i).getZ());
                } else if (cat[i].equals("C")) {
                    countC++;
                    C1.setX(C1.getX() + dataList.get(i).getX());
                    C1.setY(C1.getY() + dataList.get(i).getY());
                    C1.setZ(C1.getZ() + dataList.get(i).getZ());
                }
            }
            A.setX(A1.getX() / countA);
            A.setY(A1.getY() / countA);
            A.setZ(A1.getZ() / countA);
            B.setX(B1.getX() / countB);
            B.setY(B1.getY() / countB);
            B.setZ(B1.getZ() / countB);
            C.setX(C1.getX() / countC);
            C.setY(C1.getY() / countC);
            C.setZ(C1.getZ() / countC);

            System.out.println();
            System.out.println(A.getX()+ " " + A.getY()+ " " + A.getZ());
            System.out.println(B.getX()+ " " + B.getY()+ " " + B.getZ());
            System.out.println(C.getX()+ " " + C.getY()+ " " + C.getZ());
        }
    }

}
