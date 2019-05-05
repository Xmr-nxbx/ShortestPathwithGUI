package ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * 存储Node节点
 */
public class Path_Node implements Serializable{
    Integer[][] nodeMap;
    ArrayList<Node> node;
    int start,end;
    public Path_Node() {
        // TODO Auto-generated constructor stub
        node = new ArrayList<>();
        start = -1;
        end = -1;
    }
    public Node getNode(int x,int y) {
        Node temp = null;
        for(Node n:node) {
            if(n.getX() + 20 > x&&n.getX() - 20 < x&& n.getY() + 20 > y&&n.getY() - 20 < y) {
                temp = n;
                break;
            }
        }
        return temp;
    }
    /**
     * Map扩容
     */
    private void extendMap() {
        Integer[][] temp = new Integer[nodeMap.length + 1][];
        for(int i = 0;i < temp.length;i++) {
            //最后一行(指代新的节点的对存在的节点映射)
            if(i == temp.length - 1) {
                temp[i] = new Integer[nodeMap[i - 1].length + 1];
                for(int j = 0;j < temp[i].length;j++) {
                    if(j == temp[i].length - 1) {
                        temp[i][j] = (int) 0;//对自己的映射为0
                    }
                    temp[i][j] = (int) -1;//默认无线距离-1
                }
                continue;
            }
            //不是最后一行
            temp[i] = new Integer[nodeMap[i].length + 1];
            for(int j = 0;j < temp[i].length;j++) {
                //每一行的最后一列(指存在的节点对这个新的节点映射)
                if(j == temp[i].length - 1) {
                    temp[i][j] = (int) -1;
                    continue;
                }
                //不是最后一列
                temp[i][j] = nodeMap[i][j];
            }
        }
        nodeMap = temp;
    }

    /**
     * 修改路径表nodeMap
     * @param from	路径来向
     * @param to	路径去向
     */
    public void addModify(Node from,Node to) {
        if(from == null) {
            if(nodeMap == null) {
                nodeMap = new Integer[1][];
                nodeMap[0] = new Integer[1];
                nodeMap[0][0] = (Integer) 0;
                start = 0;
                end = 0;
            }else {
                int place = nodeMap.length - 1;
                if(place != node.indexOf(to))//节点需要在表内扩充
                    extendMap();
                end = 0;
            }
        }else {
            Integer distance = (int) -1;
            int from_place = node.indexOf(from);
            int to_place = node.indexOf(to);
            if(from_place != to_place)
                distance = (int)Math.sqrt(Math.pow(to.getX() - from.getX(),2) + Math.pow(to.getY() - from.getY(),2));
            if(to_place != nodeMap.length - 1)//节点需要在表内扩充
                extendMap();
            nodeMap[from_place][to_place] = distance;
            nodeMap[to_place][from_place] = distance;
        }
    }

    /**
     * 修改路径表:(从界面返回增加的直线)
     * @param from_x	路径来向的x坐标
     * @param from_y	路径来向的y坐标
     * @param to_x	路径去向的x坐标
     * @param to_y	路径去向的y坐标
     * @return
     */
    public boolean addModify(int from_x,int from_y,int to_x,int to_y) {
        Node temp1 = null,temp2 = null;
        for(Node temp : node) {
            //在适当范围内匹配来节点与去节点
            if(temp.getX() >= from_x - 20 && temp.getX() <= from_x + 20 && temp.getY() >= from_y - 20 && temp.getY() <= from_y + 20)
                temp1 = temp;
            if(temp.getX() >= to_x - 20 && temp.getX() <= to_x + 20 && temp.getY() >= to_y - 20 && temp.getY() <= to_y + 20)
                temp2 = temp;
        }
        if(temp1 == null || temp2 == null)
            return false;
        addModify(temp1, temp2);
        return true;
    }

    /**
     * 增加节点
     * @param x	坐标x
     * @param y	坐标y
     * @return	创建了重复的节点(间距在20px左右)
     */
    public boolean addNode(int x,int y) {
        for(Node temp : node) {
            if(temp.getX() >= x - 20 && temp.getX() <= x + 20 && temp.getY() >= y - 20 && temp.getY() <= y + 20)
                return false;
        }
//		不存在创建冲突节点则创建
        Node newN = new Node(x,y);
        node.add(newN);
        addModify(null,newN);
        return true;
    }

    /**
     * 删除节点
     * @param x	节点x坐标
     * @param y	节点y坐标
     * @return	true-正确
     */
    public boolean delNode(int x,int y) {
        int place = -1;
        for(int i=0;i < node.size();i++) {
            Node temp = node.get(i);
            if(temp.getX() >= x - 20 && temp.getX() <= x + 20 && temp.getY() >= y - 20 && temp.getY() <= y + 20) {
                place = i;
                break;
            }
        }
        if(place == -1)
            return false;
        //删除节点
        node.remove(place);
        if(start >= place)
            start--;
        if(end >= place)
            end--;
        //删除在节点表的位置
        //重新赋值
        Integer[][] temp = new Integer[nodeMap.length - 1][];
        int x1;//确认temp的x1
        int length = nodeMap.length;
        for(int i = 0;i < length;i++) {
            if(i > place)
                x1 = i - 1;
            else
                x1 = i;
            if(i == place) {
                continue;
            }
            temp[x1] = new Integer[length - 1];
//			System.out.println("place:"+place+" i:"+i+" x:"+x1 + " nodeMaplength:"+nodeMap[i].length+" length"+length);
            System.arraycopy(nodeMap[i], 0, temp[x1], 0, place);
            System.arraycopy(nodeMap[i], place + 1, temp[x1], place, length - place - 1);
        }
        if(temp.length != 0)
            nodeMap = temp;
        else {
            nodeMap = null;
            start = end = -1;
        }
        return true;
    }

    /**
     * 输入两个Node节点,删除其中的路径
     * @param n1 第一个节点,类型<Strong>Node</Strong>
     * @param n2 第二个节点,类型<Strong>Node</Strong>
     * @return	如果节点位置不正确或者两个节点间不存在连线,返回false
     */
    public boolean delPath(Node n1,Node n2) {
        int place1 = node.indexOf(n1);
        int place2 = node.indexOf(n2);
        if(place1 == -1 || place2 == -1 ||place1 == place2) {
            return false;
        }
//		System.out.println("1:"+ nodeMap[place1][place2]+" 2:" + nodeMap[place2][place1]);
        if(nodeMap[place1][place2] == (double)-1||nodeMap[place2][place1] == (double)-1) {
            return false;
        }
        nodeMap[place1][place2] = -1;
        nodeMap[place2][place1] = -1;
        return true;
    }

    /**
     * 清除Path_Node所有内容
     */
    public void clean() {
        nodeMap = null;
        node = new ArrayList<Node>();
        start = -1;
        end = -1;
    }

    /**
     * 设置图的起始与终止节点
     * @param x	节点x
     * @param y	节点y
     * @param flag	0：起始节点	1：终止节点
     * @return	节点是否选择正确、或者flag有无错误
     */
    public Boolean setStartOrEnd(int x,int y,int flag) {
        Node n = null;
        for(Node temp:node) {
            if(temp.getX() >= x - 20 && temp.getX() <= x + 20 && temp.getY() >= y - 20 && temp.getY() <= y + 20) {
                n = temp;
                break;
            }
        }
        if(n == null) {
            return false;
        }
        if(flag == 0) {
            start = node.indexOf(n);
            return true;
        }else if(flag == 1) {
            end = node.indexOf(n);
            return true;
        }else
            return false;
    }
}
/*
 * 存储x,y的节点
 */
class Node implements Serializable{
    int x;
    int y;
    public Node(int x,int y) {
        // TODO Auto-generated constructor stub
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

}