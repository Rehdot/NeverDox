package redot.neverdox.action;

import java.io.FileWriter;

@FunctionalInterface
public interface WriteAction {
    void execute(FileWriter writer);
}
