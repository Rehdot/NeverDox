package redot.neverdox.action;

import java.io.FileReader;

@FunctionalInterface
public interface ReadAction {
    void execute(FileReader reader);
}
