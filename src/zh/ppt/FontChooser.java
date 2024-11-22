package zh.ppt;

import javax.swing.*;
import java.awt.*;

public class FontChooser extends JPanel {
    private JComboBox<String> fontFamilyCombo;
    private JComboBox<Integer> fontSizeCombo;
    private JComboBox<String> fontStyleCombo;
    private JButton colorButton;
    private Color selectedColor;

    public FontChooser(Font initialFont, Color initialColor) {
        setLayout(new GridLayout(4, 2));

        // Font family
        add(new JLabel("字体："));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();
        fontFamilyCombo = new JComboBox<>(fonts);
        fontFamilyCombo.setSelectedItem(initialFont.getFamily());
        add(fontFamilyCombo);

        // Font size
        add(new JLabel("大小："));
        Integer[] sizes = {8, 10, 12, 14, 16, 18, 24, 32, 48};
        fontSizeCombo = new JComboBox<>(sizes);
        fontSizeCombo.setSelectedItem(initialFont.getSize());
        add(fontSizeCombo);

        // Font style
        add(new JLabel("风格："));
        String[] styles = {"常规", "粗体", "斜体"};
        fontStyleCombo = new JComboBox<>(styles);
        fontStyleCombo.setSelectedIndex(initialFont.getStyle());
        add(fontStyleCombo);

        // Text color
        add(new JLabel("颜色："));
        colorButton = new JButton();
        colorButton.setBackground(initialColor);
        colorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "选择颜色", selectedColor);
            if (color != null) {
                selectedColor = color;
                colorButton.setBackground(color);
            }
        });
        add(colorButton);

        selectedColor = initialColor;
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
