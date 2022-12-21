package com.waterflow.rich.scheduled;

import com.waterflow.common.util.HttpUtil;
import com.waterflow.rich.bean.NoticeBean;
import com.waterflow.rich.service.FundService;
import com.waterflow.rich.util.MsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FundTask {

    Logger logger = LoggerFactory.getLogger(FundTask.class);

    @Autowired
    FundService fundService;

    @Value(value="${server.port}")
    int serverPort;

//    @Scheduled(initialDelay = 1000, fixedRate = 3000 * 10000)
    public void asyncCall() {
        try{
            HttpUtil.get("http://localhost:"+serverPort+"/fund/antv-table?refresh=true", null);
        }catch (Exception e) {
        }
    }

//    @Scheduled(initialDelay = 1000, fixedRate = 3000 * 10000)
    @Scheduled(cron = "0 40 6,15 * * ?")
    public void scheduledTask() {

        List<NoticeBean> noticeBeans = fundService.fundTable(FundService.FUNDCODES);

        StringBuilder content = new StringBuilder();
        content.append("<iframe src=\"http://rich.ccopen.top/fund/antv-table\" width=\"100%\" height=\"100%\"/>");

        NoticeBean firstNotice = noticeBeans.get(0);
        String url = String.format("http://rich.ccopen.top/fund/std?code=%s&month=3", firstNotice.getFundCode());

        String summary = firstNotice.toSummary();
        content.append("<br/><br/>");
        content.append(firstNotice.toSummary() + "<br/>");
        content.append("<iframe src=\" "+url+" \" width=\"100%\" height=\"100%\"/>");

        MsgUtil.sendWxNotice(summary, content.toString(), url);

        try{
            HttpUtil.get("http://localhost:"+serverPort+"/fund/antv-table?refresh=true", null);
        }catch (Exception e) {
        }
    }
}
