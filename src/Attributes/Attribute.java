package Attributes;

public interface Attribute<T,V> {
    public String getTag();
    public V[] getSchemes();
    public Layer<T> generateMatrix(V scheme, int options);
    public Layer<T> generateDistraction(int options);
    public boolean testMatrix(T[][] matrix, V scheme);
}
