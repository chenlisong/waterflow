package com.waterflow.ccopen.control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.waterflow.ccopen.service.HelloService;
import com.waterflow.ccopen.util.DateFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/control")
public class HelloControl {

    Logger logger = LoggerFactory.getLogger(HelloControl.class);

    @Resource
    HelloService helloService;

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        String dist = helloService.hello(name);
        logger.info("hello control, name is " + name);
        return dist;
    }

    /**
     * @see http://blog.csdn.net/flueky/article/details/77099454
     * @param rate          贷款利率
     * @param principal     贷款总额
     * @param months        贷款期限
     * @return
     */
    @GetMapping("/calculateEqualPrincipalAndInterest")
    public String calculateEqualPrincipalAndInterest(@RequestParam(value = "rate", defaultValue = "0") double rate
            ,@RequestParam(value = "principal", defaultValue = "0") Double principal
            ,@RequestParam(value = "months", defaultValue = "0") int months ) {
        JSONObject result = new JSONObject();

        double monthRate = rate / (100 * 12);//月利率
        double preLoan = (principal * monthRate * Math.pow((1 + monthRate), months)) / (Math.pow((1 + monthRate), months) - 1);//每月还款金额
        double totalMoney = preLoan * months;//还款总额
        double interest = totalMoney - principal;//还款总利息
        result.put("totalMoney", DateFormatUtil.format(totalMoney));//还款总额
        result.put("principal", DateFormatUtil.format(principal));//贷款总额
        result.put("interest", DateFormatUtil.format(interest));//还款总利息
        result.put("preLoan", DateFormatUtil.format(preLoan));//每月还款金额
        result.put("month", months);//每月还款金额

        return JSON.toJSONString(result);
    }

    @GetMapping("/calculateEqualPrincipal")
    public String calculateEqualPrincipal(@RequestParam(value = "rate", defaultValue = "0") double rate
            ,@RequestParam(value = "principal", defaultValue = "0") Double principal
            ,@RequestParam(value = "months", defaultValue = "0") int months ) {
        JSONObject result = new JSONObject();

        double monthRate = rate / (100 * 12);//月利率
        double prePrincipal = principal / months;//每月还款本金
        double firstMonth = prePrincipal + principal * monthRate;//第一个月还款金额
        double decreaseMonth = prePrincipal * monthRate;//每月利息递减
        double interest = (months + 1) * principal * monthRate / 2;//还款总利息
        double totalMoney = principal + interest;//还款总额
        result.put("totalMoney", DateFormatUtil.format(totalMoney));//还款总额
        result.put("principal", DateFormatUtil.format(principal));//贷款总额
        result.put("interest", DateFormatUtil.format(interest));//还款总利息
        result.put("firstMonth", DateFormatUtil.format(firstMonth));//首月还款金额
        result.put("preLoan", DateFormatUtil.format(decreaseMonth));//每月递减利息
        result.put("month", months);//还款期限

        return JSON.toJSONString(result);
    }

}
