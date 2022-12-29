import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;

//�๦�ܣ�����ģ�Ͳ����
public class Model {
  public Data data;             //������Data�Ķ���
  public IloCplex model;        //����cplex�ڲ���Ķ���
  public IloNumVar[][][] x;     //x[i][j][k]��ʾ��arcs[i][j]������k����
  public IloNumVar[][] a;       //����k�ڿͻ���i��ʼ�ӻ�����ҽ����Ա��ʱ��
  public IloNumVar[][] q;       //����뿪���ͻ���iʱ����k�ϵ�ҽ����Ա����
  public IloNumVar[] q0;        //�뿪�ֿ�ʱ����k�ϵ�ҽ����Ա����
  public IloNumVar[] d;         //�ڿͻ������³���ҽ����Ա����
  public int total_people;      //Ŀ��ֵobject
  public Solution solution;     //��¼�����

  public Model(Data data) {
    this.data = data;
  }

  //�������ܣ���ģ�ͣ������ɳ���·���͵õ�Ŀ��ֵ
  public void solve() throws IloException {
	  //��ʼ����������������solution
    ArrayList<ArrayList<Integer>> routes = new ArrayList<>();    //���峵��·������
    ArrayList<ArrayList<Double>> start_serve = new ArrayList<>();//������ҽ����Ա��ҽ����Ա��ʼ���񣩵�ʱ������
    ArrayList<ArrayList<Integer>> cusnums = new ArrayList<>();//�뿪�ͻ���ʱ���ϵ�ҽ����Ա��������
    int[] cusnum0 = new int[data.vecnum];         //����ÿ�����ʼ���ص�ҽ����Ա����(�뿪�ͻ���0ʱ���ϵ�ҽ����Ա��������)
    int[] cus_demands = new int[2*data.vetexnum];  //����ÿ�������³�ҽ����Ա����
    //��ʼ���������飬���鳤��Ϊ������k
    for (int k = 0; k < data.vecnum; k++) {
      ArrayList<Integer> t1 = new ArrayList<>();  //����һ������Ϊint�͵�����
      ArrayList<Double> t2 = new ArrayList<>();  //����һ������Ϊdouble�͵�����
      ArrayList<Integer> t3 = new ArrayList<>();  //����һ������Ϊint�͵�����
      routes.add(t1);                //�����������������뵽����routes��
      start_serve.add(t2);              //ͬ��
      cusnums.add(t3);
    }
    //�жϽ�����ģ���Ƿ�ɽ�
    if(model.solve() == false){
      //ģ�Ͳ��ɽ�
      System.out.println("problem should not solve false!!!");
      return;                    //�����ɽ⣬��ֱ������solve����
    }
    else{  //ģ�Ϳɽ⣬���ɳ���·��
    	  //��ӡ���߱���ֵ
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
    	//����·��
      for(int k = 0; k < data.vecnum; k++){
        boolean terminate = true;
        int i = 0;
        routes.get(k).add(0);    //��ÿһ��k���ӵ�0��ʼ
        start_serve.get(k).add(0.0);
//        cusnums.get(k).add(0);
//        while(terminate){
//          for (int j = 0; j < 2+2*data.vetexnum; j++) {
//            if (data.arcs[i][j]==1 && model.getValue(x[i][j][k])==1) {
//              routes.get(k).add(j);//����һ���ڵ����·����
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

  //�������ܣ�����VRPTW��ѧģ�ͽ���VRPTW��cplexģ��
  private void build_model() throws IloException {
    //model
    model = new IloCplex();
//  model.setOut(null);
    //variables
    x = new IloNumVar[2+2*data.vetexnum][2+2*data.vetexnum][data.vecnum];
    a = new IloNumVar[2+2*data.vetexnum][data.vecnum];        //�������ʵ��ʱ��
    q = new IloNumVar[2+2*data.vetexnum][data.vecnum];
    d = new IloNumVar[2+2*data.vetexnum];
    //����cplex�������������ͼ�ȡֵ��Χ
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
          //Xijk,��ʽ(10)-(11)
          for (int k = 0; k < data.vecnum; k++) {
            x[i][j][k] = model.numVar(0, 1, IloNumVarType.Int, "x" + i + "," + j + "," + k);
          }
        }
      }
    }
    //����Ŀ�꺯��
    //��ʽ(1)
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
    //����Լ��
    //��ʽ(2)ÿ���ͻ��㶼��Ҫ������һ��
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
    //��ʽ(3)��D�㱻���ʺ󣬶�Ӧ��P�㱻ͬһ��������
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
    //��ʽ(4)ÿ��������Ҫ��ҽ�����ĳ���
//    for (int k = 0; k < data.vecnum; k++) {
//      IloNumExpr expr2 = model.numExpr();
//      for (int j = 1; j <= 2*data.vetexnum; j++) {
//        if (data.arcs[0][j]==1) {
//          expr2 = model.sum(expr2, x[0][j][k]);
//        }
//      }
//      model.addEq(expr2, 1);
//    }
    //��ʽ(5)���غ�Լ��
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
    //��ʽ(6)ÿ��������Ҫ����ҽ������
//    for (int k = 0; k < data.vecnum; k++) {
//      IloNumExpr expr4 = model.numExpr();
//      for (int i = 1; i <= 2*data.vetexnum; i++) {
//        if (data.arcs[i][2*data.vetexnum+1]==1) {
//          expr4 = model.sum(expr4,x[i][2*data.vetexnum+1][k]);
//        }
//      }
//      model.addEq(expr4, 1);
//    }
    //��ʽ(7)ʱ����ƹ�ϵ
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
    //��ʽ(8)���������ƹ�ϵ
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
    //��ʽ(9)�ͻ�ʱ�䴰Լ��
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
     //��ʽ(10)ҽ������ʱ�䴰Լ��
//    for (int k = 0; k < data.vecnum; k++) {
//      model.addLe(data.E, a[0][k]);
//      model.addLe(data.E, a[2*data.vetexnum+1][k]);
//      model.addLe(a[0][k], data.L);
//      model.addLe(a[2*data.vetexnum+1][k], data.L);
//    }
 
  }

  //�������ܣ���txt�ļ��ж�ȡ���ݲ���ʼ������
  public static void process_solomon(String path,Data data,int vetexnum) throws Exception{
    String line = null;
    String[] substr = null;
    Scanner cin = new Scanner(new BufferedReader(new FileReader(path)));  //��ȡ�ļ�
    for(int i =0; i < 4;i++){
      line = cin.nextLine();  //��ȡһ��
    }
    line = cin.nextLine();
    line.trim(); //���ص����ַ��������һ��������ɾ����ʼ�ͽ�β�Ŀո�
    substr = line.split(("\\s+")); //�Կո�Ϊ��־���ַ������
    //��ʼ������
    data.vetexnum = Integer.parseInt(substr[0]);;
    data.vecnum = Integer.parseInt(substr[1]); 
    data.cap = Integer.parseInt(substr[2]);
    data.vertexs =new int[2+2*data.vetexnum][2];        //���е������x,y
    data.vehicles = new int[data.vecnum];          //�������
    data.e = new double[2*data.vetexnum+2];            //ʱ�䴰��ʼʱ��
    data.l = new double[2*data.vetexnum+2];            //ʱ�䴰����ʱ��
    data.s = new double[2*data.vetexnum+2];            //����ʱ��
    data.arcs = new int[2+2*data.vetexnum][2+2*data.vetexnum];
    data.dist = new double[2+2*data.vetexnum][2+2*data.vetexnum];  //��������������ǹ�ϵ,�þ����ʾʱ�� 
    for(int i =0; i < 4;i++){
      line = cin.nextLine();
    }
    //��ȡvetexnum-1������
    for (int i = 0; i <= 2*data.vetexnum+1; i++) {
      line.trim();
      substr = line.split("\\s+");
      data.vertexs[i][0] = Integer.parseInt(substr[1]);
      data.vertexs[i][1] = Integer.parseInt(substr[2]);
      data.e[i] = Integer.parseInt(substr[3]);
      data.l[i] = Integer.parseInt(substr[4]);
      data.s[i] = Integer.parseInt(substr[5]);
    }
    cin.close();//�ر���
    //��ʼ���������Ĳ���
    data.vertexs[1+2*data.vetexnum] = data.vertexs[0];
    data.e[2*data.vetexnum+1] = data.e[0];
    data.l[2*data.vetexnum+1] = data.l[0];
    data.E = data.e[0];
    data.L = data.l[0];
    data.s[2*data.vetexnum+1] = 0;
    double min1 = 1e15;
    double min2 = 1e15;
    //��������ʼ��
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
    //��������������ǹ�ϵ???
//    for (int  k = 0; k < data.vetexnum; k++) {
//      for (int i = 0; i < data.vetexnum; i++) {
//        for (int j = 0; j < data.vetexnum; j++) {
//          if (data.dist[i][j] > data.dist[i][k] + data.dist[k][j]) {
//            data.dist[i][j] = data.dist[i][k] + data.dist[k][j];
//          }
//        }
//      }
//    }
    //��ʼ��Ϊ��ȫͼ
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
    //��ȥ������ʱ�䴰������Լ���ı�
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
      System.exit(0);//��ֹ����
    }
    //��ʼ����������0��2n+1����Ĳ���
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
    int vetexnum = 3;//���е������������0��2n+1�����������ĵ�
    //���벻ͬ���ļ�ǰҪ�ֶ��޸�vetexnum����������ֵ�������е����,������������
    String path = "data/c1";//������ַ
    process_solomon(path,data,vetexnum);
    System.out.println("input succesfully");
    System.out.println("cplex procedure###########################");
    Model cplex = new Model(data);
    cplex.build_model();
    double cplex_time1 = System.nanoTime();
    cplex.solve();
//    cplex.solution.fesible();
    double cplex_time2 = System.nanoTime();
    double cplex_time = (cplex_time2 - cplex_time1) / 1e9;//���ʱ�䣬��λs
    System.out.println("cplex_time " + cplex_time + " bestcost " + cplex.total_people);
  }
}