package coldsrc.cerve.logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Wraps a logger instance from any provider.
 */
public interface LoggerProxy {

    static LoggerProxy debugEnabled(LoggerProxy in) {
        try {
            Method debug0 = LoggerProxy.class.getMethod("debug0", Object[].class);
            Method debug = LoggerProxy.class.getMethod("debug", Object[].class);
            return (LoggerProxy) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{ LoggerProxy.class },
                    ((proxy, method, args) -> {
                        if (method != debug) {
                            return InvocationHandler.invokeDefault(proxy, method, args);
                        }

                        return debug0.invoke(proxy, args);
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static LoggerProxy voiding() {
        return new LoggerProxy() {
            @Override public void debug0(String msg) { }
            @Override public void info(String msg) { }
            @Override public void warn(String msg) { }
            @Override public void error(String msg) { }
        };
    }

    /* Implementation Methods */
    void debug0(String msg);
    void info(String msg);
    void warn(String msg);
    void error(String msg);

    default void debug0(Object... msg) {
        debug0(buildMessage(msg));
    }

    default void info(Object... msg) {
        info(buildMessage(msg));
    }

    default void warn(Object... msg) {
        warn(buildMessage(msg));
    }

    default void error(Object... msg) {
        error(buildMessage(msg));
    }

    /**
     * Logs the given debug message if debugging is
     * enabled on this logger.
     *
     * @param msg The message.
     */
    default void debug(Object... msg) { }

    default String buildMessage(Object... msg) {
        StringBuilder b = new StringBuilder();
        for (Object o : msg)
            b.append(o);
        return b.toString();
    }

}
