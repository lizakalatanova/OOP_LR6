import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import java.io.IOException;

public class FractalExplorer {

    private int width;
    private int height;
    private JFrame frame;
    private JPanel northP;
    private JComboBox chooseF;
    private JLabel textF;
    private JImageDisplay display;
    private Rectangle2D.Double range;
    private JPanel southP;
    private JButton resetB;
    private JButton saveB;
    private ArrayList<FractalGenerator> fractals;
    private File nowPath = null;
    private int rowsRemaining = 0;
    private class resetButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.out.println("Reset button clicked!");
            int index = chooseF.getSelectedIndex();
            if (index >= fractals.size()) {
                FractalExplorer.this.setStartImage();
                return;
            }
            fractals.get(index).getInitialRange(range);
            FractalExplorer.this.drawFractal(index);
        }
    }
    private class saveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.out.println("Save button clicked!");
            JFileChooser fchooser;
            if (nowPath == null) {
                fchooser = new JFileChooser();
            } else {
                fchooser = new JFileChooser(nowPath);
            }
            fchooser.setDialogTitle("Choose path");
            fchooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG Images", "*.png"));
            fchooser.setAcceptAllFileFilterUsed(false);
            int result = fchooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                System.out.println("Directory get");
            } else {
                System.out.println("Directory get ERROR");
                return;
            }
            String ext = "";
            String extension = fchooser.getFileFilter().getDescription();
            System.out.println("Desctiption = " + extension);
            if (extension.equals("PNG Images")) ext = "png";
            nowPath = new File(fchooser.getSelectedFile().getPath() + "." + ext);
            System.out.println("Full name = " + nowPath);
            try
            {
                ImageIO.write(display.getImage(), ext, nowPath);
                System.out.println("Write image success!");
                JOptionPane.showMessageDialog(FractalExplorer.this.frame, "Save is success!", "File save", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(FractalExplorer.this.frame, "Save is failed!", "File save", JOptionPane.WARNING_MESSAGE);
            }

        }
    }

    private class mouseClickListener implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            System.out.println("Mouse button clicked!");
            if (rowsRemaining != 0) return;
            int index = chooseF.getSelectedIndex();
            if (index >= fractals.size()) return;
            int x = e.getX();
            int y = e.getY();
            double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width, display.getWidth(), x);
            double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height, display.getHeight(), y);
            if (e.getButton() == MouseEvent.BUTTON1) {
                // Масштабирование
                fractals.get(index).recenterAndZoomRange(range, xCoord, yCoord, 0.5);
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
                fractals.get(index).recenterAndZoomRange(range, xCoord, yCoord, 1.5);
            }
            FractalExplorer.this.drawFractal(index);
        }
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
    }
    private class comboBoxClickListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.out.println("Click in ComboBox on " + chooseF.getSelectedItem());
            int index = chooseF.getSelectedIndex();
            if (index >= fractals.size()) {
                FractalExplorer.this.setStartImage();
                return;
            }
            fractals.get(index).getInitialRange(range);
            FractalExplorer.this.drawFractal(index);
        }
    }

    public FractalExplorer(int size) {
        this(size, size);
    }

    public FractalExplorer(int width, int height) {
        this.width = width;
        this.height = height;
        this.range = new Rectangle2D.Double();
        fractals = new ArrayList<FractalGenerator>();
        fractals.add(new Mandelbrot());
        fractals.add(new Tricorn());
        fractals.add(new BurningShip());
    }

    public void createAndShowGUI() {

        this.frame = new JFrame("Fractal explorer");
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setSize(this.width, this.height);
        this.frame.setResizable(false);

        northP = new JPanel();
        southP = new JPanel();

        this.saveB = new JButton("Save image");
        this.saveB.setPreferredSize(new Dimension(frame.getWidth() / 3, 30));
        southP.add(this.saveB);

        this.resetB = new JButton("Reset display");
        this.resetB.setPreferredSize(new Dimension(frame.getWidth() / 3, 30));
        southP.add(this.resetB);

        this.textF = new JLabel("Fractals: ");
        Font font = saveB.getFont();
        textF.setFont(font);
        northP.add(this.textF);

        this.chooseF = new JComboBox();
        for (int i = 0; i < fractals.size(); i++) {
            chooseF.addItem(fractals.get(i).toString());
        }

        chooseF.addItem("-Empty-");

        chooseF.setSelectedIndex(fractals.size());

        this.chooseF.setPreferredSize(new Dimension(frame.getWidth() / 4, 30));
        northP.add(this.chooseF);

        frame.getContentPane().add(BorderLayout.NORTH, this.northP);
        frame.getContentPane().add(BorderLayout.SOUTH, this.southP);


        int height = frame.getHeight() - 60;
        int width = height;
        frame.setSize(width, frame.getHeight());

        this.display = new JImageDisplay(width, height);
        frame.getContentPane().add(BorderLayout.CENTER, this.display);

        display.addMouseListener(new mouseClickListener());

        resetB.addActionListener(new resetButtonListener());
        saveB.addActionListener(new saveButtonListener());
        chooseF.addActionListener(new comboBoxClickListener());

        frame.setVisible(true);
    }
    public void drawFractal(int index) {
        this.clearImage();

        this.enableUI(false);

        int pictureHeight = display.getHeight();
        int pictureWidth = display.getWidth();

        rowsRemaining = pictureHeight;

        for (int i = 0; i < pictureHeight; i++) {

            FractalWorker tempThread = new FractalWorker(i, pictureWidth, index);
            tempThread.execute();
        }

    }

    public void setStartImage() {
        this.display.setStartImage();
    }

    public void clearImage() {
        this.display.clearImage();
    }

    private class FractalWorker extends SwingWorker<Object, Object> { // потоки


        private int numOfStr; // координата вычисляемой строки
        private int picWidth;


        private int[] strValues; // значения для каждого пикселя


        private int index;

        public FractalWorker(int y, int picWidth, int index) {
            this.numOfStr = y;
            this.picWidth = picWidth;
            this.index = index;
        }


        @Override
        protected String doInBackground() throws Exception { // фоновые процессы
            strValues = new int[picWidth];
            for (int x = 0; x < picWidth; x++) {
                // Преобразование координат плоскости пикселей в координаты мнимой плоскости
                double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width, display.getWidth(), x);
                double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height, display.getHeight(), numOfStr);
                strValues[x] = fractals.get(index).numIterations(xCoord, yCoord);
            }
            return null;
        }

        @Override
        protected void done() { // фоновая задача завершена
            try {
                int x = 0;
                for (int numOfIter: strValues) {
                    int rgbColor;
                    if (numOfIter != -1) {
                        float hue = 0.7f + (float) numOfIter / 200f;
                        rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    }
                    else {
                        rgbColor = Color.HSBtoRGB(0, 0, 0);
                    }
                    display.drawPixelWithNoRepaint(x, numOfStr, new Color(rgbColor));
                    x++;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            rowsRemaining--;
            if (rowsRemaining == 0) {
                enableUI(true);
                display.repaintPicture();
            }
        }
    }

    private void enableUI(boolean enable) { // включать или отключать кнопки с выпадающим списком в пользовательском интерфейсе на основе указанного параметра
        chooseF.setEnabled(enable);
        saveB.setEnabled(enable);
        resetB.setEnabled(enable);
    }

    public static void main(String[] args) {
        FractalExplorer explorer = new FractalExplorer(600);
        explorer.createAndShowGUI();
    }
}