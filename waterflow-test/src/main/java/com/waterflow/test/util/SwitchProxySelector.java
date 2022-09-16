package com.waterflow.test.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Random;

public class SwitchProxySelector extends ProxySelector {

    private final static Logger logger = LoggerFactory.getLogger(SwitchProxySelector.class);

    public static ThreadLocal<Proxy> proxyThreadLocal = new ThreadLocal<>();

    public static String[] ipPool = new String[] {"183.236.123.242:8060", "60.170.204.30:8060"};

    public static Random random = new Random();

    public SwitchProxySelector() {
        super();
    }

    @Override
    public List<Proxy> select(URI uri) {

        Proxy proxy = SwitchProxySelector.proxyThreadLocal.get();
        if(proxy == null) {
            proxy = Proxy.NO_PROXY;
        }

        logger.info("proxy select type is {}, address is {}", proxy.type().name(), proxy.address());

        SwitchProxySelector.proxyThreadLocal.remove();

        return null;
    }

    public static Proxy getProxy() {
        int randomIntValue = random.nextInt();
        String ip = ipPool[randomIntValue % ipPool.length];
        String[] ipSplit = ip.split(":");
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ipSplit[0], Integer.parseInt(ipSplit[1])));
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

    }
}
