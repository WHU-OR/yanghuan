import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;

//类功能：建立模型并求解
public class Model {
  public Data data;             //定义类Data的对象
  public IloCplex model;        //定义cplex内部类的对象
  public IloNumVar[][][] x;     //x[i][j][k]表示弧arcs[i][j]被车辆k访问
  public IloNumVar[][] a;       //车辆k在客户点i开始接或者送医护人员的时间
  public IloNumVar[][] q;       //到达（离开）客户点i时车辆k上的医护人员数量
  public IloNumVar[] q0;        //离开仓库时车辆k上的医护人员数量
  public IloNumVar[] d;         //在客户点上下车的医护人员数量
  public int total_people;      //目标值object
  public Solution solution;     //记录解情况

  public Model(Data data) {
    this.data = data;
  }

  //函数功能：解模型，并生成车辆路径和得到目标值
  public void solve() throws IloException {
	  //初始化变量，用于生成solution
    ArrayList<ArrayList<Integer>> routes = new ArrayList<>();    //定义车辆路径链表
    ArrayList<ArrayList<Double>> start_serve = new ArrayList<>();//车接送医护人员（医护人员开始服务）的时间序列
    ArrayList<ArrayList<Integer>> cusnums = new ArrayList<>();//离开客户点时车上的医护人员数量序列
    int[] cusnum0 = new int[data.vecnum];         //定义每辆车最开始搭载的医护人员数量(离开客户点0时车上的医护人员数量序列)
    int[] cus_demands = new int[2*data.vetexnum];  //定义每个点上下车医护人员数量
    //初始化二级数组，数组长度为车辆数k
    for (int k = 0; k < data.vecnum; k++) {
      ArrayList<Integer> t1 = new ArrayList<>();  //定义一个对象为int型的链表
      ArrayList<Double> t2 = new ArrayList<>();  //定义一个对象为double型的链表
      ArrayList<Integer> t3 = new ArrayList<>();  //定义一个对象为int型的链表
      routes.add(t1);                //将上述定义的链表加入到链表routes中
      start_serve.add(t2);              //同上
      cusnums.add(t3);
    }
    //判断建立的模型是否可解
    if(model.solve() == false){
      //模型不可解
      System.out.println("problem should not solve false!!!");
      return;                    //若不可解，则直接跳出solve函数
    }
    else{  //模型可解，生成车辆路径
    	  //打印决策变量值
//    	System.out.println();
//    	for (int i = 1; i <= 2*data.vetexnum; i++) {
//			System.out.print("d"+i+":");
//			System.out.print(model.getValue(d[i])+" ");
//		}
//    	System.out.println();
//    	for (int k = 0; k < data.vecnum; k++) {
//			System.out.print("q0"+","+k+":");
//			System.out.print(model.getValue(q0[k])+" ");
//		}
//    	System.out.println();
//    	for (int i = 0; i < 2+2*data.vetexnum; ++i) {
//    		for (int j = 0; j < 2+2*data.vetexnum; ++j) {
//				if (data.arcs[i][j]==1) {
//					for (int k = 0; k < data.vetexnum; ++k) {
//						System.out.print("x"+i+","+j+","+k+":");
//						System.out.print(model.getValue(x[i][j][k])+" ");
//					}
//				}
//			}
//
//		}
//    	System.out.println();
//    	for (int i = 0; i < 2+2*data.vetexnum; ++i) {
//					for (int k = 0; k < data.vetexnum; ++k) {
//						System.out.print("a"+i+","+k+":");
//						System.out.print(data.double_truncate(model.getValue(a[i][k]))+" ");
//					}
//
//		}
//    	System.out.println();
//    	for (int i = 0; i < 2+2*data.vetexnum; ++i) {
//					for (int k = 0; k < data.vetexnum; ++k) {
//						System.out.print("q"+i+","+k+":");
//						System.out.print(model.getValue(q[i][k])+" ");
//					}
//
//		}
    	//生成路径
      for(int k = 0; k < data.vecnum; k++){
        boolean terminate = true;
        int i = 0;
        routes.get(k).add(0);    //对每一个k，从点0开始
        start_serve.get(k).add(0.0);
//        cusnums.get(k).add(0);
//        while(terminate){
//          for (int j = 0; j < 2+2*data.vetexnum; j++) {
//            if (data.arcs[i][j]==1 && model.getValue(x[i][j][k])==1) {
//              routes.get(k).add(j);//把下一个节点加入路径中
//              start_serve.get(k).add( model.getValue(a[j][k]));
//              cusnums.get(k).add((int) model.getValue(q[j][k]));
//              i = j;
//              break;
//            }
//          }
//          if (i == data.vetexnum-1) {
//            terminate = false;
//          }
//        }
      }
    }
    solution = new Solution(data, routes, start_serve, cusnums, cusnum0);
    total_people = (int) model.getObjValue();

    System.out.println("routes="+solution.routes);
    System.out.println("cusnums="+solution.cusnums);
    System.out.println("cusnum0="+solution.cusnum0);
    System.out.println("start_serve="+solution.start_serve);
  }

  //函数功能：根据VRPTW数学模型建立VRPTW的cplex模型
  private void build_model() throws IloException {
    //model
    model = new IloCplex();
//  model.setOut(null);
    //variables
    x = new IloNumVar[2+2*data.vetexnum][2+2*data.vetexnum][data.vecnum];
    a = new IloNumVar[2+2*data.vetexnum][data.vecnum];        //车辆访问点的时间
    q = new IloNumVar[2+2*data.vetexnum][data.vecnum];
    d = new IloNumVar[2+2*data.vetexnum];
    //定义cplex变量的数据类型及取值范围
    for (int i = 0; i < 2+2*data.vetexnum; i++) {
    	d[i] = model.numVar((-data.cap),data.cap,IloNumVarType.Int,"d"+i);
      for (int k = 0; k < data.vecnum; k++) {
        a[i][k] = model.numVar(0, 1e15, IloNumVarType.Float, "a" + i + "," + k);
        q[i][k] = model.numVar(0, data.cap, IloNumVarType.Int, "q" + i + "," + k);
      }
      for (int j = 0; j < 2+2*data.vetexnum; j++) {
        if (data.arcs[i][j]==0) {
          x[i][j] = null;
        }
        else{
          //Xijk,公式(10)-(11)
          for (int k = 0; k < data.vecnum; k++) {
            x[i][j][k] = model.numVar(0, 1, IloNumVarType.Int, "x" + i + "," + j + "," + k);
          }
        }
      }
    }
    //加入目标函数
    //公式(1)
    IloNumExpr obj = model.numExpr();
    for(int j = 1; j <= data.vetexnum; j++){
        if (data.arcs[0][j]==0) {
          continue;
        }
        for(int k = 0; k < data.vecnum; k++){
          obj = model.sum(obj, model.prod(q[0][k], x[0][j][k]));
        }
      }
    model.addMinimize(obj);
    //加入约束
    //公式(2)每个客户点都需要被访问一次
//    for(int i= 1; i <= data.vetexnum;i++){
//      IloNumExpr expr1 = model.numExpr();
//      for (int k = 0; k < data.vecnum; k++) {
//        for (int j = 1; j <= data.vetexnum; j++) {
//          if (data.arcs[i][j]==1) {
//            expr1 = model.sum(expr1, x[i][j][k]);
//          }
//        }
//      }
//      model.addEq(expr1, 1);
//    }
    //公式(3)当D点被访问后，对应的P点被同一辆车访问
//    for (int i = 1; i <= data.vetexnum; i++) {
//      for (int k = 0; k < data.vecnum; k++) {
//        IloNumExpr expr3 = model.numExpr();
//        IloNumExpr subExpr1 = model.numExpr();
//        IloNumExpr subExpr2 = model.numExpr();
//        for (int j = 1; j <= 2*data.vetexnum; j++) {
//          if (data.arcs[i][j]==1) {
//            subExpr1 = model.sum(subExpr1,x[i][j][k]);
//          }
//          if (data.arcs[i+data.vetexnum][j]==1) {
//            subExpr2 = model.sum(subExpr2,x[i+data.vetexnum][j][k]);
//          }
//        }
//        expr3 = model.sum(subExpr1,model.prod(-1, subExpr2));
//        model.addEq(expr3, 0);
//      }
//    }
    //公式(4)每辆车都需要从医护中心出发
//    for (int k = 0; k < data.vecnum; k++) {
//      IloNumExpr expr2 = model.numExpr();
//      for (int j = 1; j <= 2*data.vetexnum; j++) {
//        if (data.arcs[0][j]==1) {
//          expr2 = model.sum(expr2, x[0][j][k]);
//        }
//      }
//      model.addEq(expr2, 1);
//    }
    //公式(5)流守恒约束
//    for (int k = 0; k < data.vecnum; k++) {
//      for (int i = 1; i <= 2*data.vetexnum; i++) {
//        IloNumExpr expr3 = model.numExpr();
//        IloNumExpr subExpr1 = model.numExpr();
//        IloNumExpr subExpr2 = model.numExpr();
//        for (int j = 1; j <= 2*data.vetexnum; j++) {
//          if (data.arcs[i][j]==1) {
//            subExpr1 = model.sum(subExpr1,x[i][j][k]);
//          }
//          if (data.arcs[j][i]==1) {
//            subExpr2 = model.sum(subExpr2,x[j][i][k]);
//          }
//        }
//        expr3 = model.sum(subExpr1,model.prod(-1, subExpr2));
//        model.addEq(expr3, 0);
//      }
//    }
    //公式(6)每辆车都需要返回医护中心
//    for (int k = 0; k < data.vecnum; k++) {
//      IloNumExpr expr4 = model.numExpr();
//      for (int i = 1; i <= 2*data.vetexnum; i++) {
//        if (data.arcs[i][2*data.vetexnum+1]==1) {
//          expr4 = model.sum(expr4,x[i][2*data.vetexnum+1][k]);
//        }
//      }
//      model.addEq(expr4, 1);
//    }
    //公式(7)时间递推关系
    double M = 1e15;
//    for (int k = 0; k < data.vecnum; k++) {
//      for (int i = 1; i <= 2*data.vetexnum; i++) {
//        for (int j = 1; j <= 2*data.vetexnum; j++) {
//          if (data.arcs[i][j] == 1) {
//            IloNumExpr expr5 = model.numExpr();
//            IloNumExpr expr6 = model.numExpr();
//            IloNumExpr expr7 = model.numExpr();
//            expr5 = model.sum(a[i][k], data.s[i]+data.dist[i][j]);
//            expr6 = model.prod(M,model.diff( x[i][j][k],1));
//            expr7 = model.sum(expr5,expr6,model.prod(-1, a[j][k]));
//
//            model.addLe(expr7, 0);
//          }
//        }
//      }
//      model.addEq(a[0][k], 0);
//    }
    //公式(8)车容量递推关系
//    for (int k = 0; k < data.vecnum; k++) {
//      for (int i = 1; i <= 2*data.vetexnum; i++) {
//        for (int j = 1; j <= 2*data.vetexnum; j++) {
//          if (data.arcs[i][j] == 1) {
//        	   IloNumExpr expr8 = model.numExpr();
//        	   IloNumExpr expr9 = model.numExpr();
//        	   IloNumExpr expr10 = model.numExpr();
//               expr8 = model.sum(d[j],q[i][k]);
//               expr9 = model.prod(M,model.diff(x[i][j][k], 1));
//               expr10 = model.sum(expr8,expr9,model.prod(-1, q[j][k]));
//               model.addLe(expr10, 0);
//          }
//        }
//      }
//    }
    //公式(9)客户时间窗约束
//    for (int k = 0; k < data.vecnum; k++) {
//      for (int i = 0; i <= 2*data.vetexnum+1; i++) {
//    	  IloNumExpr expr11 = model.numExpr();
//        for (int j = 0; j <= 2*data.vetexnum+1; j++) {
//          if (data.arcs[i][j] == 1) {
//        	  expr11 = model.sum(expr11,x[i][j][k]);
//          }
//        }
//        model.addLe(model.prod(data.e[i],expr11), a[i][k]);
//        model.addLe(a[i][k], model.prod(data.l[i], expr11));
//      }
//    }
     //公式(10)医护中心时间窗约束
//    for (int k = 0; k < data.vecnum; k++) {
//      model.addLe(data.E, a[0][k]);
//      model.addLe(data.E, a[2*data.vetexnum+1][k]);
//      model.addLe(a[0][k], data.L);
//      model.addLe(a[2*data.vetexnum+1][k], data.L);
//    }
 
  }

  //函数功能：从txt文件中读取数据并初始化参数
  public static void process_solomon(String path,Data data,int vetexnum) throws Exception{
    String line = null;
    String[] substr = null;
    Scanner cin = new Scanner(new BufferedReader(new FileReader(path)));  //读取文件
    for(int i =0; i < 4;i++){
      line = cin.nextLine();  //读取一行
    }
    line = cin.nextLine();
    line.trim(); //返回调用字符串对象的一个副本，删除起始和结尾的空格
    substr = line.split(("\\s+")); //以空格为标志将字符串拆分
    //初始化参数
    data.vetexnum = Integer.parseInt(substr[0]);;
    data.vecnum = Integer.parseInt(substr[1]); 
    data.cap = Integer.parseInt(substr[2]);
    data.vertexs =new int[2+2*data.vetexnum][2];        //所有点的坐标x,y
    data.vehicles = new int[data.vecnum];          //车辆编号
    data.e = new double[2*data.vetexnum+2];            //时间窗开始时间
    data.l = new double[2*data.vetexnum+2];            //时间窗结束时间
    data.s = new double[2*data.vetexnum+2];            //服务时间
    data.arcs = new int[2+2*data.vetexnum][2+2*data.vetexnum];
    data.dist = new double[2+2*data.vetexnum][2+2*data.vetexnum];  //距离矩阵，满足三角关系,用距离表示时间 
    for(int i =0; i < 4;i++){
      line = cin.nextLine();
    }
    //读取vetexnum-1行数据
    for (int i = 0; i <= 2*data.vetexnum+1; i++) {
      line.trim();
      substr = line.split("\\s+");
      data.vertexs[i][0] = Integer.parseInt(substr[1]);
      data.vertexs[i][1] = Integer.parseInt(substr[2]);
      data.e[i] = Integer.parseInt(substr[3]);
      data.l[i] = Integer.parseInt(substr[4]);
      data.s[i] = Integer.parseInt(substr[5]);
    }
    cin.close();//关闭流
    //初始化配送中心参数
    data.vertexs[1+2*data.vetexnum] = data.vertexs[0];
    data.e[2*data.vetexnum+1] = data.e[0];
    data.l[2*data.vetexnum+1] = data.l[0];
    data.E = data.e[0];
    data.L = data.l[0];
    data.s[2*data.vetexnum+1] = 0;
    double min1 = 1e15;
    double min2 = 1e15;
    //距离矩阵初始化
    for (int i = 0; i <= data.vetexnum; i++) {
      for (int j = 1; j <= data.vetexnum; j++) {
        if (i == j) {
          data.dist[i][j] = 0;
          continue;
        }
        data.dist[i][j] = Math.sqrt((data.vertexs[i][0]-data.vertexs[j][0])*(data.vertexs[i][0]-data.vertexs[j][0])+
            (data.vertexs[i][1]-data.vertexs[j][1])*(data.vertexs[i][1]-data.vertexs[j][1]));
        data.dist[i][j]=data.double_truncate(data.dist[i][j]);
        data.dist[j][data.vetexnum+j] = 0;
      }
    }
   
    data.dist[0][2*data.vetexnum+1] = 0;
    data.dist[2*data.vetexnum+1][0] = 0;
    //距离矩阵满足三角关系???
//    for (int  k = 0; k < data.vetexnum; k++) {
//      for (int i = 0; i < data.vetexnum; i++) {
//        for (int j = 0; j < data.vetexnum; j++) {
//          if (data.dist[i][j] > data.dist[i][k] + data.dist[k][j]) {
//            data.dist[i][j] = data.dist[i][k] + data.dist[k][j];
//          }
//        }
//      }
//    }
    //初始化为完全图
//    for (int i = 0; i < data.vetexnum; i++) {
//      for (int j = 0; j < data.vetexnum; j++) {
//        if (i != j) {
//          data.arcs[i][j] = 1;
//        }
//        else {
//          data.arcs[i][j] = 0;
//        }
//      }
//    }
    //除去不符合时间窗和容量约束的边
    for (int i = 0; i <= 2*data.vetexnum+1; i++) {
      for (int j = 0; j <= 2*data.vetexnum+1; j++) {
        if (i == j) {
          continue;
        }
        if (data.e[i]+data.s[i]+data.dist[i][j]>data.l[j] ) {
          data.arcs[i][j] = 0;
        }
        if (data.e[0]+data.s[i]+data.dist[0][i]+data.dist[i][2*data.vetexnum+1]>data.l[2*data.vetexnum+1]) {
          System.out.println("the calculating example is false");
          
        }
      }
    }
    for (int i = 0; i <= 2*data.vetexnum+1; i++) {
      if (data.l[i] - data.dist[0][i] < min1) {
        min1 = data.e[i] - data.dist[0][i];
      }
      if (data.e[i] + data.s[i] + data.dist[i][2*data.vetexnum+1] < min2) {
        min2 = data.e[i] + data.s[i] + data.dist[i][2*data.vetexnum+1];
      }
    }
    if (data.E > min1 || data.L < min2) {
      System.out.println("Duration false!");
      System.exit(0);//终止程序
    }
    //初始化配送中心0，2n+1两点的参数
    data.arcs[1+2*data.vetexnum][0] = 0;
    data.arcs[0][1+2*data.vetexnum] = 1;
    for (int i = 1; i < 1+2*data.vetexnum; i++) {
      data.arcs[1+2*data.vetexnum][i] = 0;
    }
    for (int i = 1; i < 1+2*data.vetexnum; i++) {
      data.arcs[i][0] = 0;
    }
  }

  public static void main(String[] args) throws Exception {
    Data data = new Data();
    int vetexnum = 3;//所有点个数，不包括0，2n+1两个配送中心点
    //读入不同的文件前要手动修改vetexnum参数，参数值等于所有点个数,包括配送中心
    String path = "data/c1";//算例地址
    process_solomon(path,data,vetexnum);
    System.out.println("input succesfully");
    System.out.println("cplex procedure###########################");
    Model cplex = new Model(data);
    cplex.build_model();
    double cplex_time1 = System.nanoTime();
    cplex.solve();
//    cplex.solution.fesible();
    double cplex_time2 = System.nanoTime();
    double cplex_time = (cplex_time2 - cplex_time1) / 1e9;//求解时间，单位s
    System.out.println("cplex_time " + cplex_time + " bestcost " + cplex.total_people);
  }
}