package redot.neverdox.action;

import java.io.IOException;

@FunctionalInterface
public interface IOAction {
    void execute() throws IOException;
}
