package zh.ppt.component;

import zh.ppt.main.Slide;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageComponent extends JComponent {
    private BufferedImage image;
    private String imagePath;

    public ImageComponent(Slide slide, String imagePath, int x, int y, int width, int height) {
        this.imagePath = imagePath;
        try {
            image = ImageIO.read(new File(imagePath));
            if (width > 0 && height > 0) {
                image = resizeImage(image, width, height);
            }
            setBounds(x, y, image.getWidth(), image.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
        MouseAdapter mouseAdapter = new MouseAdapter() {
            Point offset;

            @Override
            public void mousePressed(MouseEvent e) {
                offset = e.getPoint();
                requestFocus();
                setBorder(BorderFactory.createLineBorder(Color.BLUE));
                if (slide != null) {
                    slide.updateThumbnail(slide.getCurPageIdx());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = getLocation();
                int x = location.x + e.getX() - offset.x;
                int y = location.y + e.getY() - offset.y;
                setLocation(x, y);
                // 更新 ImageData 中的位置
                if (getParent() instanceof JPanel) {
                    Slide slide = (Slide) SwingUtilities.getWindowAncestor(getParent());
                    slide.updateImagePosition(ImageComponent.this);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBorder(null);
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image tmp = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }

    public static class FontChooser extends JPanel {
        private JComboBox<String> fontFamilyCombo;
        private JComboBox<Integer> fontSizeCombo;
        private JComboBox<String> fontStyleCombo;
        private JButton colorButton;
        private Color selectedColor;

        public FontChooser(Font initFont, Color initColor) {
            setLayout(new GridLayout(4, 2));

            add(new JLabel("字体："));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fonts = ge.getAvailableFontFamilyNames();
            fontFamilyCombo = new JComboBox<>(fonts);
            fontFamilyCombo.setSelectedItem(initFont.getFamily());
            add(fontFamilyCombo);

            add(new JLabel("大小："));
            Integer[] sizes = {8, 10, 12, 14, 16, 18, 24, 32, 48};
            fontSizeCombo = new JComboBox<>(sizes);
            fontSizeCombo.setSelectedItem(initFont.getSize());
            add(fontSizeCombo);

            add(new JLabel("风格："));
            String[] styles = {"常规", "粗体", "斜体"};
            fontStyleCombo = new JComboBox<>(styles);
            fontStyleCombo.setSelectedIndex(initFont.getStyle());
            add(fontStyleCombo);

            add(new JLabel("颜色："));
            colorButton = new JButton();
            colorButton.setBackground(initColor);
            colorButton.addActionListener(e -> {
                Color color = JColorChooser.showDialog(this, "选择颜色", selectedColor);
                if (color != null) {
                    selectedColor = color;
                    colorButton.setBackground(color);
                }
            });
            add(colorButton);

            selectedColor = initColor;
        }

        public Font getSelectedFont() {
            String fontFamily = (String) fontFamilyCombo.getSelectedItem();
            int fontSize = (Integer) fontSizeCombo.getSelectedItem();
            int fontStyle = fontStyleCombo.getSelectedIndex();
            return new Font(fontFamily, fontStyle, fontSize);
        }

        public Color getSelectedColor() {
            return selectedColor;
        }
    }
}
