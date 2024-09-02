package redot.neverdox.action;

@FunctionalInterface
public interface Action<T> {
    void execute(T object);
}
