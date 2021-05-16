import java.awt.geom.Rectangle2D;

public class Mandelbrot extends FractalGenerator{
    public static final int MAX_ITERATIONS = 1000;
    public Mandelbrot() {
    }
    @Override
    public void getInitialRange(Rectangle2D.Double range) { // косплексная плоскость, начальный диапазон
        range.x = -2;
        range.y = -1.5;
        range.width = 3;
        range.height = 3;
    }
    @Override
    public int numIterations(double x, double y) { // опредление границ итераций
        int iteration = 0;
        double realPart = 0;
        double imaginaryPart = 0;
        while ((iteration < MAX_ITERATIONS) && ((realPart * realPart + imaginaryPart * imaginaryPart) < 4)) {
            double rp = realPart * realPart - imaginaryPart * imaginaryPart + x;
            double ip = 2 * realPart * imaginaryPart + y;
            realPart = rp;
            imaginaryPart = ip;
            iteration += 1;
        }
        if (iteration == MAX_ITERATIONS)
            return -1;
        else
            return iteration;
    }
    @Override
    public String toString() {
        return "Mandelbrot";
    }
}