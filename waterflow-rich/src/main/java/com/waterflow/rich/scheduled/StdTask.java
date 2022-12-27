package com.waterflow.rich.scheduled;

import com.waterflow.common.util.HttpUtil;
import com.waterflow.rich.bean.CheckView;
import com.waterflow.rich.bean.NoticeBean;
import com.waterflow.rich.bean.QuoteView;
import com.waterflow.rich.service.FundService;
import com.waterflow.rich.service.QuoteService;
import com.waterflow.rich.util.MsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StdTask {

    Logger logger = LoggerFactory.getLogger(StdTask.class);

    @Autowired
    QuoteService quoteService;

    @Value(value="${server.port}")
    int serverPort;

    @Value(value="${spring.profiles.active}")
    String profile;

    @Value(value="${quote.codes}")
    String quoteCodes;

    String url = "http://rich.ccopen.top/quote/checkview";

    @Scheduled(initialDelay = 1000, fixedRate = 1000 * 60 * 40)
    public void asyncCall() {
        if(!"online".equals(profile)) {
            return;
        }

        try{
            HttpUtil.get(url, null);
        }catch (Exception e) {
        }
    }

//    @Scheduled(initialDelay = 1000, fixedRate = 3000 * 10000)
    @Scheduled(cron = "0 0 6,19 * * ?")
    public void scheduledTask() {

        if(!"online".equals(profile)) {
            return;
        }

        try{
            String[] codes = quoteCodes.split(",");

            List<CheckView> views = quoteService.checkView(codes);

            List<String> sell = views.stream().filter(view -> view.getLevel() == -1)
                    .limit(5)
                    .map(view -> {return view.getName() + "/" + view.getCode();})
                    .collect(Collectors.toList());

            List<String> buy = views.stream().filter(view -> view.getLevel() == 1)
                    .limit(5)
                    .map(view -> {return view.getName() + "/" + view.getCode();})
                    .collect(Collectors.toList());

            StringBuilder content = new StringBuilder();
            content.append("<iframe src=\"http://rich.ccopen.top/quote/checkview\" width=\"100%\" height=\"100%\"/>");

            StringBuilder summary = new StringBuilder();

            summary.append("强烈卖出：")
                    .append(String.join(",", sell));

            summary.append("强烈买入：")
                    .append(String.join(",", buy));

            MsgUtil.sendWxNotice(summary.toString(), content.toString(), url);
            HttpUtil.get(url, null);
        }catch (Exception e) {
        }
    }
}
