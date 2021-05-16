import java.awt.geom.Rectangle2D;

public class BurningShip extends FractalGenerator {
    public static final int MAX_ITERATIONS = 1000;
    public BurningShip() {
    }
    @Override
    public void getInitialRange(Rectangle2D.Double range) { // комлексная плоскость, начальный диапазон
        range.x = -2;
        range.y = -2.5;
        range.width = 4;
        range.height = 4;
    }
    @Override
    public int numIterations(double x, double y) { // определение границ итераций
        int iteration = 0;
        double realPart = 0;
        double imaginaryPart = 0;
        while ((iteration < MAX_ITERATIONS) && ((realPart * realPart + imaginaryPart * imaginaryPart) < 4)) {
            double rp = realPart * realPart - imaginaryPart * imaginaryPart + x;
            double ip = Math.abs(2 * realPart * imaginaryPart + y);
            realPart = Math.abs(rp);
            imaginaryPart = ip;
            iteration += 1;
        }
        if (iteration == MAX_ITERATIONS)
            return -1;
        else
            return iteration;
    }
    @Override
    public String toString() { // возвращение названия
        return "BurningShip";
    }
}