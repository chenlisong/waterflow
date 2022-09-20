package com.waterflow.test.util;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SwitchProxyInterceptor implements Interceptor {

    private final static Logger logger = LoggerFactory.getLogger(SwitchProxyInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        if("true".equals(chain.request().header("meta.proxy"))
                && SwitchProxySelector.ipPool.length > 0){
            String proxyHeader = chain.request().header("meta.proxy");
            logger.info("inertcept meta header is {}", proxyHeader);
            SwitchProxySelector.proxyThreadLocal.set(SwitchProxySelector.getProxy());
            Request newRequest = chain.request().newBuilder().removeHeader("meta.proxy").build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(chain.request());
    }
}
