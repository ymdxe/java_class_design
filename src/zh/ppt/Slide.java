package zh.ppt;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.*;

public class Slide extends JFrame {

    JPanel mainPanel;        // 显示幻灯片页面的主面板
    JPanel edgePanel;        // 显示缩略图的面板
    JScrollPane edgeScrollPane; // 包含缩略图面板的滚动面板
    ArrayList<JPanel> pages; // 存储幻灯片页面的列表
    int curPageIdx = 0;      // 当前页面索引

    Presentation presentation;   // 当前演示文稿
    boolean isModified = false;  // 是否有未保存的修改
    File currentFile = null;     // 当前文件

    JButton addTextBoxButton; // 添加文本框按钮
    JButton setFontButton;    // 设置字体按钮
    JTextPane selectedTextBox; // 当前选中的文本框

    public Slide() {
        pages = new ArrayList<>();
        presentation = new Presentation();
        setFrame();
        setMenu();
        createNewSlide();
        showFrame();
    }

    public void setSelectedShape(ShapeComponent shape) {
        this.selectedShape = shape;
    }

    // 添加一个按钮，用于修改形状填充颜色
    JButton setShapeFillColorButton;

    // 当前选中的形状
    ShapeComponent selectedShape;
    /**
     * 设置窗体
     */
    void setFrame() {
        setTitle("PPT演示");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setSize(screenSize.width / 4 * 3, screenSize.height / 4 * 3);
        setLocation(screenSize.width / 8, screenSize.height / 8);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        edgePanel = new JPanel();
        edgePanel.setLayout(new BoxLayout(edgePanel, BoxLayout.Y_AXIS));
        edgePanel.setBackground(Color.LIGHT_GRAY);

        edgeScrollPane = new JScrollPane(edgePanel);
        edgeScrollPane.setPreferredSize(new Dimension(200, getHeight()));
        add(edgeScrollPane, BorderLayout.WEST);

        edgeScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 创建工具栏
        JToolBar toolBar = new JToolBar();

        addTextBoxButton = new JButton("添加文本框");
        addTextBoxButton.addActionListener(e -> addTextBox());
        toolBar.add(addTextBoxButton);

        setFontButton = new JButton("设置字体");
        setFontButton.addActionListener(e -> setFontProperties());
        toolBar.add(setFontButton);

        setShapeFillColorButton = new JButton("修改形状填充颜色");
        setShapeFillColorButton.addActionListener(e -> setShapeFillColor());
        toolBar.add(setShapeFillColorButton);


        add(toolBar, BorderLayout.NORTH);
    }
    // 实现 setShapeFillColor() 方法
    void setShapeFillColor() {
        if (selectedShape == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个形状！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Color newColor = JColorChooser.showDialog(this, "选择填充颜色", selectedShape.getFillColor());
        if (newColor != null) {
            selectedShape.setFillColor(newColor);
            isModified = true;

            // 更新 ShapeData 中的填充颜色
            updateShapeFillColor(selectedShape);
        }
    }
    // 更新 ShapeData 中的填充颜色
    void updateShapeFillColor(ShapeComponent shapeComp) {
        int index = getShapeIndex(shapeComp);
        if (index != -1) {
            ShapeData shapeData = presentation.getPagesData().get(curPageIdx).getShapes().get(index);
            shapeData.setFillColor(shapeComp.getFillColor());
        }
        isModified = true;
    }
    // 更新 ShapeData 中的大小和旋转角度
    void updateShapeSizeAndRotation(ShapeComponent shapeComp) {
        int index = getShapeIndex(shapeComp);
        if (index != -1) {
            ShapeData shapeData = presentation.getPagesData().get(curPageIdx).getShapes().get(index);
            shapeData.setWidth(shapeComp.getWidth());
            shapeData.setHeight(shapeComp.getHeight());
            shapeData.setRotation(shapeComp.getRotation());
        }
        isModified = true;
    }

    /**
     * 显示窗体
     */
    void showFrame() {
        setVisible(true);
    }

    /**
     * 新建幻灯片
     */
    void createNewSlide() {
        if (isModified) {
            int option = JOptionPane.showConfirmDialog(this, "当前文件尚未保存，是否保存？", "提示",
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

        // Initialize PageData for the new page
        PageData pageData = new PageData();
        presentation.addPageData(pageData);

        addThumbnail(newPage, pages.size() - 1);

        isModified = true;

        JOptionPane.showMessageDialog(this, "新幻灯片已创建！", "提示", JOptionPane.INFORMATION_MESSAGE);
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

        // 添加鼠标监听器，支持选取和移动
        MouseAdapter mouseAdapter = new MouseAdapter() {
            Point offset;

            @Override
            public void mousePressed(MouseEvent e) {
                selectedTextBox = (JTextPane) e.getSource();
                offset = e.getPoint();
                selectedTextBox.requestFocus();
                // 高亮选中的文本框
                selectedTextBox.setBorder(BorderFactory.createLineBorder(Color.BLUE));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = selectedTextBox.getLocation();
                int x = location.x + e.getX() - offset.x;
                int y = location.y + e.getY() - offset.y;
                selectedTextBox.setLocation(x, y);
                isModified = true;

                // 更新 TextBoxData 中的位置
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

        // 添加键盘监听器，更新文本内容
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

        currentPage.add(textPane);
        currentPage.repaint();

        // 添加文本框数据到当前页面
        TextBoxData textBoxData = new TextBoxData(
                "", 50, 50, 200, 50, "Serif", Font.PLAIN, 16, Color.BLACK);
        presentation.getPagesData().get(curPageIdx).addTextBoxData(textBoxData);

        isModified = true;
    }

    /**
     * 设置字体属性
     */
    void setFontProperties() {
        if (selectedTextBox == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个文本框！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Font currentFont = selectedTextBox.getFont();
        Color currentColor = selectedTextBox.getForeground();

        FontChooser fontChooser = new FontChooser(currentFont, currentColor);
        int result = JOptionPane.showConfirmDialog(this, fontChooser, "选择字体",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Font selectedFont = fontChooser.getSelectedFont();
            Color selectedColor = fontChooser.getSelectedColor();

            selectedTextBox.setFont(selectedFont);
            selectedTextBox.setForeground(selectedColor);

            isModified = true;

            // 更新对应的 TextBoxData
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

    /**
     * 获取文本框在当前页面中的索引
     */
    int getTextBoxIndex(JTextPane textPane) {
        JPanel currentPage = pages.get(curPageIdx);
        Component[] components = currentPage.getComponents();
        int textBoxCount = 0;
        for (Component comp : components) {
            if (comp instanceof JTextPane) {
                if (comp == textPane) {
                    return textBoxCount;
                }
                textBoxCount++;
            }
        }
        return -1;
    }

    /**
     * 添加形状
     */
    void addShape(int shapeType) {
        JPanel currentPage = pages.get(curPageIdx);

        // 弹出颜色选择对话框
        Color fillColor = JColorChooser.showDialog(this, "选择填充颜色", Color.WHITE);
        Color borderColor = JColorChooser.showDialog(this, "选择边框颜色", Color.BLACK);

        // 默认大小和位置
        int x = 50;
        int y = 50;
        int width = 100;
        int height = 100;

        ShapeComponent shapeComp = new ShapeComponent(this, shapeType, x, y, width, height, fillColor, borderColor);

        currentPage.add(shapeComp);
        currentPage.repaint();

        // 添加 ShapeData 到 PageData
        ShapeData shapeData = new ShapeData(shapeType, x, y, width, height, fillColor, borderColor, 0);
        presentation.getPagesData().get(curPageIdx).addShapeData(shapeData);

        isModified = true;
    }

    /**
     * 更新 ShapeComponent 的位置到 ShapeData
     */
    void updateShapePosition(ShapeComponent shapeComp) {
        int index = getShapeIndex(shapeComp);
        if (index != -1) {
            ShapeData shapeData = presentation.getPagesData().get(curPageIdx).getShapes().get(index);
            shapeData.setX(shapeComp.getX());
            shapeData.setY(shapeComp.getY());
        }
        isModified = true;
    }

    /**
     * 获取形状在当前页面中的索引
     */
    int getShapeIndex(ShapeComponent shapeComp) {
        JPanel currentPage = pages.get(curPageIdx);
        Component[] components = currentPage.getComponents();
        int shapeCount = 0;
        for (Component comp : components) {
            if (comp instanceof ShapeComponent) {
                if (comp == shapeComp) {
                    return shapeCount;
                }
                shapeCount++;
            }
        }
        return -1;
    }

    /**
     * 添加图片
     */
    void addImage() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String imagePath = file.getAbsolutePath();

            // 默认大小和位置
            int x = 50;
            int y = 50;
            int width = 200; // 您可以根据需要调整默认大小
            int height = 150;

            JPanel currentPage = pages.get(curPageIdx);

            ImageComponent imageComp = new ImageComponent(imagePath, x, y, width, height);

            currentPage.add(imageComp);
            currentPage.repaint();

            // 添加 ImageData 到 PageData
            ImageData imageData = new ImageData(imagePath, x, y, width, height);
            presentation.getPagesData().get(curPageIdx).addImageData(imageData);

            isModified = true;
        }
    }

    /**
     * 更新 ImageComponent 的位置到 ImageData
     */
    void updateImagePosition(ImageComponent imageComp) {
        int index = getImageIndex(imageComp);
        if (index != -1) {
            ImageData imageData = presentation.getPagesData().get(curPageIdx).getImages().get(index);
            imageData.setX(imageComp.getX());
            imageData.setY(imageComp.getY());
        }
        isModified = true;
    }

    /**
     * 获取图片在当前页面中的索引
     */
    int getImageIndex(ImageComponent imageComp) {
        JPanel currentPage = pages.get(curPageIdx);
        Component[] components = currentPage.getComponents();
        int imageCount = 0;
        for (Component comp : components) {
            if (comp instanceof ImageComponent) {
                if (comp == imageComp) {
                    return imageCount;
                }
                imageCount++;
            }
        }
        return -1;
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

        // 创建页面的图像
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        panel.paint(g2);
        g2.dispose();

        // 缩放图像为缩略图
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        // 将缩放后的图像转换为 BufferedImage
        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = thumbnail.createGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();

        // 转换颜色空间，移除错误的 ICC 配置文件
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null);
        op.filter(thumbnail, thumbnail);

        return thumbnail;
    }

    /**
     * 显示指定页面
     */
    void displayPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < pages.size()) {
            curPageIdx = pageIndex;
            mainPanel.removeAll();

            JPanel page = pages.get(pageIndex);
            page.removeAll(); // 清除现有组件

            selectedShape = null; // 清空当前选中的形状
            selectedTextBox = null; // 清空当前选中的文本框


            // 从 PageData 重建文本框
            PageData pageData = presentation.getPagesData().get(pageIndex);
            for (TextBoxData data : pageData.getTextBoxes()) {
                JTextPane textPane = createTextPaneFromData(data);
                page.add(textPane);
            }

            // 重建形状
            for (ShapeData data : pageData.getShapes()) {
                ShapeComponent shapeComp = createShapeComponentFromData(data);
                page.add(shapeComp);
            }

            // 重建图片
            for (ImageData data : pageData.getImages()) {
                ImageComponent imageComp = createImageComponentFromData(data);
                page.add(imageComp);
            }

            mainPanel.add(page, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();

            updateThumbnailBorders();
        }
    }

    /**
     * 从 TextBoxData 创建 JTextPane
     */
    JTextPane createTextPaneFromData(TextBoxData data) {
        JTextPane textPane = new JTextPane();
        textPane.setText(data.getTextContent());
        textPane.setBounds(data.getX(), data.getY(), data.getWidth(), data.getHeight());
        textPane.setFont(new Font(data.getFontName(), data.getFontStyle(), data.getFontSize()));
        textPane.setForeground(data.getTextColor());
        textPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // 添加鼠标监听器，支持选取和移动
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

                // 更新 TextBoxData 中的位置
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

        // 添加键盘监听器，更新文本内容
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
     * 从 ShapeData 创建 ShapeComponent
     */
    ShapeComponent createShapeComponentFromData(ShapeData data) {
        ShapeComponent shapeComp = new ShapeComponent(
                this,
                data.getShapeType(),
                data.getX(),
                data.getY(),
                data.getWidth(),
                data.getHeight(),
                data.getFillColor(),
                data.getBorderColor()
        );

        shapeComp.setRotation(data.getRotation());

        // 添加鼠标监听器，支持选取和移动
        MouseAdapter mouseAdapter = new MouseAdapter() {
            Point offset;

            @Override
            public void mousePressed(MouseEvent e) {
                offset = e.getPoint();
                shapeComp.requestFocus();
                shapeComp.setSelected(true);
                selectedShape = shapeComp;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (shapeComp.getResizingHandle() == ShapeComponent.HANDLE_NONE) {
                    Point location = shapeComp.getLocation();
                    int x = location.x + e.getX() - offset.x;
                    int y = location.y + e.getY() - offset.y;
                    shapeComp.setLocation(x, y);
                    isModified = true;

                    // 更新 ShapeData 中的位置
                    updateShapePosition(shapeComp);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                shapeComp.setBorder(null);
            }
        };

//        shapeComp.addMouseListener(mouseAdapter);
//        shapeComp.addMouseMotionListener(mouseAdapter);

        return shapeComp;
    }

    /**
     * 从 ImageData 创建 ImageComponent
     */
    ImageComponent createImageComponentFromData(ImageData data) {
        ImageComponent imageComp = new ImageComponent(
                data.getImagePath(),
                data.getX(),
                data.getY(),
                data.getWidth(),
                data.getHeight()
        );

        // 添加鼠标监听器，支持移动
        MouseAdapter mouseAdapter = new MouseAdapter() {
            Point offset;

            @Override
            public void mousePressed(MouseEvent e) {
                offset = e.getPoint();
                imageComp.requestFocus();
                imageComp.setBorder(BorderFactory.createLineBorder(Color.BLUE));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = imageComp.getLocation();
                int x = location.x + e.getX() - offset.x;
                int y = location.y + e.getY() - offset.y;
                imageComp.setLocation(x, y);
                isModified = true;

                // 更新 ImageData 中的位置
                updateImagePosition(imageComp);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                imageComp.setBorder(null);
            }
        };

        imageComp.addMouseListener(mouseAdapter);
        imageComp.addMouseMotionListener(mouseAdapter);

        return imageComp;
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

        JMenuItem itemImage = new JMenuItem("图片");
        JMenu subMenu = new JMenu("形状");
        JMenuItem shape1 = new JMenuItem("直线");
        JMenuItem shape2 = new JMenuItem("矩形");
        JMenuItem shape3 = new JMenuItem("圆");
        JMenuItem shape4 = new JMenuItem("椭圆");

        // 添加形状的动作监听器
        shape1.addActionListener(e -> addShape(ShapeData.TYPE_LINE));
        shape2.addActionListener(e -> addShape(ShapeData.TYPE_RECTANGLE));
        shape3.addActionListener(e -> addShape(ShapeData.TYPE_CIRCLE));
        shape4.addActionListener(e -> addShape(ShapeData.TYPE_ELLIPSE));

        // 添加插入图片的动作监听器
        itemImage.addActionListener(e -> addImage());

        menu1.add(itemNew);
        menu1.add(itemOpen);
        menu1.add(itemSave);

        menu2.add(newSlide);

        menu3.add(itemImage);
        subMenu.add(shape1);
        subMenu.add(shape2);
        subMenu.add(shape3);
        subMenu.add(shape4);
        menu3.addSeparator();
        menu3.add(subMenu);

        mb.add(menu1);
        mb.add(menu2);
        mb.add(menu3);

        setJMenuBar(mb);
    }

    /**
     * 保存演示文稿
     */
    boolean savePresentation() {
        if (currentFile == null) {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
            } else {
                return false;
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(currentFile))) {
            oos.writeObject(presentation);
            isModified = false;
            JOptionPane.showMessageDialog(this, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * 打开演示文稿
     */
    void openPresentation() {
        if (isModified) {
            int option = JOptionPane.showConfirmDialog(this, "当前文件尚未保存，是否保存？", "提示",
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
        int option = fileChooser.showOpenDialog(this);
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

                    // 重建文本框
                    PageData pageData = presentation.getPagesData().get(i);
                    for (TextBoxData data : pageData.getTextBoxes()) {
                        JTextPane textPane = createTextPaneFromData(data);
                        newPage.add(textPane);
                    }

                    // 重建形状
                    for (ShapeData data : pageData.getShapes()) {
                        ShapeComponent shapeComp = createShapeComponentFromData(data);
                        newPage.add(shapeComp);
                    }

                    // 重建图片
                    for (ImageData data : pageData.getImages()) {
                        ImageComponent imageComp = createImageComponentFromData(data);
                        newPage.add(imageComp);
                    }

                    pages.add(newPage);
                    addThumbnail(newPage, i);
                }

                if (!pages.isEmpty()) {
                    curPageIdx = 0;
                    displayPage(curPageIdx);
                }

                JOptionPane.showMessageDialog(this, "打开成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "打开失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new Slide();
    }
}
