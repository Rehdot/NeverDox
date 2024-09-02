package redot.neverdox.action;

@FunctionalInterface
public interface ExceptionAction<T extends Exception> {
    void execute() throws T;
}
