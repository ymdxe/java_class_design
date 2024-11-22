package zh.ppt;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;



public class Slide {

    JFrame frame;
    JPanel mainPanel;        // 显示幻灯片页面的主面板
    JPanel edgePanel;        // 显示缩略图的面板
    JScrollPane edgeScrollPane; // 包含缩略图面板的滚动面板
    ArrayList<JPanel> pages; // 存储幻灯片页面的列表
    int curPageIdx = 0;      // 当前页面索引
    JButton addTextBoxButton; // 添加信息框按钮
    JButton setFontButton;    // 设置字体属性的按钮
    JTextPane selectedTextBox; // 当前选定的文本框

    Presentation presentation;   // 当前演示文稿
    boolean isModified = false;  // 是否有未保存的修改
    File currentFile = null;     // 当前文件

    public Slide() {
        frame = new JFrame();
        pages = new ArrayList<>();
        presentation = new Presentation();
    }

    /**
     * 设置窗体
     */
    void setFrame() {
        frame.setTitle("PPT演示");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        frame.setSize(screenSize.width / 4 * 3, screenSize.height / 4 * 3);
        frame.setLocation(screenSize.width / 8, screenSize.height / 8);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        edgePanel = new JPanel();
        edgePanel.setLayout(new BoxLayout(edgePanel, BoxLayout.Y_AXIS));
        edgePanel.setBackground(Color.LIGHT_GRAY);

        edgeScrollPane = new JScrollPane(edgePanel);
        edgeScrollPane.setPreferredSize(new Dimension(200, frame.getHeight()));
        frame.add(edgeScrollPane, BorderLayout.WEST);

        edgeScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JToolBar toolBar = new JToolBar();

        addTextBoxButton = new JButton("添加文本框");
        addTextBoxButton.addActionListener(e -> addTextBox());
        toolBar.add(addTextBoxButton);

        setFontButton = new JButton("设置字体");
        setFontButton.addActionListener(e -> setFontProperties());
        toolBar.add(setFontButton);

        frame.add(toolBar, BorderLayout.NORTH);
    }
    /**
     * 添加文本框
    */
    void addTextBox() {
        JPanel currentPage = pages.get(curPageIdx);

        JTextPane textPane = new JTextPane();
        textPane.setSize(200, 50);
        textPane.setLocation(50, 50);
        textPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        textPane.setFont(new Font("Serif", Font.PLAIN, 16));
        textPane.setForeground(Color.BLACK);

        // 为选择和拖动添加鼠标监听器
        MouseAdapter mouseAdapter = new MouseAdapter() {
            Point offset;

            @Override
            public void mousePressed(MouseEvent e) {
                selectedTextBox = (JTextPane) e.getSource();
                offset = e.getPoint();
                selectedTextBox.requestFocus();
                // 突出显示选定的文本框
                selectedTextBox.setBorder(BorderFactory.createLineBorder(Color.BLUE));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = selectedTextBox.getLocation();
                int x = location.x + e.getX() - offset.x;
                int y = location.y + e.getY() - offset.y;
                selectedTextBox.setLocation(x, y);
                isModified = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 移动后重置边框
                selectedTextBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        };

        textPane.addMouseListener(mouseAdapter);
        textPane.addMouseMotionListener(mouseAdapter);

        currentPage.add(textPane);
        currentPage.repaint();

        // Add text box data to current page's data
        TextBoxData textBoxData = new TextBoxData(
                "", 50, 50, 200, 50, "Serif", Font.PLAIN, 16, Color.BLACK);
        presentation.getPagesData().get(curPageIdx).addTextBoxData(textBoxData);

        isModified = true;
    }

    /**
     * 设置属性
     */

    void setFontProperties() {
        if (selectedTextBox == null) {
            JOptionPane.showMessageDialog(frame, "请先选择一个文本框！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Font currentFont = selectedTextBox.getFont();
        Color currentColor = selectedTextBox.getForeground();

        FontChooser fontChooser = new FontChooser(currentFont, currentColor);
        int result = JOptionPane.showConfirmDialog(frame, fontChooser, "选择字体",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Font selectedFont = fontChooser.getSelectedFont();
            Color selectedColor = fontChooser.getSelectedColor();

            selectedTextBox.setFont(selectedFont);
            selectedTextBox.setForeground(selectedColor);

            isModified = true;

            // Update the corresponding TextBoxData
            int index = getTextBoxIndex(selectedTextBox);
            if (index != -1) {
                TextBoxData data = presentation.getPagesData().get(curPageIdx).getTextBoxes().get(index);
                data.setFontName(selectedFont.getName());
                data.setFontStyle(selectedFont.getStyle());
                data.setFontSize(selectedFont.getSize());
                data.setTextColor(selectedColor);
            }
        }
    }

    int getTextBoxIndex(JTextPane textPane) {
        JPanel currentPage = pages.get(curPageIdx);
        Component[] components = currentPage.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == textPane) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 显示窗体
     */
    void show() {
        frame.setVisible(true);
    }

    /**
     * 新建幻灯片
     */
    void createNewSlide() {
        if (isModified) {
            int option = JOptionPane.showConfirmDialog(frame, "当前文件尚未保存，是否保存？", "提示",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.CANCEL_OPTION) {
                return; // 取消操作
            } else if (option == JOptionPane.YES_OPTION) {
                if (!savePresentation()) {
                    return; // 保存失败或取消
                }
            }
            // 否则继续新建
        }

        pages.clear();
        edgePanel.removeAll();
        curPageIdx = 0;
        presentation.clear();
        currentFile = null;
        isModified = false;

        addEmptyPage();
        displayPage(curPageIdx);
    }

    /**
     * 新建空白页面
     */
    void addEmptyPage() {
        JPanel newPage = new JPanel();
        newPage.setBackground(Color.WHITE);
        newPage.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        newPage.setLayout(null);
        newPage.setPreferredSize(new Dimension(800, 600));

        newPage.setSize(newPage.getPreferredSize());
        newPage.validate();

        pages.add(newPage);

        // 添加页面数据
        PageData pageData = new PageData();
        presentation.addPageData(pageData);

        addThumbnail(newPage, pages.size() - 1);

        isModified = true;

        JOptionPane.showMessageDialog(frame, "新幻灯片已创建！", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 添加缩略图
     */
    void addThumbnail(JPanel page, int pageIndex) {
        BufferedImage thumbnailImage = createThumbnail(page, 160, 120);
        JLabel thumbnailLabel = new JLabel(new ImageIcon(thumbnailImage));
        thumbnailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        thumbnailLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        thumbnailLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        thumbnailLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayPage(pageIndex);
            }
        });

        edgePanel.add(thumbnailLabel);
        edgePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        edgePanel.revalidate();
        edgePanel.repaint();
    }

    /**
     * 创建缩略图
     */
    BufferedImage createThumbnail(JPanel panel, int width, int height) {
        // 确保面板有有效的大小
        panel.setSize(panel.getPreferredSize());
        panel.validate();

        // 创建图像
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        panel.paint(g2);
        g2.dispose();

        // 缩放图像
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        // 创建缩略图
        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = thumbnail.createGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();

        // 转换颜色空间，移除错误的 ICC 配置文件
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null);
        op.filter(thumbnail, thumbnail);

        return thumbnail;
    }



    JTextPane createTextPaneFromData(TextBoxData data) {
        JTextPane textPane = new JTextPane();
        textPane.setText(data.getTextContent());
        textPane.setBounds(data.getX(), data.getY(), data.getWidth(), data.getHeight());
        textPane.setFont(new Font(data.getFontName(), data.getFontStyle(), data.getFontSize()));
        textPane.setForeground(data.getTextColor());
        textPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Add mouse listeners for selection and dragging
        MouseAdapter mouseAdapter = new MouseAdapter() {
            Point offset;

            @Override
            public void mousePressed(MouseEvent e) {
                selectedTextBox = (JTextPane) e.getSource();
                offset = e.getPoint();
                selectedTextBox.requestFocus();
                selectedTextBox.setBorder(BorderFactory.createLineBorder(Color.BLUE));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = selectedTextBox.getLocation();
                int x = location.x + e.getX() - offset.x;
                int y = location.y + e.getY() - offset.y;
                selectedTextBox.setLocation(x, y);
                isModified = true;

                // Update position in TextBoxData
                int index = getTextBoxIndex(selectedTextBox);
                if (index != -1) {
                    TextBoxData data = presentation.getPagesData().get(curPageIdx).getTextBoxes().get(index);
                    data.setX(x);
                    data.setY(y);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedTextBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        };

        textPane.addMouseListener(mouseAdapter);
        textPane.addMouseMotionListener(mouseAdapter);

        // Add key listener to update text content in TextBoxData
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                isModified = true;
                int index = getTextBoxIndex(textPane);
                if (index != -1) {
                    TextBoxData data = presentation.getPagesData().get(curPageIdx).getTextBoxes().get(index);
                    data.setTextContent(textPane.getText());
                }
            }
        });

        return textPane;
    }


    /**
     * 显示指定页面
     */
    void displayPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < pages.size()) {
            curPageIdx = pageIndex;
            mainPanel.removeAll();

            JPanel page = pages.get(pageIndex);
            page.removeAll(); // Clear existing components

            // Reconstruct text boxes from PageData
            PageData pageData = presentation.getPagesData().get(pageIndex);
            for (TextBoxData data : pageData.getTextBoxes()) {
                JTextPane textPane = createTextPaneFromData(data);
                page.add(textPane);
            }

            mainPanel.add(pages.get(pageIndex), BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();

            updateThumbnailBorders();
        }
    }

    /**
     * 更新缩略图边框
     */
    void updateThumbnailBorders() {
        Component[] components = edgePanel.getComponents();
        int thumbnailIndex = 0;
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (thumbnailIndex == curPageIdx) {
                    label.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                } else {
                    label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                }
                thumbnailIndex++;
            }
        }
    }

    /**
     * 设置菜单
     */
    void setMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu menu1 = new JMenu("文件");
        JMenu menu2 = new JMenu("开始");
        JMenu menu3 = new JMenu("插入");

        JMenuItem itemNew = new JMenuItem("新建");
        JMenuItem itemOpen = new JMenuItem("打开");
        JMenuItem itemSave = new JMenuItem("保存");

        JMenuItem newSlide = new JMenuItem("新建幻灯片");

        itemNew.addActionListener(e -> {
            createNewSlide();
        });

        itemOpen.addActionListener(e -> {
            openPresentation();
        });

        itemSave.addActionListener(e -> {
            savePresentation();
        });

        newSlide.addActionListener(e -> {
            addEmptyPage();
        });

        JMenuItem item4 = new JMenuItem("图片");
        JMenu subMenu = new JMenu("形状");
        JMenuItem shape1 = new JMenuItem("直线");
        JMenuItem shape2 = new JMenuItem("矩形");
        JMenuItem shape3 = new JMenuItem("圆");
        JMenuItem shape4 = new JMenuItem("椭圆");

        menu1.add(itemNew);
        menu1.add(itemOpen);
        menu1.add(itemSave);

        menu2.add(newSlide);

        menu3.add(item4);
        subMenu.add(shape1);
        subMenu.add(shape2);
        subMenu.add(shape3);
        subMenu.add(shape4);
        menu3.addSeparator();
        menu3.add(subMenu);

        mb.add(menu1);
        mb.add(menu2);
        mb.add(menu3);

        frame.setJMenuBar(mb);
    }

    /**
     * 保存演示文稿
     */
    boolean savePresentation() {
        if (currentFile == null) {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
            } else {
                return false;
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(currentFile))) {
            oos.writeObject(presentation);
            isModified = false;
            JOptionPane.showMessageDialog(frame, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * 打开演示文稿
     */
    void openPresentation() {
        if (isModified) {
            int option = JOptionPane.showConfirmDialog(frame, "当前文件尚未保存，是否保存？", "提示",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.CANCEL_OPTION) {
                return;
            } else if (option == JOptionPane.YES_OPTION) {
                if (!savePresentation()) {
                    return;
                }
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(currentFile))) {
                presentation = (Presentation) ois.readObject();
                isModified = false;

                pages.clear();
                edgePanel.removeAll();

                for (int i = 0; i < presentation.getPagesData().size(); i++) {
                    JPanel newPage = new JPanel();
                    newPage.setBackground(Color.WHITE);
                    newPage.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    newPage.setLayout(null);
                    newPage.setPreferredSize(new Dimension(800, 600));
                    newPage.setSize(newPage.getPreferredSize());
                    newPage.validate();

                    PageData pageData = presentation.getPagesData().get(i);
                    for (TextBoxData data : pageData.getTextBoxes()) {
                        JTextPane textPane = createTextPaneFromData(data);
                        newPage.add(textPane);
                    }

                    pages.add(newPage);
                    addThumbnail(newPage, i);
                }

                if (!pages.isEmpty()) {
                    curPageIdx = 0;
                    displayPage(curPageIdx);
                }

                JOptionPane.showMessageDialog(frame, "打开成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "打开失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        Slide jFrame = new Slide();
        jFrame.setFrame();
        jFrame.setMenu();
        jFrame.createNewSlide();
        jFrame.show();
    }
}
