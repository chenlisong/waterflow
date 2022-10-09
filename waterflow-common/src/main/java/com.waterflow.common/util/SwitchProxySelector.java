package com.waterflow.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SwitchProxySelector extends ProxySelector {

    private final static Logger logger = LoggerFactory.getLogger(SwitchProxySelector.class);

    public static ThreadLocal<Proxy> proxyThreadLocal = new ThreadLocal<>();

    public static String[] ipPool = new String[]{};

    public static Random random = new Random();

    public static JsoupUtil jsoupUtil = new JsoupUtil();

    private static final String SPLIT = ":";

    public SwitchProxySelector() {
        super();
    }

    static {
        // init proxy pool
//        Map<String, Integer> proxy = jsoupUtil.proxyGet();
//        int index = 0;
//        ipPool = new String[proxy.size()];
//        for(String ip : proxy.keySet()) {
//            ipPool[index] = ip + SPLIT + proxy.get(ip);
//            index++;
//        }

        ipPool = new String[]{"221.5.80.66:3128"};
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
        if(ipPool.length <= 0) {
            return Proxy.NO_PROXY;
        }

        int randomIntValue = random.nextInt(ipPool.length);
        String ip = ipPool[randomIntValue % ipPool.length];
        logger.info("get proxy ip is {}", ip);
        String[] ipSplit = ip.split(SPLIT);
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ipSplit[0], Integer.parseInt(ipSplit[1])));
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        logger.error("connect fail address is {}", sa);
    }
}
