package ai;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;


public class Shortest_Path {
    Path_Node path;
    Path_algorithm algorithm;
    JFrame frame;
    //节点画图区
    JPanel paint;
    ArrayList<Node> point;//显示鼠标的位置
    Integer[] pointSize;//动态显示图形	(原点最大5px
    Object[] shortestPath;//图像中应该绘画的区域(存储ArrayList<Integer>)
    double[] times;
    //方便在图上画出新建连线的位置,设定两个值
    int previous_x = -1,previous_y = -1 ,now_x = -1,now_y = -1;
    //设置高亮节点位置
    int highLight_x = -1,highLight_y = -1;
    //操作区
    JPanel[] control;//侧边有关节点的操作面板
    JButton[] controlPanelOperation;//控制面板的按键
    JLabel[] controlPanelTips;//控制面板正在执行的操作提示(默认不显示
    Boolean[] isProcessing;//哪个操作区正在进行
    String[] s = {"选择起点终点","新建节点","新建路径","删除节点","删除路径","清空图像"};//标题
    //菜单的组件
    JMenuBar mb;
    JMenu[] mcomponet;
    JMenuItem[][] mitem;
    JToolBar tb;
    ImageIcon[] img = new ImageIcon[3];
    int frame_x,frame_y;//窗体大小
    int paint_x,paint_y;//画板大小
    //结果显示区
    JPanel result;
    Boolean change;
    JLabel[] resultLabel;
    Double[] resultOfLength;
    Boolean[] whichSelected;//表示某一个JLabel被选中时,显示在close表中经过的节点
    //关于界面
    JFrame about;
    JFrame help;
    //打开保存文件
    JFileChooser openFile;
    JFileChooser saveFile;
    /**
     * 设置UI界面
     */
    private void setUI() {
        try {
            UIManager.setLookAndFeel(
                    "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(frame.getContentPane());
        SwingUtilities.updateComponentTreeUI(about.getContentPane());
        SwingUtilities.updateComponentTreeUI(openFile);
        SwingUtilities.updateComponentTreeUI(saveFile);
        for(int i = 0;i < mcomponet.length;i++) {
            SwingUtilities.updateComponentTreeUI(mcomponet[i]);
        }
//		SwingUtilities.updateComponentTreeUI();
    }

    /**
     * 绘制菜单栏
     */
    private void drawMenu() {
        mb = new JMenuBar();
        mcomponet = new JMenu[2];//暂时设置两个功能选项
        mcomponet[0] = new JMenu(" 文件 ");
        mcomponet[1] = new JMenu(" 帮助 ");
        mitem = new JMenuItem[2][];
        //第一列菜单
        mitem[0] = new JMenuItem[3];//打开,保存,关于
        mitem[0][0] = new JMenuItem("打开文件");
        mitem[0][1] = new JMenuItem("保存文件");
        mitem[0][2] = new JMenuItem("关闭程序");
        for(int i = 0;i < mitem[0].length;i++) {
            mitem[0][i].addActionListener(new Menufunction());
            mcomponet[0].add(mitem[0][i]);
            if(i == 1) {
                mcomponet[0].addSeparator();
            }
        }
        //第二列
        mitem[1] = new JMenuItem[2];//帮助,关于
        mitem[1][0] = new JMenuItem("帮助内容");
        mitem[1][1] = new JMenuItem("关于...");
        for(int i = 0;i < mitem[1].length;i++) {
            mitem[1][i].addActionListener(new Menufunction());
            mcomponet[1].add(mitem[1][i]);
            if(i == 0) {
                mcomponet[1].addSeparator();
            }
        }
        //添加菜单
        for(int i = 0;i < mcomponet.length;i++) {
            mb.add(mcomponet[i]);
        }
        mb.setBackground(Color.WHITE);
        frame.setJMenuBar(mb);
    }
    /**
     * 实现菜单功能
     * @author 徐明瑞的电脑
     *
     */
    class Menufunction implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if(e.getActionCommand() == "关闭程序") {
                frame.dispose();
                System.exit(0);
            }else if(e.getActionCommand() == "打开文件") {
                shortestPath = new Object[3];
                openFile.setSelectedFile(null);
                openFile.showOpenDialog(frame);
                if(openFile.getSelectedFile() != null) {
                    try {
                        File f = openFile.getSelectedFile();
                        if(f.isDirectory())
                            return;
                        else if(!f.exists())
                            return;
                        ObjectInputStream os = new ObjectInputStream(new FileInputStream(f));
                        path = (Path_Node)os.readObject();
                        os.close();
                    } catch (IOException | ClassNotFoundException e1) {
                        // TODO Auto-generated catch block
                        System.out.println("读了错误文件");
                        JOptionPane.showMessageDialog(frame, "文件内容不正确", "警告", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    //当path恢复后,需要更新Shortest_Path对象中的画线程序
                    point = (ArrayList<Node>) path.node.clone();
                    pointSize = new Integer[point.size()];
                    for(int i = 0;i < pointSize.length;i++)
                        pointSize[i] = 0;
                }
            }else if(e.getActionCommand() == "保存文件") {
                shortestPath = new Object[3];
                openFile.setSelectedFile(null);
                saveFile.showSaveDialog(frame);
                if(saveFile.getSelectedFile() != null) {
                    try {
                        File f = saveFile.getSelectedFile();
                        if(f.getName() == null||f.isDirectory())
                            return;
                        if(f.getName().endsWith(".txt")||f.getName().endsWith(".xmr")||f.getName().endsWith(".ys")||f.getName().endsWith(".dhn")) {
                        }else {
                            f = new File(f.getPath()+".txt");
                            System.out.println("保存文件路径:"+f.getPath());
                        }
                        f.createNewFile();
                        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
                        os.writeObject(path);
                        os.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }else if(e.getActionCommand() == "帮助内容") {
                help.setVisible(true);
            }else if(e.getActionCommand() == "关于...") {
                about.setVisible(true);
            }
        }
    }

    private JButton createSpecialButton(Action a) {
        JButton button = new JButton(a);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setBackground(new Color(237,240,249));
        button.setFont(new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT,16));
        return button;
    }
    /**
     * 绘制工具栏
     */
    private void drawToolBar() {
        tb = new JToolBar("执行方式");
        JButton[] selection = new JButton[3];
//		ImageIcon[] img = new ImageIcon[3];
        img[0] = new ImageIcon("img/source1.png");img[0].setImage(img[0].getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        img[1] = new ImageIcon("img/source2.png");img[1].setImage(img[1].getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        img[2] = new ImageIcon("img/source3.png");img[2].setImage(img[2].getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));

        selection[0] = createSpecialButton(new AbstractAction("深度优先",img[0]) {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                if((isProcessing[0]||isProcessing[1]||isProcessing[2]||isProcessing[3]||isProcessing[4]||isProcessing[5]||isProcessing[6]))
                    return;
                //避免堵塞进程,用线程运行结果
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        double t1 = System.currentTimeMillis();
                        ArrayList<Integer> ans = algorithm.height(path.nodeMap, path.start, path.end);
                        double t2 = System.currentTimeMillis();
                        times[0] = (t2 - t1);
                        System.out.println("节点路径:"+ans.toString()+" 时间:"+times[0]+"ms");
                        resultOfLength[0] = (double)0;
                        Node pre,now;
                        for(int i = 0;i < ans.size() - 1;i++) {
                            pre = point.get(ans.get(i));
                            now = point.get(ans.get(i+1));
                            resultOfLength[0] += Math.sqrt(Math.pow(pre.getX() - now.getY(), 2)+Math.pow(pre.getY() - now.getY(), 2));
                        }
                        change = true;
                        shortestPath[0] = ans;
                    }
                }).start();

            }
        });
        selection[1] = createSpecialButton(new AbstractAction("广度优先",img[1]) {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                if((isProcessing[0]||isProcessing[1]||isProcessing[2]||isProcessing[3]||isProcessing[4]||isProcessing[5]||isProcessing[6]))
                    return;
                //避免堵塞进程,用线程执行
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        double t1 = System.currentTimeMillis();
                        ArrayList<Integer> ans = algorithm.width(path.nodeMap, path.start, path.end);
                        double t2 = System.currentTimeMillis();
                        times[1] = (t2 - t1);
                        System.out.println("节点路径:"+ans.toString()+" 时间:"+times[1]+"ms");
                        resultOfLength[1] = (double)0;
                        Node pre,now;
                        for(int i = 0;i < ans.size() - 1;i++) {
                            pre = point.get(ans.get(i));
                            now = point.get(ans.get(i+1));
                            resultOfLength[1] += Math.sqrt(Math.pow(pre.getX() - now.getY(), 2)+Math.pow(pre.getY() - now.getY(), 2));
                        }
                        change = true;
                        shortestPath[1] = ans;
                    }
                }).start();
            }
        });
        selection[2] = createSpecialButton(new AbstractAction("A*算法",img[2]) {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                if((isProcessing[0]||isProcessing[1]||isProcessing[2]||isProcessing[3]||isProcessing[4]||isProcessing[5]||isProcessing[6]))
                    return;
                //避免堵塞进程,用线程执行
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        double t1 = System.currentTimeMillis();
                        ArrayList<Integer> ans = algorithm.A_Star(path.nodeMap, path.start, path.end,point);
                        double t2 = System.currentTimeMillis();
                        times[2] = (t2 - t1);
                        System.out.println("节点路径:"+ans.toString()+" 时间:"+times[1]+"ms");
                        resultOfLength[2] = (double)0;
                        Node pre,now;
                        for(int i = 0;i < ans.size() - 1;i++) {
                            pre = point.get(ans.get(i));
                            now = point.get(ans.get(i+1));
                            resultOfLength[2] += Math.sqrt(Math.pow(pre.getX() - now.getY(), 2)+Math.pow(pre.getY() - now.getY(), 2));
                        }
                        change = true;
                        shortestPath[2] = ans;
                    }
                }).start();
            }
        });
        JLabel info = new JLabel("选择运行方式:");
        info.setFont(new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT,16));
        tb.add(info);
        for(int i = 0;i < selection.length;i++) {
            tb.add(selection[i]);
        }
        tb.setMargin(new Insets(20, 10, 5, 30));
        tb.setFocusable(true);
        tb.setFloatable(false);
        tb.setBackground(new Color(237,240,249));
        frame.add(tb,BorderLayout.NORTH);
    }

    private void drawpaint() {
        paint = new JPanel() {
            //绘图区边缘显示
            int panel_x = frame_x - 300;
            int panel_y = frame_y - 130;
            StringBuffer sb = new StringBuffer();
            int sleeptime = 3;
            String[] process_tips = {"点击设置为起点","点击设置为终点","点击删除此节点"};
            @Override
            public void paint(Graphics g) {
                // TODO Auto-generated method stub
                super.paint(g);
                drawBorderline(g);//画边框
                drawShortestPath(g);//画最短的路径
                drawPoint(g);//画圆
                try {
                    drawShortestPathNode(g);
                } catch (Exception e1) {
                }//画最短路径上的节点
                drawLine(g);//画节点路径连线
                drawSpecialProcessingLine(g);//画添加节点路径时的动态效果
                drawStartAndEndPlace(g);//画起点和终点的标线
                repaint();
                //以线程休息来展现动态效果
                if(previous_x == -1)
                    sleeptime = 3;
                else
                    sleeptime = 0;
                try {
                    Thread.sleep(sleeptime);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            private void drawShortestPathNode(Graphics g) throws Exception {
                // TODO Auto-generated method stub
                for(int i = 0;i < whichSelected.length;i++) {
                    if(whichSelected[i] == null|| whichSelected[i] == false) {
                        continue;
                    }
                    if(i == 0) g.setColor(Color.BLUE);
                    else if(i == 1) g.setColor(Color.ORANGE);
                    else g.setColor(Color.RED);
                    //画的是算法计算时经过的节点
                    for(int j = 0;j < algorithm.closetable[i].statusPoint.size();j++) {
                        Node temp = point.get(algorithm.closetable[i].statusPoint.get(j));
                        g.fillOval(temp.getX() - 10, temp.getY() - 10, 20, 20);
                    }
                }
                g.setColor(Color.BLACK);
            }

            private void drawShortestPath(Graphics g) {
                // TODO Auto-generated method stub
                int upspace;//区分绘画
                for(int i = 0;i < shortestPath.length;i++) {
                    if(shortestPath[i]==null){
                        continue;
                    }
                    if(i == 0) {
                        g.setColor(Color.BLUE);
                        upspace = 5;
                    }else if(i == 1) {
                        g.setColor(Color.ORANGE);
                        upspace = 8;
                    }else {
                        g.setColor(Color.RED);
                        upspace = -6;
                    }
                    Node from,to;
                    ArrayList<Integer> map = ((ArrayList<Integer>) shortestPath[i]);
                    for(int j = 0;j < map.size() - 1;j++) {
                        from = point.get(map.get(j));
                        to = point.get(map.get(j + 1));
                        g.drawLine(from.getX()+upspace, from.getY()+upspace, to.getX()+upspace, to.getY()+upspace);
                    }
                }
                g.setColor(Color.BLACK);
            }

            private void drawStartAndEndPlace(Graphics g) {
                // TODO Auto-generated method stub
                int place = path.start;
                if(place != - 1) {
                    Node temp = point.get(place);
                    g.setColor(Color.MAGENTA);
                    g.drawOval(temp.getX() - 14, temp.getY() - 14, 28, 28);
                    g.drawOval(temp.getX() - 15, temp.getY() - 15, 29, 29);
                    g.drawOval(temp.getX() - 15, temp.getY() - 15, 30, 30);
                }
                place = path.end;
                if(place != - 1) {
                    Node temp = point.get(place);
                    g.setColor(Color.ORANGE);
                    g.drawOval(temp.getX() - 12, temp.getY() - 12, 24, 24);
                    g.drawOval(temp.getX() - 13, temp.getY() - 13, 25, 25);
                    g.drawOval(temp.getX() - 13, temp.getY() - 13, 26, 26);
                }
            }

            private void drawLine(Graphics g) {
                // TODO Auto-generated method stub
                int i,j;
                int x1,y1,x2,y2;
                if(path.nodeMap == null)
                    return;
                for(i = 0;i < path.nodeMap.length;i++) {
                    for(j = 0;j < path.nodeMap[i].length;j++) {
                        if(i != j&&path.nodeMap[i][j] != (double)-1) {//-1代表两个节点之间没有连线
                            x1 = point.get(i).getX();
                            y1 = point.get(i).getY();
                            x2 = point.get(j).getX();
                            y2 = point.get(j).getY();
                            g.drawLine(x1, y1, x2, y2);
                        }
                    }
                }
            }

            private void drawSpecialProcessingLine(Graphics g) {
                // TODO Auto-generated method stub
                if(isProcessing[2]&&previous_x != -1) {//可以绘画
                    g.drawLine(previous_x, previous_y, now_x, now_y);
                    g.setColor(Color.BLUE);
                    g.drawString("右键取消连线", now_x+5, now_y);
                    g.setColor(Color.BLACK);
                }else if(isProcessing[4]) {//选择两个需要删除的节点,删除其中路径
                    g.setColor(Color.BLUE);
                    if(previous_x == -1)
                        g.drawString("选择第一个节点", now_x, now_y);
                    else {
                        sb.delete(0, sb.length());
                        sb.append("选择第二个节点,前一个节点:(");sb.append(previous_x - 10);sb.append(",");sb.append(paint_y - previous_y + 10);sb.append(")");
                        g.drawString(sb.toString(), now_x, now_y);
                        g.setColor(Color.RED);
                        g.fillOval(previous_x - 10, previous_y - 10, 20, 20);
                        sb.delete(0, sb.length());
                    }
                    g.setColor(Color.BLACK);
                }else if((isProcessing[0]||isProcessing[6]||isProcessing[3])&&highLight_x != -1) {//显示设置节点为起始或终止或删除时高亮的节点
                    g.setColor(Color.RED);
                    int place;
                    if(isProcessing[0])
                        place = 0;
                    else if(isProcessing[6])
                        place = 1;
                    else
                        place = 2;
                    g.drawString(process_tips[place], highLight_x+20, highLight_y+15);
                    g.fillOval(highLight_x - 10, highLight_y - 10, 20, 20);
                    g.setColor(Color.BLACK);
                }
            }
            private void drawPoint(Graphics g) {
                if(point.size() == pointSize.length) {
                    for(int i = 0;i < point.size();i++) {
                        g.fillOval(point.get(i).getX() - pointSize[i]/2, point.get(i).getY() - pointSize[i]/2, pointSize[i], pointSize[i]);
                        sb.delete(0, sb.length());
                        //构造输出内容
                        sb.append("(");sb.append(point.get(i).getX() - 10);sb.append(",");sb.append(panel_y - point.get(i).getY());sb.append(")");
                        g.drawString(sb.toString(), point.get(i).getX() + 15, point.get(i).getY());
                        if(pointSize[i] < 20)//设置大小
                            pointSize[i]++;
                    }
                }
            }
            private void drawBorderline(Graphics g) {
                if(paint_x + 5 > panel_x - 10) {
                    paint_x = panel_x - 10;
                }else
                    paint_x += 5;
                g.drawLine(10, panel_y - 10, paint_x, panel_y - 10);
                g.drawLine(panel_x, 10, panel_x - paint_x + 10, 10);
                sb.delete(0, sb.length());
                sb.append("x:");sb.append(paint_x);
                g.drawString(sb.toString(), paint_x, panel_y - 10);
                sb.delete(0, sb.length());
                if(paint_y + 5 > panel_y - 10) {
                    paint_y = panel_y - 10;
                }else
                    paint_y += 5;
                g.drawLine(10, panel_y - 10, 10, panel_y - paint_y);
                g.drawLine(panel_x, 10, panel_x, paint_y);
                sb.append("y:");sb.append(paint_y);
                g.drawString(sb.toString(), 10, panel_y - paint_y);
                sb.delete(0, sb.length());
            }
        };
    }

    /**
     * 图形界面构造方法
     */
    public Shortest_Path() {
        // TODO Auto-generated constructor stub
        about = new JFrame("关于");
        help = new JFrame("帮助");
        algorithm = new Path_algorithm();
        shortestPath = new Object[3];
        change = false;
        resultOfLength = new Double[3];
        resultLabel = new JLabel[3];
        times = new double[3];
        whichSelected = new Boolean[3];
        path = new Path_Node();
        point = new ArrayList<Node>();
        pointSize = new Integer[0];
        paint_x = 10;paint_y = 10;
        openFile = new JFileChooser(".");
        saveFile = new JFileChooser(".");

        Dimension tk = Toolkit.getDefaultToolkit().getScreenSize();
        frame_x = tk.width/2 + 100;frame_y = tk.height/2 + 200;
        frame = new JFrame("最短路径问题");
        setFileChooser();
        drawMenu();
        drawToolBar();
        frame.setIconImage(new ImageIcon("img/icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        frame.setBounds(200, 200, frame_x, frame_y);

        drawpaint();
        paint.addMouseListener(new MousePoint());
        //paint特殊直线追踪
        paint.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // TODO Auto-generated method stub
                if(isProcessing[2] == true) {//画连线时的特殊直线
                    setNow(e.getX(), e.getY());
                }else if(isProcessing[4]) {//删除两个点时的操作
                    setNow(e.getX(),e.getY());
                }else if(isProcessing[3]||isProcessing[0]||isProcessing[6]) {//删除节点或设置起始或终止节点的操作
                    int x = e.getX(),y = e.getY();
                    Node temp = path.getNode(x, y);
                    if(temp != null) {
                        highLight_x = temp.getX();
                        highLight_y = temp.getY();
                    }else {
                        highLight_x = -1;
                        highLight_y = -1;
                    }
                }
            }
            @Override
            public void mouseDragged(MouseEvent e) {}
        });
        paint.setBackground(Color.WHITE);
        paint.setSize(frame_x - 300, frame_y - 130);

        setControlPanel();
        frame.add(paint);

        setResultPanel();
        result.setVisible(false);
        //动态判断是否有结果显示,并且输出结果
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                int flag,height = 0;
                while(true) {
                    flag = 0;
                    for(int i = 0;i < shortestPath.length;i++) {
                        if(shortestPath[i] != null) {
                            flag = 1;
                            break;
                        }
                    }
                    if(flag == 1) {
                        if(change == true) {//渲染一次页面
                            result.setVisible(true);
                            addComponentOnResult();
                            change = false;
//							System.out.println("change");
                        }
                        if(height<=80) {
                            height += 5;
                            frame.setSize(frame_x, frame_y + height);
                        }
                    }else {
                        result.setVisible(false);
                        if(height > 0) {
                            height -= 5;
                            frame.setSize(frame_x, frame_y + height);
                        }
                    }

                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        drawAbout();
        drawHelp();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUI();//设置windows样式
        frame.setVisible(true);
    }
    /**
     * 设置文件打开保存窗口
     */
    private void setFileChooser() {
        // TODO Auto-generated method stub
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // TODO Auto-generated method stub
                if(pathname.isFile()) {
                    if(pathname.getName().endsWith(".txt"))
                        return true;
                    else if(pathname.getName().endsWith(".xmr"))
                        return true;
                    else if(pathname.getName().endsWith(".ys"))
                        return true;
                    else if(pathname.getName().endsWith(".dhn"))
                        return true;
                }
                if(pathname.isDirectory())
                    return true;
                return false;
            }
            @Override
            public String getDescription() {
                // TODO Auto-generated method stub
                return "《最短路径问题》可读文件 (*.txt; *.xmr; *.ys; *.dhn)";
            }
        };
        openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        openFile.addChoosableFileFilter(filter);
        saveFile.addChoosableFileFilter(filter);
    }

    /**
     * 在结果显示区显示结果
     */
    protected void addComponentOnResult() {
        // TODO Auto-generated method stub
        StringBuffer sb = new StringBuffer();
        String[] s = {"选择此处显示图像经过蓝色节点<br>深度优先:<br>","选择此处显示图像经过黄色节点<br>广度优先:<br>","选择此处显示图像经过红色节点<br>A*搜索:<br>"};
        result.removeAll();
        result.setSize(frame_x, 80);
        Box contain = Box.createHorizontalBox();
        contain.add(Box.createHorizontalGlue());
        for(int i = 0;i < shortestPath.length;i++) {
            whichSelected[i] = false;
            if(shortestPath[i] != null) {
                sb.append("<html>");sb.append(s[i]);
                sb.append("耗费时间:");sb.append(times[i]);sb.append("ms<br>");
                if(((ArrayList<Integer>)shortestPath[i]).size() != 0) {
                    sb.append("节点个数:");sb.append(((ArrayList<Integer>)shortestPath[i]).size());sb.append("<br>");
                    sb.append("节点距离:");sb.append(new BigDecimal(resultOfLength[i]).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());	sb.append("</html>");
                }else {
                    sb.append("无可到达目标节点的路径<br></html>");
                }
                resultLabel[i] = new JLabel(sb.toString());resultLabel[i].setSize(100, 80);resultLabel[i].setFont(new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT,12));
                resultLabel[i].addMouseListener(new triggerOfshowShortestNode(i));//当鼠标移动至JLabel上是出现经过的节点
//				resultLabel[i].setBackground(Color.ORANGE);
                contain.add(resultLabel[i]);
                contain.add(Box.createHorizontalGlue());
                sb.delete(0, sb.length());
            }
        }
        contain.setBackground(Color.WHITE);
        result.setBackground(Color.WHITE);
        result.add(contain);
        result.updateUI();
        result.repaint();
    }

    class triggerOfshowShortestNode implements MouseListener{
        int n;
        Color play;
        public triggerOfshowShortestNode(int i) {
            // TODO Auto-generated constructor stub
            n = i;whichSelected[n] = false;
            if(n == 0) play = Color.BLUE;
            else if(n == 1) play = Color.ORANGE;
            else play = Color.RED;
        }
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {whichSelected[n] = true;resultLabel[n].setForeground(play);}//鼠标位于此区域,显示结果
        @Override
        public void mouseExited(MouseEvent e) {whichSelected[n] = false;resultLabel[n].setForeground(Color.BLACK);}//鼠标移出区域,取消显示
    }

    /**
     * 设置位于BorderLayout底端的界面
     */
    private void setResultPanel() {
        // TODO Auto-generated method stub
        result = new JPanel(new BorderLayout());
        result.setSize(frame_x, 80);
        frame.add(result,BorderLayout.SOUTH);
    }

    /**
     * 添加控制面板
     */
    private void setControlPanel() {
        // TODO Auto-generated method stub
        JPanel controlPanel = new JPanel(new GridLayout(6,1,0,10));//选择起点终点、新建节点、新建链接、删除节点、删除链接、清空
        control = new JPanel[6];
        isProcessing = new Boolean[7];//起点终点选项
        controlPanelTips = new JLabel[6];
        controlPanelOperation = new JButton[7];//起点终点在一个区
        String[] tips = {"图中选择一个点设为起点或终点","点击合适区域创建节点","选择图中两个节点创建路径","选择图中节点删除它和与它有关路径",
                "选择图中两个节点删除其中的路径","清空图像前留意图像保存情况"};
        Border line = BorderFactory.createLineBorder(Color.BLACK, 1, true);//设置边框

        for(int i = 0;i < control.length;i++) {
            control[i] = new JPanel();
            isProcessing[i] = false;	//一开始并没有启动
            controlPanelTips[i] = new JLabel("              ");//留足够长度
            controlPanelTips[i].setFont(new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT,12));
            controlPanelTips[i].setForeground(Color.RED);//设置提示区的前景色
            //创建边框
            TitledBorder tb = new TitledBorder(line,s[i],TitledBorder.LEFT,TitledBorder.TOP,new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT,16));
            control[i].setBorder(tb);
            //每个控制面板空间布局:
            control[i].setLayout(new BorderLayout(2,0));
            //设置按键
            if(i == 0) {
                int l = controlPanelOperation.length - 1;//及时修改,把最后一个按键设为终点
                isProcessing[l] = false;
                controlPanelOperation[i] = new JButton("设置起点");
                controlPanelOperation[l] = new JButton("设置终点");
//				controlPanelOperation[i].setMargin(new Insets(5,0,5,0));
                controlPanelOperation[i].setFont(new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT,16));
                controlPanelOperation[i].addActionListener(new Operation());
//				controlPanelOperation[l].setMargin(new Insets(5,0,5,0));
                controlPanelOperation[l].setFont(new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT,16));
                controlPanelOperation[l].addActionListener(new Operation());
            }else {
                controlPanelOperation[i] = new JButton(s[i]);
//				controlPanelOperation[i].setMargin(new Insets(5,0,5,0));
                controlPanelOperation[i].setFont(new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT,16));
                controlPanelOperation[i].addActionListener(new Operation());
            }
            //设置功能提示
            JLabel tip = new JLabel(tips[i]);tip.setFont(new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT,12));
            //北:操作提示 中:按键执行 南:是否正在执行
            control[i].add(tip,BorderLayout.NORTH);

            //中间部分及时修改(起点终点两个按键)
            if(i == 0) {
                Box temp = Box.createHorizontalBox();
                temp.add(controlPanelOperation[i]);temp.add(controlPanelOperation[controlPanelOperation.length - 1]);
                control[i].add(temp);
                control[i].add(controlPanelTips[i],BorderLayout.SOUTH);
            }else {
                control[i].add(controlPanelOperation[i]);
                control[i].add(controlPanelTips[i],BorderLayout.SOUTH);
            }
            controlPanel.add(control[i]);
        }
        controlPanel.setSize(400, frame_y - 130);
        frame.add(controlPanel,BorderLayout.EAST);
    }
    /**
     * 右侧控制面板的功能实现
     * @author 徐明瑞的电脑
     *
     */
    class Operation implements ActionListener{
        /**
         * 变更按钮
         */
        public void changeinfo() {
            int place;
            for(int i = 0;i < isProcessing.length;i++) {
                if(i == 6 || i == 0) {//增加,删除节点
                    place = 0;
                }else {
                    place = i;
                }
//				System.out.println(i);
                if(isProcessing[i] == true) {
                    controlPanelOperation[i].setText("取消操作");
                    controlPanelTips[place].setText("此操作正在执行中...");;
                }else {
                    controlPanelOperation[i].setEnabled(false);
                }
            }
//			System.out.println("已更改");
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            //	String[] s = {"选择起点终点","新建节点","新建路径","删除节点","删除路径","清空图像"};//标题
            if(e.getActionCommand() == "清空图像") {//5
                isProcessing[5] = true;
                shortestPath = new Object[3];
                paint_x = 10;
                paint_y = 10;
                point.clear();
                pointSize = new Integer[0];
                //修改存储内容
                point = new ArrayList<Node>();
                pointSize = new Integer[0];
                path.clean();
                isProcessing[5] = false;
            }else if(e.getActionCommand() == "新建节点") {//1
                isProcessing[1] = true;
                shortestPath = new Object[3];
                changeinfo();
            }else if(e.getActionCommand() == "新建路径"){//2
                isProcessing[2] = true;
                shortestPath = new Object[3];
                changeinfo();
            }else if(e.getActionCommand() == "删除节点") {//3
                isProcessing[3] = true;
                shortestPath = new Object[3];
                changeinfo();
            }else if(e.getActionCommand() == "删除路径") {//4
                isProcessing[4] = true;
                shortestPath = new Object[3];
                changeinfo();
            }else if(e.getActionCommand() == "设置起点") {//0
                isProcessing[0] = true;
                shortestPath = new Object[3];
                changeinfo();
            }else if(e.getActionCommand() == "设置终点") {//6
                isProcessing[6] = true;
                shortestPath = new Object[3];
                changeinfo();
            }else if(e.getActionCommand() == "取消操作") {//检测isProcessing
                //把可能错误的paint的特殊画线清除
                setPrevious(-1,-1);
                highLight_x = -1;highLight_y = -1;

                int place;
                for(int i = 0;i < isProcessing.length;i++) {
                    //找准对应的面板位置
                    if(i == 0||i == 6) {
                        place = 0;
                    }else {
                        place = i;
                    }
                    if(isProcessing[i] == true) {
                        //变更按钮样式
                        if(i == 0) {
                            controlPanelOperation[i].setText("设置起点");
                        }else if(i == 6) {
                            controlPanelOperation[i].setText("设置终点");
                        }else
                            controlPanelOperation[i].setText(s[i]);
                        controlPanelTips[place].setText("    ");
                        isProcessing[i] = false;
                    }else {
                        controlPanelOperation[i].setEnabled(true);
                    }
                }
            }
        }
    }

    /**
     * 实现绘画区点击可操作
     * @author 徐明瑞的电脑
     *
     */
    class MousePoint implements MouseListener{
        //设置路径建立前一个坐标
        int before_x = -1,before_y = -1;
        @Override
        public void mouseClicked(MouseEvent e) {
            // TODO Auto-generated method stub
            //测试...在图像上生成对应的原点
            if(isProcessing[1] == true) {//1:添加节点
                int x = e.getX();
                int y = e.getY();
                if(x > paint_x || x < 10 || y > paint_y || y < 10) {
                    //超出规定范围
                    JOptionPane.showMessageDialog(frame, "超出界面范围", "警告", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                else {
                    //判断并完成加入
                    if(path.addNode(x, y)) {
                        point.add(new Node(x,y));
                        System.out.println("path节点数:"+path.node.size()+"point节点数:"+point.size());
                        Integer[] temp = new Integer[pointSize.length + 1];
                        System.arraycopy(pointSize, 0, temp, 0, pointSize.length);
                        temp[temp.length - 1] = 0;
                        pointSize = temp;
                    }else {
                        //节点距离太近警告
                        JOptionPane.showMessageDialog(frame, "选择节点距离太近", "警告", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }else if(isProcessing[2] == true) {//新建路径
                if(e.getModifiers() == e.BUTTON1_MASK) {
                    Node temp = path.getNode(e.getX(), e.getY());
                    before_x = previous_x;before_y = previous_y;//同步
                    if(before_x == -1 || before_y == -1) {//说明要点下第一个点
                        if(temp == null) {
                            JOptionPane.showMessageDialog(frame, "当前位置没有节点", "警告", JOptionPane.ERROR_MESSAGE);
                        }else {
                            before_x = temp.getX();
                            before_y = temp.getY();
                            setPrevious(before_x, before_y);
                        }
                    }else {
                        if(temp == null) {
                            JOptionPane.showMessageDialog(frame, "当前位置没有节点", "警告", JOptionPane.ERROR_MESSAGE);
                        }else {
                            path.addModify(before_x, before_y, e.getX(), e.getY());
                            //自动转移到下一个节点
                            Node n = path.getNode(e.getX(), e.getY());
                            before_x = n.getX();
                            before_y = n.getY();
                            setPrevious(before_x, before_y);
                        }
                    }
                }else if(e.getModifiers() == e.BUTTON3_MASK) {//鼠标右键取消当前节点设置
                    before_x = -1;
                    before_y = -1;
                    setPrevious(-1, -1);
                }
            }else if(isProcessing[3] == true) {//删除节点
                Node temp = path.getNode(e.getX(), e.getY());
                if(temp == null) {
                    JOptionPane.showMessageDialog(frame, "当前位置没有节点", "警告", JOptionPane.ERROR_MESSAGE);
                }else {
                    //移除point对应与path node位置的节点
                    int place = path.node.indexOf(temp);
                    path.delNode(temp.getX(), temp.getY());

                    Integer[] newSize = new Integer[pointSize.length - 1];
                    System.arraycopy(pointSize, 0, newSize, 0, place);
                    System.arraycopy(pointSize, place + 1, newSize, place, pointSize.length - place - 1);
                    pointSize = newSize.clone();

//					System.out.println("place:"+place);
                    point.remove(place);
//					System.out.println(point.toString());
                }
            }else if(isProcessing[4]) {//删除路径
                Node temp = path.getNode(e.getX(), e.getY());
                before_x = previous_x;before_y = previous_y;//同步
                if(before_x == -1 &&before_y == -1) {
                    if(temp == null)
                        JOptionPane.showMessageDialog(frame, "当前位置没有节点", "警告", JOptionPane.ERROR_MESSAGE);
                    else {
                        before_x = temp.getX();
                        before_y = temp.getY();
                        setPrevious(before_x, before_y);
                    }
                }else {
                    if(temp == null)
                        JOptionPane.showMessageDialog(frame, "当前位置没有节点", "警告", JOptionPane.ERROR_MESSAGE);
                    else {
//						System.out.println("1:x,y "+before_x+" "+before_y +" 2:x,y "+temp.getX()+" "+temp.getY());
                        boolean ans = path.delPath(path.getNode(before_x, before_y), temp);
                        if(ans == false)
                            JOptionPane.showMessageDialog(frame, "没有可用的路径删除", "警告", JOptionPane.ERROR_MESSAGE);
                        else {
                            before_x = -1;
                            before_y = -1;
                            setPrevious(-1, -1);
                        }
                    }
                }
            }else if(isProcessing[0]) {//设置起点
                Node temp = path.getNode(e.getX(), e.getY());
                if(temp == null)
                    JOptionPane.showMessageDialog(frame, "当前位置没有节点", "警告", JOptionPane.ERROR_MESSAGE);
                else {
                    boolean ans = path.setStartOrEnd(temp.getX(), temp.getY(), 0);
                    if(ans==false)
                        JOptionPane.showMessageDialog(frame, "初始节点添加错误", "警告", JOptionPane.ERROR_MESSAGE);
                }
            }else if(isProcessing[6]) {//设置终点
                Node temp = path.getNode(e.getX(), e.getY());
                if(temp == null)
                    JOptionPane.showMessageDialog(frame, "当前位置没有节点", "警告", JOptionPane.ERROR_MESSAGE);
                else {
                    boolean ans = path.setStartOrEnd(temp.getX(), temp.getY(), 1);
                    if(ans==false)
                        JOptionPane.showMessageDialog(frame, "节点添加错误", "警告", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }

    //设置绘画区的及时表现节点
    public void setPrevious(int x,int y) {//当为-1-1时表示不绘画
        this.previous_x = x;
        this.previous_y = y;
    }
    public void setNow(int x,int y) {
        this.now_x = x;
        this.now_y = y;
    }

    public void drawAbout() {
        //关于界面
        about.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        about = new JFrame("最短路径问题");
        about.setIconImage(new ImageIcon("img/icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        about.setBounds(300,300,600,500);
        JTextArea TextTitle = new JTextArea();
        TextTitle.setFont(new Font("微软雅黑",Font.BOLD,30));
        TextTitle.setText("关于");
        about.add(TextTitle, BorderLayout.NORTH);
        JLabel TextContent = new JLabel();
        TextContent.setFont(new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT,20));
        TextContent.setText(
                "<html>最短路径问题 version1.0" + "<br><br>" +
                        "CopyRight \u00A9 2019 徐明瑞，段浩楠，姚帅。保留所有权利。" + "<br><br>" +
                        "联系我们：<a href='mailto:xmr666665@qq.com?subject=反馈'>xmr666665@qq.com</a></html>"
        );
        JPanel panel = new JPanel();panel.setBackground(Color.WHITE);
        panel.add(TextContent);
        about.add(panel);
        TextTitle.setBackground(Color.white);
        TextContent.setBackground(Color.WHITE);
        about.setBackground(Color.white);
        TextTitle.setVisible(true);
        TextContent.setVisible(true);
        TextTitle.setEditable(false);
    }
    public void drawHelp() {
        //帮助界面
        help.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        help.setIconImage(new ImageIcon("img/icon.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        help.setBounds(300, 300, 800, 700);
        JPanel panel = new JPanel(new BorderLayout());
        JPanel panelTitle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel panelContent = new JPanel();
        JLabel TextTitle = new JLabel();
        TextTitle.setFont(new Font("微软雅黑",Font.BOLD,30));
        TextTitle.setText("最短路径问题");
        panelTitle.add(TextTitle);
        panel.add(panelTitle,BorderLayout.NORTH);
        JTextArea TextContent = new JTextArea(30,40);
        TextContent.setFont(new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT,20));
        TextContent.setLineWrap(true);
        TextContent.setWrapStyleWord(true);
        TextContent.setLocation(400,400);
        TextContent.setText(
                "1.首先用户可以点击添加节点，然后在界面上的空白地方点击即可添加一个节点。添加完毕之后再点击取消操作以进行其他操作。\n\n" +
                        "2.添加完节点后可以添加路径，用户可以根据提示先后点击两个点就可以在这两个点之间添加一条路径。\n\n" +
                        "3.添加完节点和路径之后就可以设置起点和终点，默认起点为第一个添加的点。\n\n" +
                        "4.如果用户添加了不想添加的节点和路径，可以选择删除。删除节点即删除该节点和与该节点有关的路径，删除路径需要根据提示先后点击与该路径相关的两个点进行删除。\n\n" +
                        "5.整个图添加完成之后用户可以选择界面上方的算法，一共有三种算法：深度优先、广度优先、A*算法。选择功能之后，系统就会生成一条从起点到终点的最短路径，并在界面下方显示路径上的节点数，所用时间和路径的长度。\n\n" +
                        "6.用户可以选择清楚图像功能来清空面板。\n\n" +
                        "7.用户可以打开和保存文件，方便对图像进行操作。\n\n"
        );
        panelContent.add(TextContent,BorderLayout.SOUTH);
        panel.add(panelContent);
        help.add(panel);
        TextTitle.setBackground(Color.WHITE);
        TextContent.setBackground(Color.WHITE);
        panelTitle.setBackground(Color.WHITE);
        panelContent.setBackground(Color.WHITE);
        panel.setBackground(Color.WHITE);
        help.setBackground(Color.WHITE);
        TextTitle.setVisible(true);
        TextContent.setVisible(true);
        TextContent.setEditable(false);
    }
    public static void main(String[] args) {
        new Shortest_Path();
    }
}

