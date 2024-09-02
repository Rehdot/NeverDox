package redot.neverdox.action;

@FunctionalInterface
public interface CastAction<T, R> {
    R execute(T object);
}
