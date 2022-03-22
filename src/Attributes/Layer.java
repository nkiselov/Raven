package Attributes;

public class Layer<T> {
    public T[][] matrix;
    public T[] options;

    public Layer(T[][] matrix, T[] options) {
        this.matrix = matrix;
        this.options = options;
    }
}
