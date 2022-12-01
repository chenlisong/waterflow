package com.waterflow.ccopen.control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson2.JSONObject;
import com.waterflow.ccopen.bean.LoanBean;
import com.waterflow.ccopen.util.DateFormatUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;


@RestController
@RequestMapping
public class LoanControl {

    /**
     * 组合贷款Java实现：https://blog.csdn.net/flueky/article/details/77099454
     * 提前还款在线工具：https://finance.sina.com.cn/calc/house_early_repayment.html
     * 提前还款Java实现：https://blog.csdn.net/flueky/article/details/77099454
     * @param repayType          还款方式（1:等额本息、2：等额本金）
     * @param commerLoan          商业贷款总额
     * @param fundLoan           公积金贷款金额
     * @param commerRate         商贷利率
     * @param fundRate           公积金贷款利率
     * @param months             贷款期限
     * @return
     */
    @GetMapping("/loan")
    public String calculateEqualPrincipalAndInterest(@RequestParam(value = "repayType", defaultValue = "-1") int repayType
            ,@RequestParam(value = "commerLoan", defaultValue = "0") Double commerLoan
            ,@RequestParam(value = "fundLoan", defaultValue = "0") Double fundLoan
            ,@RequestParam(value = "commerRate", defaultValue = "0") Double commerRate
            ,@RequestParam(value = "fundRate", defaultValue = "0") Double fundRate
            ,@RequestParam(value = "months", defaultValue = "0") int months ) {
        LoanBean result = new LoanBean();

        if(months <= 0
                || (commerLoan <= 0 && fundLoan <= 0)
                || (repayType != 1 && repayType != 2)) {
            return JSON.toJSONString(result);
        }

        if(repayType == 1) {
            double monthRate = commerRate / (100 * 12);//月利率
            double preLoan = (commerLoan * monthRate * Math.pow((1 + monthRate), months)) / (Math.pow((1 + monthRate), months) - 1);//每月还款金额
            double totalMoney = preLoan * months;//还款总额
            double interest = totalMoney - commerLoan;//还款总利息

            double fundmonthRate = fundRate / (100 * 12);//月利率
            double fundpreLoan = (fundLoan * fundmonthRate * Math.pow((1 + fundmonthRate), months)) / (Math.pow((1 + fundmonthRate), months) - 1);//每月还款金额
            double fundtotalMoney = fundpreLoan * months;//还款总额
            double fundinterest = totalMoney - fundLoan;//还款总利息

            result.setTotalMoney(totalMoney + fundtotalMoney);
            result.setPrincipal(commerLoan + fundLoan);
            result.setInterest(interest + fundinterest);
            result.setPreLoan(preLoan + fundpreLoan);
            result.setMonth(months);

            result.setFirstMonth(preLoan + fundpreLoan);
            result.setDecreaseMonth(0.0);
        }else if(repayType == 2) {

            double monthRate = commerRate / (100 * 12);//月利率
            double prePrincipal = commerLoan / months;//每月还款本金
            double firstMonth = prePrincipal + commerLoan * monthRate;//第一个月还款金额
            double decreaseMonth = prePrincipal * monthRate;//每月利息递减
            double interest = (months + 1) * commerLoan * monthRate / 2;//还款总利息
            double totalMoney = commerLoan + interest;//还款总额

            double fundmonthRate = fundRate / (100 * 12);//月利率
            double fundprePrincipal = fundLoan / months;//每月还款本金
            double fundfirstMonth = fundprePrincipal + fundLoan * fundmonthRate;//第一个月还款金额
            double funddecreaseMonth = fundprePrincipal * fundmonthRate;//每月利息递减
            double fundinterest = (months + 1) * fundLoan * fundmonthRate / 2;//还款总利息
            double fundtotalMoney = fundLoan + fundinterest;//还款总额

            result.setTotalMoney(totalMoney + fundtotalMoney);
            result.setPrincipal(commerLoan + fundLoan);
            result.setInterest(interest + fundinterest);
            result.setPreLoan(firstMonth + fundfirstMonth);
            result.setMonth(months);

            result.setFirstMonth(firstMonth + fundfirstMonth);
            result.setDecreaseMonth(decreaseMonth + funddecreaseMonth);
        }

        ValueFilter doubleFilter = new ValueFilter() {
            @Override
            public Object process(Object o, String s, Object value){
                try{
                    if(value instanceof BigDecimal || value instanceof Double) {
                        return NumberUtils.toScaledBigDecimal((double)value, Integer.valueOf(2), RoundingMode.HALF_UP).doubleValue();
                    }
                }catch (Exception e) {
                }
                return value;
            }
        };

        return JSON.toJSONString(result, doubleFilter);
    }

}
