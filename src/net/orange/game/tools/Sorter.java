package net.orange.game.tools;

@FunctionalInterface
public interface Sorter<T> {
    private static int sign(long a){
        return a < 0 ? -1 : a > 0 ? 1 : 0;
    }
    long apply(T value);
    default Sorter<T> add(Sorter<T> other){
        return (o)->apply(o)+other.apply(o);
    }
    default int cmp(T a, T b){
        return sign(apply(b) - apply(a));
    }
}
