//package zh.ppt;
//
//import java.awt.*;
//import javax.swing.*;
//
//public class JFrameDemo {
//
//    JFrame frame;
//
//    public JFrameDemo() {
//        // 创建窗体
//        frame = new JFrame();
//    }
//
//    /**
//     * 设置窗体
//     */
//    void setFrame() {
//        // 设置窗体标题
//        frame.setTitle("PPT演示");
//
//        // 获得显示屏幕的尺寸
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//
//        // 设置窗体大小为当前屏幕大小的1/4，且居中显示
//        frame.setSize(screenSize.width / 4 * 3, screenSize.height / 4 * 3);
//        frame.setLocation(screenSize.width / 8, screenSize.height / 8);
//
//        // 设置窗体关闭的退出操作
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    }
//
//    /**
//     * 使窗体可见
//     */
//    void show() {
//        frame.setVisible(true);
//    }
//
//    /**
//     * 在窗体上添加组件
//     */
//    void addComponent() {
//        Container c = frame.getContentPane();
//
//        // 创建各组件
//        JLabel label = new JLabel("输入：");// 标签
//        JTextField textField = new JTextField(15);// 文本框
//        String items[] = { "沈阳", "大连", "北京" };
//        JComboBox comboBox = new JComboBox(items);// 下拉列表
//        JButton button1 = new JButton("确定");// 按钮1
//        JButton button2 = new JButton("can..");// 按钮2
//        JTextArea textArea = new JTextArea(5, 15);// 文本域
//
//        // 三个复选框，用Panel网格布局管理
//        JCheckBox check1 = new JCheckBox("one", true);
//        JCheckBox check2 = new JCheckBox("two");
//        JCheckBox check3 = new JCheckBox("three");
//        JPanel p = new JPanel(new GridLayout(3, 1));
//        p.add(check1);
//        p.add(check2);
//        p.add(check3);
//
//        // 单选按钮，需要添加至同一按钮组
//        ButtonGroup g = new ButtonGroup();// 按钮组
//        JRadioButton rb1 = new JRadioButton("male");// 单选按钮1
//        JRadioButton rb2 = new JRadioButton("female", true);// 单选按钮2
//        g.add(rb1);
//        g.add(rb2);
//
//        // 滚动面板
//        JScrollPane sp = new JScrollPane();
//        JTextArea ta = new JTextArea("", 10, 50);
//        sp.getViewport().setView(ta);
//
//        // 设置容器的布局管理器
//        c.setLayout(new FlowLayout());
//
//        // 向容器中添加组件
//        c.add(label);
//        c.add(textField);
//        c.add(comboBox);
//        c.add(button1);
//        c.add(button2);
//        c.add(textArea);
//        c.add(p);
//        c.add(rb1);
//        c.add(rb2);
//        c.add(sp);
//
//    }
//
//    /**
//     * 设置菜单
//     */
//    void setMenu() {
//        JMenuBar mb = new JMenuBar();// 菜单栏
//        JMenu menu1 = new JMenu("文件");// 菜单1
//        JMenu menu2 = new JMenu("插入");// 菜单2
//        JMenuItem item1 = new JMenuItem("新建");// 菜单项
//        JMenuItem item2 = new JMenuItem("打开");// 菜单项
//        JMenuItem item3 = new JMenuItem("保存");// 子菜单
//
//        JMenuItem item4 = new JMenuItem("图片");
//        JMenu subMenu = new JMenu("形状");
//        JMenuItem shape1 = new JMenuItem("直线");
//        JMenuItem shape2 = new JMenuItem("矩形");
//        JMenuItem shape3 = new JMenuItem("圆");
//        JMenuItem shape4 = new JMenuItem("椭圆");
//
//        // 将菜单项加入菜单1
//        menu1.add(item1);
//        menu1.add(item2);
//        menu1.add(item3);
//
//        // 将菜单项加入菜单2
//        menu2.add(item4);
//        subMenu.add(shape1);
//        subMenu.add(shape2);
//        subMenu.add(shape3);
//        subMenu.add(shape4);
//        menu2.addSeparator();
//        menu2.add(subMenu);
//
//        // 将菜单加入菜单条
//        mb.add(menu1);
//        mb.add(menu2);
//
//        // 在窗体中显示菜单
//        frame.setJMenuBar(mb);
//    }
//
//    public static void main(String[] args) {
//        // TODO Auto-generated method stub
//
//        JFrameDemo jFrame = new JFrameDemo();
//        // 设置窗体
//        jFrame.setFrame();
//
//        // 添加组件
//        jFrame.addComponent();
//
//        // 设置菜单
//        jFrame.setMenu();
//
//        // 最后显示窗体
//        jFrame.show();
//
//    }
//
//}
