package zh.ppt;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageComponent extends JComponent {
    private BufferedImage image;
    private String imagePath;

    public ImageComponent(String imagePath, int x, int y, int width, int height) {
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

        // 添加鼠标监听器，支持移动
        MouseAdapter mouseAdapter = new MouseAdapter() {
            Point offset;

            @Override
            public void mousePressed(MouseEvent e) {
                offset = e.getPoint();
                requestFocus();
                setBorder(BorderFactory.createLineBorder(Color.BLUE));
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

    // Getters and setters
    public BufferedImage getImage() { return image; }
    public void setImage(BufferedImage image) { this.image = image; repaint(); }

    public String getImagePath() { return imagePath; }
}
