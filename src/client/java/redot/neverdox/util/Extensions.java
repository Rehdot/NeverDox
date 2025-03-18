package redot.neverdox.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class Extensions {

    public static URL newURL(String in) {
        try {
            return new URL(in);
        } catch (MalformedURLException ignored) {
            return null;
        }
    }

    public static HttpURLConnection getConnection(URL in) {
        try {
            return (HttpURLConnection) in.openConnection();
        } catch (IOException ignored) {
            return null;
        }
    }

    public static <T> T ifNull(T in, Runnable code) {
        if (in != null) return in;
        code.run();
        return null;
    }

    public static <T> void consume(T in, Consumer<T> action) {
        action.accept(in);
    }

    public static <T, R> R apply(T in, Function<T, R> action) {
        return action.apply(in);
    }

    @SafeVarargs
    public static <T, C extends Collection<T>> C with(C in, T... toAdd) {
        Collection<T> additionList = Arrays.asList(toAdd);
        in.addAll(additionList);
        return in;
    }

}
