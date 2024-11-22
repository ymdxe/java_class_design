package zh.ppt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class ShapeComponent extends JComponent {
    private int shapeType;
    private Color fillColor;
    private Color borderColor;
    private boolean isSelected = false; // 是否被选中
    private double rotation = 0; // 旋转角度，单位为弧度

    // 调整句柄的大小
    private static final int HANDLE_SIZE = 8;

    // 当前拖拽的句柄类型
    private int resizingHandle = -1;


    // 定义句柄类型常量
    public static final int HANDLE_NONE = -1;
    private static final int HANDLE_NW = 0;
    private static final int HANDLE_NE = 1;
    private static final int HANDLE_SW = 2;
    private static final int HANDLE_SE = 3;
    private static final int HANDLE_ROTATE = 4;

    // 添加偏移量变量
    private Point offset;
    private Slide slide;

    public ShapeComponent(Slide slide, int shapeType, int x, int y, int width, int height, Color fillColor, Color borderColor) {
        this.slide = slide; // 添加 Slide 的引用
        this.shapeType = shapeType;
        this.fillColor = fillColor;
        this.borderColor = borderColor;

        setBounds(x, y, width, height);
        setOpaque(false); // 使背景透明

        // 添加鼠标监听器，支持移动
        MouseAdapter mouseAdapter = new MouseAdapter() {
            Point offset;

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
                isSelected = true;
                repaint();
                // 判断点击的位置是否在调整句柄上
                resizingHandle = getHandleAtPoint(e.getPoint());
                if (resizingHandle == HANDLE_NONE) {
                    // 开始拖拽移动
                    offset = e.getPoint();
                }
                // 通知父组件选中了此形状
                selectThisShape();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = getLocation();
                int x = location.x + e.getX() - offset.x;
                int y = location.y + e.getY() - offset.y;
                setLocation(x, y);
                // 更新 ShapeData 中的位置
                if (getParent() instanceof JPanel) {
                    Slide slide = (Slide) SwingUtilities.getWindowAncestor(getParent());
                    slide.updateShapePosition(ShapeComponent.this);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBorder(null);
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
                isSelected = true;
                repaint();
                // 判断点击的位置是否在调整句柄上
                resizingHandle = getHandleAtPoint(e.getPoint());
                if (resizingHandle == HANDLE_NONE) {
                    // 开始拖拽移动
                    offset = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                resizingHandle = HANDLE_NONE;
                offset = null;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // 通知父组件选中了此形状
                selectThisShape();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (resizingHandle != HANDLE_NONE) {
                    // 正在调整大小或旋转
                    resizeOrRotate(e.getPoint());
                } else if (offset != null) {
                    // 正在拖拽移动
                    int x = getX() + e.getX() - offset.x;
                    int y = getY() + e.getY() - offset.y;
                    setLocation(x, y);
                    // 更新 ShapeData 中的位置
                    updateShapeDataPosition();
                }
            }
        });

        // 获得焦点以接收键盘事件
        setFocusable(true);

        // 添加键盘监听器，用于取消选中
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    isSelected = false;
                    repaint();
                }
            }
        });
    }

    public int getResizingHandle() {
        return resizingHandle;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // 抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Shape shape = null;
        int w = getWidth();
        int h = getHeight();
        g2.translate(w / 2, h / 2);
        g2.rotate(rotation);

        switch (shapeType) {
            case ShapeData.TYPE_LINE:
                shape = new Line2D.Float(-w / 2, -h / 2, w / 2, h / 2);
                break;
            case ShapeData.TYPE_RECTANGLE:
                shape = new Rectangle2D.Float(-w / 2, -h / 2, w, h);
                break;
            case ShapeData.TYPE_CIRCLE:
                int diameter = Math.min(w, h);
                shape = new Ellipse2D.Float(-diameter / 2, -diameter / 2, diameter, diameter);
                break;
            case ShapeData.TYPE_ELLIPSE:
                shape = new Ellipse2D.Float(-w / 2, -h / 2, w, h);
                break;
        }

        if (shape != null) {
            if (fillColor != null) {
                g2.setColor(fillColor);
                g2.fill(shape);
            }
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.draw(shape);
            }
        }
        g2.dispose();
        // 绘制选中状态的边框和调整句柄
        if (isSelected) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.BLUE);
            // 绘制边框
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            // 绘制调整句柄
            drawHandles(g2d);

            g2d.dispose();
        }
    }
    // 绘制调整句柄
    private void drawHandles(Graphics2D g2d) {
        int w = getWidth();
        int h = getHeight();

        // 四个角的句柄
        g2d.fillRect(0 - HANDLE_SIZE / 2, 0 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // NW
        g2d.fillRect(w - HANDLE_SIZE / 2, 0 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // NE
        g2d.fillRect(0 - HANDLE_SIZE / 2, h - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // SW
        g2d.fillRect(w - HANDLE_SIZE / 2, h - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // SE

        // 旋转句柄（放在顶部中央）
        g2d.fillOval(w / 2 - HANDLE_SIZE / 2, 0 - HANDLE_SIZE * 2, HANDLE_SIZE, HANDLE_SIZE);
    }

    // 获取点击的点在哪个句柄上
    private int getHandleAtPoint(Point p) {
        int w = getWidth();
        int h = getHeight();

        Rectangle handleNW = new Rectangle(0 - HANDLE_SIZE / 2, 0 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
        Rectangle handleNE = new Rectangle(w - HANDLE_SIZE / 2, 0 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
        Rectangle handleSW = new Rectangle(0 - HANDLE_SIZE / 2, h - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
        Rectangle handleSE = new Rectangle(w - HANDLE_SIZE / 2, h - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
        Ellipse2D handleRotate = new Ellipse2D.Float(w / 2 - HANDLE_SIZE / 2, 0 - HANDLE_SIZE * 2, HANDLE_SIZE, HANDLE_SIZE);

        if (handleNW.contains(p)) {
            return HANDLE_NW;
        } else if (handleNE.contains(p)) {
            return HANDLE_NE;
        } else if (handleSW.contains(p)) {
            return HANDLE_SW;
        } else if (handleSE.contains(p)) {
            return HANDLE_SE;
        } else if (handleRotate.contains(p)) {
            return HANDLE_ROTATE;
        } else {
            return HANDLE_NONE;
        }
    }

    // 调整大小或旋转
    private void resizeOrRotate(Point p) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        switch (resizingHandle) {
            case HANDLE_NW:
                int newW = w + x - (x + p.x);
                int newH = h + y - (y + p.y);
                setBounds(x + p.x, y + p.y, newW, newH);
                break;
            case HANDLE_NE:
                newW = p.x;
                newH = h + y - (y + p.y);
                setBounds(x, y + p.y, newW, newH);
                break;
            case HANDLE_SW:
                newW = w + x - (x + p.x);
                newH = p.y;
                setBounds(x + p.x, y, newW, newH);
                break;
            case HANDLE_SE:
                newW = p.x;
                newH = p.y;
                setSize(newW, newH);
                break;
            case HANDLE_ROTATE:
                // 计算旋转角度
                double centerX = w / 2;
                double centerY = h / 2;
                double deltaX = p.x - centerX;
                double deltaY = p.y - centerY;
                rotation = Math.atan2(deltaY, deltaX);
                repaint();
                break;
        }

        // 更新 ShapeData 中的大小和旋转角度
        updateShapeDataSizeAndRotation();
    }
    // 更新 ShapeData 中的位置
    private void updateShapeDataPosition() {
        // 从父组件获取 Slide 实例，更新 ShapeData
        Container parent = getParent();
        if (parent != null) {
            Slide slide = (Slide) SwingUtilities.getWindowAncestor(parent);
            slide.updateShapePosition(this);
        }
    }

    // 更新 ShapeData 中的大小和旋转角度
    private void updateShapeDataSizeAndRotation() {
        // 从父组件获取 Slide 实例，更新 ShapeData
        Container parent = getParent();
        if (parent != null) {
            Slide slide = (Slide) SwingUtilities.getWindowAncestor(parent);
            slide.updateShapeSizeAndRotation(this);
        }
    }

    // 通知父组件选中了此形状
    private void selectThisShape() {

        if (slide != null) {
            slide.setSelectedShape(this);
        }

        Container parent = getParent();
        if (parent != null) {
            for (Component comp : parent.getComponents()) {
                if (comp instanceof ShapeComponent && comp != this) {
                    ((ShapeComponent) comp).setSelected(false);
                }
            }
        }
        isSelected = true;
        repaint();
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        repaint();
    }

    public boolean isSelected() {
        return isSelected;
    }

    // Getter 和 Setter
    public double getRotation() { return rotation; }
    public void setRotation(double rotation) { this.rotation = rotation; repaint(); }

    // Getters and setters
    public int getShapeType() { return shapeType; }
    public void setShapeType(int shapeType) { this.shapeType = shapeType; repaint(); }

    public Color getFillColor() { return fillColor; }
    public void setFillColor(Color fillColor) { this.fillColor = fillColor; repaint(); }

    public Color getBorderColor() { return borderColor; }
    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; repaint(); }
}
