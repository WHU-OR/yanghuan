public class Data {
    int vetexnum;          //所有客户点集合n（不包括配送中心，首尾（0和2n+2）为配送中心）
    double E;                //配送中心时间窗开始时间
    double L;                //配送中心时间窗结束时间
    int vecnum;              //车辆数
    double cap;             //车辆载荷
    int[][] vertexs;        //所有点的坐标x,y
    int[] vehicles;          //车辆编号
    double[] e;            //时间窗开始时间【e[i],l[i]】
    double[] l;            //时间窗结束时间【e[i],l[i]】
    double[] s;            //客户点的服务时间
    int[][] arcs;          //arcs[i][j]表示i到j点的弧
    double[][] dist;        //距离矩阵，满足三角关系,暂用距离表示行驶时间 t[i][j]=dist[i][j]

    //截断小数3.26434-->3.2
    public double double_truncate(double v) {
        int iv = (int) v;
        if (iv + 1 - v <= 0.000000000001)
            return iv + 1;
        double dv = (v - iv) * 10;
        int idv = (int) dv;
        double rv = iv + idv / 10.0;
        return rv;
    }
}
