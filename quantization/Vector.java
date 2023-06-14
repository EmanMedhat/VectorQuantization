package vector.quantization;

public class Vector {
    
    int width ;
    int height ;
    double [][] data ;

    public Vector () {}
    public Vector(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new double [height][width];
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }
}
