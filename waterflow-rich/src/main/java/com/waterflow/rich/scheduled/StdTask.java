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
            String[] codes = new String[] {
                    "004432", "110020", "501302", "004488", "004532", "003765", "004069", "002656", "001631", "001455",
                    "0600428", "0601020", "0600150", "0601989", "1002594",
                    "159915", "159920", "159919", "159922", "159949", "159941", "159865", "159857", "159883", "159870", "159828", "159952", "159938", "159813", "159905", "159916", "159869", "159929", "159837", "159867", "159801", "159843", "159861", "159939", "159824", "159886", "159850", "159945", "159930", "159954", "159839", "159871"};

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
