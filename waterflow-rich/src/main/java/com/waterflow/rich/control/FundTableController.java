package com.waterflow.rich.control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.waterflow.rich.bean.NoticeBean;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.service.FundService;
import com.waterflow.rich.strategy.RetreatStrategy;
import com.waterflow.rich.strategy.RichBean;
import com.waterflow.rich.strategy.StdRichBean;
import com.waterflow.rich.strategy.StdStrategy;
import com.waterflow.rich.util.LocalCacheUtil;
import com.waterflow.rich.util.MsgUtil;
import com.waterflow.rich.util.NumberFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/fund")
public class FundTableController {

    Logger logger = LoggerFactory.getLogger(FundTableController.class);

    @Autowired
    private FundGrab fundGrab;

    @Autowired
    StdStrategy stdStrategy;

    @Autowired
    RetreatStrategy retreat;

    @Autowired
    FundService fundService;

    @RequestMapping("/antv-table")
    public String fundTable(Model model, @RequestParam(value = "refresh", defaultValue = "false") boolean refresh) {
        String key = "rich::fund::antv-table";
        String cacheValue = LocalCacheUtil.instance().getWithTradeTime(key);
        if(!refresh && StringUtils.isNotEmpty(cacheValue)) {
            model.addAttribute("data", cacheValue);
            return "antv-table";
        }

        List<NoticeBean> newNoticeBeans = fundService.fundTable(FundService.FUNDCODES);

        List<JSONObject> result = new ArrayList<>();
        for(NoticeBean noticeBean: newNoticeBeans) {
            JSONObject diff = new JSONObject();
            diff.put("x", noticeBean.getFundName()+"/"+noticeBean.getFundCode());
            diff.put("type", "标准差相减");
            diff.put("y", noticeBean.getMonthStd() - noticeBean.getYearStd());

            JSONObject month = new JSONObject();
            month.put("x", noticeBean.getFundName()+"/"+noticeBean.getFundCode());
            month.put("type", "3月标准差");
            month.put("y", noticeBean.getMonthStd());
            result.add(diff);
            result.add(month);
        }

        model.addAttribute("data", JSON.toJSONString(result, NumberFilter.defaultDouble()));
        LocalCacheUtil.instance().set(key, JSON.toJSONString(result, NumberFilter.defaultDouble()));

        return "antv-table";
    }
}
