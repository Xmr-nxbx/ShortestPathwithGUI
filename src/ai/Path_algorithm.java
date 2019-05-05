package ai;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 用于计算Open表与Close表的路径问题
 * @author 徐明瑞的电脑
 *
 */
public class Path_algorithm {
    Table[] opentable,closetable;
    /**
     * 建立三张表,0:深度优先 1:广度优先 2:A*
     */
    public Path_algorithm() {
        // TODO Auto-generated constructor stub
        opentable = new Table[3];
        closetable = new Table[3];
    }

    /**
     * 深度优先搜索
     * @param nodeMap	关于node节点的二维矩阵，代表了节点与节点的连线
     * @param start	起始节点坐标
     * @param end	终止节点坐标
     * @return ArrayList 返回一维路径，代表每个节点的坐标位置
     */
    public synchronized ArrayList<Integer> height(Integer[][] nodeMap,int start,int end) {
        Table open = new Table();
        Table close = new Table();
        //获得一组open表数据
        int status,father;
        int price;
        ArrayList<Integer> path = new ArrayList<Integer>();
        if(nodeMap == null)
            return path;
        //添加起始位置
//		path.add(start);
        open.statusPoint.add(start);
        open.fatherPoint.add(-1);//-1代表是起始节点位置
        open.price.add(0);
        //完全的遍历一次，找起始到所有的节点最短距离，然后计算最短路径
        while(open.statusPoint.size()!=0) {
            //获得表头的节点
            status = open.statusPoint.get(0);
            father = open.fatherPoint.get(0);
            price = open.price.get(0);
            open.statusPoint.remove(0);open.fatherPoint.remove(0);open.price.remove(0);
            //移动至close表
            int index2 = close.statusPoint.indexOf(status);
            if(index2 != -1) {
                if(close.price.get(index2) > price) {//可以替换
                    close.fatherPoint.set(index2, father);
                    close.price.set(index2, price);
                }
            }else {
                close.statusPoint.add(status);
                close.fatherPoint.add(father);
                close.price.add(price);
            }
            if(status == end) {//如果已经到达了终点,计算其他的节点距离
                continue;
            }
            for(int i = 0;i < nodeMap[status].length;i++) {
                if(nodeMap[status][i] != (double)-1 && status != i) {//存在链接的节点
                    //检测是否在open表存在过,存在,以最小的节点更新
                    int index = open.statusPoint.indexOf(i);
                    if(index != -1) {//存在open表,需要作比较
                        if(price + nodeMap[status][i] < open.price.get(index)) {
                            open.fatherPoint.set(index, status);
                            open.price.set(index, price + nodeMap[status][i]);
                        }
                    }
                    //检测是否已在close表存在过.存在,更新至open
                    index = close.statusPoint.indexOf(i);
                    if(index != -1) {//已经存在与close表中
                        if(price + nodeMap[status][i]< close.price.get(index)) {//并且路径也很小
                            close.fatherPoint.set(index, status);
                            close.price.set(index, price + nodeMap[status][i]);
                        }else{//表示这条路径走的节点大于前面走的短距离,不记录
                            continue;
                        }
                    }
                    //不存在或者存在并且最短距离更新至open表
                    open.statusPoint.add(0, i);
                    open.fatherPoint.add(0,status);
                    open.price.add(0,price+nodeMap[status][i]);
                }
            }
        }
        opentable[0] = open;
        closetable[0] = close;
        //经过了一次节点查找,通过查看close表来寻找终止节点
        int index = close.statusPoint.indexOf(end);
        while(index != -1) {
            path.add(close.statusPoint.get(index));
            index = close.statusPoint.indexOf(close.fatherPoint.get(index));
        }
//		path.add(start);
        Collections.reverse(path);
        return path;
    }

    /**
     * 广度优先搜索
     * @param nodeMap	关于node节点的二维矩阵，代表了节点与节点的连线
     * @param start	起始节点坐标
     * @param end	终止节点坐标
     * @return ArrayList 返回一维路径，代表每个节点的坐标位置
     */
    public synchronized ArrayList<Integer> width(Integer[][] nodeMap,int start,int end) {
        Table open = new Table();
        Table close = new Table();
        //获得一组open表数据
        int status,father,place = 0;
        int price;
        ArrayList<Integer> path = new ArrayList<Integer>();
        if(nodeMap == null)
            return path;
        //添加起始位置
//		path.add(start);
        open.statusPoint.add(start);
        open.fatherPoint.add(-1);//-1代表是起始节点位置
        open.price.add(0);
        //完全的遍历一次，找起始到所有的节点最短距离，然后计算最短路径
        while(open.statusPoint.size()!=0) {
            place = place % open.statusPoint.size();
            //获得表头的节点
            status = open.statusPoint.get(place);
            father = open.fatherPoint.get(place);
            price = open.price.get(place);
            open.statusPoint.remove(place);open.fatherPoint.remove(place);open.price.remove(place);
            //移动至close表
            int index2 = close.statusPoint.indexOf(status);
            if(index2 != -1) {
                if(close.price.get(index2) > price) {//可以替换
                    close.fatherPoint.set(index2, father);
                    close.price.set(index2, price);
                }
            }else {
                close.statusPoint.add(status);
                close.fatherPoint.add(father);
                close.price.add(price);
            }
            if(status == end) {//如果已经到达了终点,计算其他的节点距离
                continue;
            }
            for(int i = 0;i < nodeMap[status].length;i++) {
                if(nodeMap[status][i] != (double)-1 && status != i) {//存在链接的节点
                    //检测是否在open表存在过,存在,以最小的节点更新
                    int index = open.statusPoint.indexOf(i);
                    if(index != -1) {//存在open表,需要作比较
                        if(price + nodeMap[status][i] < open.price.get(index)) {
                            open.fatherPoint.set(index, status);
                            open.price.set(index, price + nodeMap[status][i]);
                        }else
                            continue;
                    }
                    //检测是否已在close表存在过.存在,更新至open
                    index = close.statusPoint.indexOf(i);
                    if(index != -1) {//已经存在与close表中
                        if(price + nodeMap[status][i]< close.price.get(index)) {//并且路径也很小
                            close.fatherPoint.set(index, status);
                            close.price.set(index, price + nodeMap[status][i]);
                        }else{//表示这条路径走的节点大于前面走的短距离,不记录
                            continue;
                        }
                    }
                    //不存在或者存在并且最短距离更新至open表
                    open.statusPoint.add(place, i);
                    open.fatherPoint.add(place,status);
                    open.price.add(place++,price+nodeMap[status][i]);
                }
            }
        }
        opentable[1] = open;
        closetable[1] = close;
        //经过了一次节点查找,通过查看close表来寻找终止节点
        int index = close.statusPoint.indexOf(end);
        while(index != -1) {
            path.add(close.statusPoint.get(index));
            index = close.statusPoint.indexOf(close.fatherPoint.get(index));
        }
//		path.add(start);
        Collections.reverse(path);
        return path;
    }

    /**
     * A*启发式搜索
     * @param nodeMap	关于node节点的二维矩阵，代表了节点与节点的连线
     * @param start	起始节点坐标
     * @param end	终止节点坐标
     * @param node	每个节点的位置(便于计算节点到目标的代价
     * @return ArrayList 返回一维路径，代表每个节点的坐标位置
     */
    public synchronized ArrayList<Integer> A_Star(Integer[][] nodeMap,int start,int end,ArrayList<Node> node){
        opentable[2] = new Table();
        closetable[2] = new Table();
//		System.out.println("代价:"+hprice.toString());
        //获得一组open表数据
        int status,father;
        int price;
        ArrayList<Integer> path = new ArrayList<Integer>();
        if(nodeMap == null)
            return path;
        ArrayList<Integer> hprice = estimatePrice(node, end);
        //加入起点
        opentable[2].statusPoint.add(start);
        opentable[2].fatherPoint.add(-1);//-1代表是起始节点位置
        opentable[2].price.add(0);
        //Dijkstra 算法上采用启发式算法
        while(opentable[2].statusPoint.size() != 0) {
            //获得表头的节点
            status = opentable[2].statusPoint.get(0);
            father = opentable[2].fatherPoint.get(0);
            price = opentable[2].price.get(0);
            opentable[2].statusPoint.remove(0);opentable[2].fatherPoint.remove(0);opentable[2].price.remove(0);
            //移动至close表
            int index2 = closetable[2].statusPoint.indexOf(status);
            if(index2 != -1) {
                //可以替换(close表需要更新)
                closetable[2].fatherPoint.set(index2, father);
                closetable[2].price.set(index2, price);
            }else {
                closetable[2].statusPoint.add(status);
                closetable[2].fatherPoint.add(father);
                closetable[2].price.add(price);
            }
            if(status == end)//已搜索到结果
                break;
            while(true) {//建立temp表,保存迪杰斯特拉路径有关close表的节点最近距离
                Table temp = new Table();
                int place = -1;
                int minp;
                Boolean redo = false;
                minp = 9999;
                for(int i = 0;i < closetable[2].statusPoint.size();i++) {//在已选节点中查找代价最小的点
                    //1.先记录每个close表中的节点状态
                    status = closetable[2].statusPoint.get(i);
                    father = closetable[2].fatherPoint.get(i);
                    price = closetable[2].price.get(i);
                    //2.根据状态在nodeMap中找到下一个节点
//					System.out.println(nodeMap[status].length);
                    for(int j = 0;j < nodeMap[status].length;j++) {
                        //如果存在子节点,并且不是在Map上对应的自己:
                        if(nodeMap[status][j] != -1 && j != status) {
                            int index = temp.statusPoint.indexOf(j);
                            if(index == -1) {//在temp表没有存储此节点
                                //在temp表加入信息(节点.父节点,代价)
                                temp.statusPoint.add(j);
                                temp.fatherPoint.add(status);
                                temp.price.add(price + nodeMap[status][j] + hprice.get(j));//代价 = g(x) + h(x)
                                int index3 = closetable[2].statusPoint.indexOf(j);
                                if(minp > temp.price.get(temp.price.size() - 1) && index3== -1) {//获取最小值,为了再从Open表延展出最近的节点(不存在close表
                                    minp = temp.price.get(temp.price.size() - 1);
                                    place = temp.price.size() - 1;
                                }
                                //查找close表内是否需要修改,因为走过的节点不一定是最优的,如果存在不是最优的节点,重新开始建立temp表延展出open表
                                if(index3 != -1) {
//									System.out.println("1: temp:"+temp.price.get(temp.price.size() - 1)+
//											" origin:"+(closetable[2].price.get(index3) + hprice.get(j)));
                                    //如果存在temp表节点比close表其中的节点距离更近
                                    if(temp.price.get(temp.price.size() - 1) < closetable[2].price.get(index3) + hprice.get(j)) {
                                        closetable[2].fatherPoint.set(index3, status);
                                        closetable[2].price.set(index3, price + nodeMap[status][j]);//close表中不计算代价
                                        redo = true;
                                    }
                                }
                            }else if((price + nodeMap[status][j] + hprice.get(j)) < temp.price.get(index)) {//得到了代价更小的节点
                                temp.fatherPoint.set(index, status);
                                temp.price.set(index, price + nodeMap[status][j] + hprice.get(j));
                                int index3 = closetable[2].statusPoint.indexOf(j);
                                if(minp > temp.price.get(index) && index3 == -1) {//获取最小值(不存在与close表中)
                                    minp = temp.price.get(index);
                                    place = index;
                                }
                                //查找close表内是否需要修改
                                if(index3 != -1) {
//									System.out.println("2: temp:"+temp.price.get(index)+
//											" origin:"+(closetable[2].price.get(index3) + hprice.get(j)));
                                    if(temp.price.get(index) <  closetable[2].price.get(index3) + hprice.get(j)) {
                                        closetable[2].fatherPoint.set(index3, status);
                                        closetable[2].price.set(index3, price + nodeMap[status][j]);//close表中不计算代价
                                        redo = true;
                                    }
                                }
                            }
                        }
                        if(redo == true)
                            break;
                    }
                    if(redo == true)
                        break;
                }
//				System.out.println("close:"+closetable[2].statusPoint+" temp:"+temp.statusPoint);
//				System.out.println("close:"+closetable[2].fatherPoint+" temp:"+temp.fatherPoint);
//				System.out.println("close:"+closetable[2].price+" temp:"+temp.price);
//				System.out.println(redo);
                //3.以上已经得到了close表每个节点到每一个可以达到节点的距离,求最小的
                if(redo == false) {
                    if(place != -1) {
                        opentable[2].statusPoint.add(temp.statusPoint.get(place));
                        opentable[2].fatherPoint.add(temp.fatherPoint.get(place));
                        opentable[2].price.add(minp - hprice.get(temp.statusPoint.get(place)));
                    }
                    break;
                }
            }
        }
        //经过了一次节点查找,通过查看close表来寻找终止节点
        int index = closetable[2].statusPoint.indexOf(end);
        while(index != -1) {
            path.add(closetable[2].statusPoint.get(index));
            index = closetable[2].statusPoint.indexOf(closetable[2].fatherPoint.get(index));
        }
        Collections.reverse(path);
        return path;
    }


    /**
     * 计算每个节点到目标节点的代价
     * @return	代价(曼哈顿距离)
     */
    private ArrayList<Integer> estimatePrice(ArrayList<Node> node,int end){
        ArrayList<Integer> hprice = new ArrayList<Integer>();
        Node endNode = node.get(end);
        for(int i = 0;i < node.size();i++) {
            Node n = node.get(i);
            hprice.add((int)Math.sqrt(Math.pow(n.getX() - endNode.getX(),2) + Math.pow(n.getY() - endNode.getY(),2)));
        }
        return hprice;
    }
}

/**
 * 有关Open表,Close表的数据结构（其中的节点以point存储位置决定）
 * @author 徐明瑞的电脑
 *
 */
class Table{
    ArrayList<Integer> statusPoint;
    ArrayList<Integer> fatherPoint;
    ArrayList<Integer> price;
    public Table() {
        // TODO Auto-generated constructor stub
        statusPoint = new ArrayList<Integer>();
        fatherPoint = new ArrayList<Integer>();
        price = new ArrayList<Integer>();
    }
}