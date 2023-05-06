package coldsrc.cerve.logging;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public class LoggerProvider {

    /**
     * Created logger proxies by name.
     */
    private final Map<String, LoggerProxy> loggerMap = new WeakHashMap<>();

    /**
     * The factory function for creating logger proxies.
     */
    private final Function<String, LoggerProxy> proxyFactory;

    /**
     * If debug logs should be enabled.
     */
    private final boolean enableDebug;

    public LoggerProvider(Function<String, LoggerProxy> proxyFactory, boolean enableDebug) {
        if (proxyFactory == null)
            proxyFactory = __ -> LoggerProxy.voiding();

        this.proxyFactory = proxyFactory;
        this.enableDebug = enableDebug;
    }

    public LoggerProxy getLogger(final String name) {
        return loggerMap.computeIfAbsent(name, __ -> {
            LoggerProxy proxy = proxyFactory.apply(name);

            if (enableDebug) {
                proxy = LoggerProxy.debugEnabled(proxy);
            }

            return proxy;
        });
    }

    public boolean isDebugEnabled() {
        return enableDebug;
    }

}
