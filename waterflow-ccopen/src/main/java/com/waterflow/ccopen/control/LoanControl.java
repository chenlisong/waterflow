package com.waterflow.ccopen.control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.waterflow.ccopen.bean.LoanBean;
import com.waterflow.ccopen.bean.RepayBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@RequestMapping
public class LoanControl {
    Logger logger = LoggerFactory.getLogger(LoanControl.class);

    /**
     * 组合贷款Java实现：https://blog.csdn.net/flueky/article/details/77099454
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

    /**
     * 提前还款
     * @param type          还款类型（1. 一次性提前还款，2. 部分提前还款）
     * @param repayType     还款方式（1:等额本息、2：等额本金）
     * @param loan          贷款总额
     * @param prePayType    提前还款类型（1:月供不变、2:余额不变）【仅当还款类型=部分提前还款 时有效】
     * @param prePayLoan    提前还款金额（当type=2时，必填）
     * @param firstPayTime  第一次还款时间，格式yyyyMM 202008
     * @param rate          利率
     * @param months        贷款期限
     * 提前还款在线工具：https://finance.sina.com.cn/calc/house_early_repayment.html
     * 提前还款Java实现：https://blog.csdn.net/flueky/article/details/77099454
     *
     * 例子场景1- 一次性提前还款，等额本息：http://localhost:2100/prepay?type=1&repayType=1&loan=1000000&rate=6.37&months=360&firstPayTime=202009&prePayTime=202212
     * 例子场景2- 一次性提前还款，等额本金：http://localhost:2100/prepay?type=1&repayType=2&loan=1000000&rate=6.37&months=360&firstPayTime=202009&prePayTime=202212
     * 例子场景3- 部分提前还款，等额本息，月供不变：http://localhost:2100/prepay?type=2&repayType=1&loan=1000000&rate=6.37&months=360&firstPayTime=202009&prePayType=1&prePayLoan=500000&prePayTime=202212
     * 例子场景4- 部分提前还款，等额本金，月供不变：http://localhost:2100/prepay?type=2&repayType=2&prePayType=1&loan=1000000&rate=6.37&months=360&firstPayTime=202009&prePayLoan=500000&prePayTime=202212
     * 例子场景5- 部分提前还款，等额本息，期限不变：http://localhost:2100/prepay?type=2&repayType=1&prePayType=2&loan=1000000&rate=6.37&months=360&firstPayTime=202009&prePayLoan=500000&prePayTime=202212
     * 例子场景6- 部分提前还款，等额本金，期限不变：http://localhost:2100/prepay?type=2&repayType=2&prePayType=2&loan=1000000&rate=6.37&months=360&firstPayTime=202009&prePayLoan=500000&prePayTime=202212
     * @return
     */
    @GetMapping("/prepay")
    public String calculateEqualPrincipalAndInterest(@RequestParam(value = "type", defaultValue = "-1") int type
            ,@RequestParam(value = "repayType", defaultValue = "-1") int repayType
            ,@RequestParam(value = "loan", defaultValue = "0") Double loan
            ,@RequestParam(value = "prePayType", defaultValue = "-1") int prePayType
            ,@RequestParam(value = "prePayLoan", defaultValue = "0") Double prePayLoan
            ,@RequestParam(value = "prePayTime") String prePayTime
            ,@RequestParam(value = "firstPayTime") String firstPayTime
            ,@RequestParam(value = "rate", defaultValue = "0") Double rate
            ,@RequestParam(value = "months", defaultValue = "0") int months ) {
        RepayBean result = new RepayBean();

        if((type != 1 && type != 2) || (repayType !=1 && repayType != 2)
            || StringUtils.isEmpty(firstPayTime)) {
            return JSON.toJSONString(result);
        }

        int payTimes = 0;
        SimpleDateFormat standFormat = new SimpleDateFormat("yyyyMM");
        Date now = null;
        try {
            now = standFormat.parse(prePayTime);
            Date firstPayDate = standFormat.parse(firstPayTime);
            String diffMonth = DurationFormatUtils.formatPeriod(firstPayDate.getTime(), now.getTime(),"M");
            payTimes = Integer.valueOf(diffMonth);
        } catch (Exception e) {
            logger.error("error.", e);
            return JSON.toJSONString(result);
        }

        double principal = loan;

        // 一次性提前还款 等额本息
        if(type == 1 && repayType == 1) {
            double monthRate = rate / (100 * 12);//月利率
            double preLoan = (principal * monthRate * Math.pow((1 + monthRate), months)) / (Math.pow((1 + monthRate), months) - 1);//每月还款金额
            double totalMoney = preLoan * months;//还款总额
            double interest = totalMoney - principal;//还款总利息
            double leftLoan = principal * Math.pow(1 + monthRate, payTimes) - preLoan * (Math.pow(1 + monthRate, payTimes) - 1) / monthRate;//n个月后欠银行的钱
            double payLoan = principal - leftLoan;//已还本金
            double payTotal = preLoan * payTimes;//已还总金额
            double payInterest = payTotal - payLoan;//已还利息
            double totalPayAhead = leftLoan * (1 + monthRate);//剩余一次还清
            double saveInterest = totalMoney - payTotal - totalPayAhead;

            result.setTotalMoney(totalMoney);
            result.setPrincipal(principal);
            result.setInterest(interest);
            result.setPreLoan(preLoan);
            result.setPayTotal(payTotal);
            result.setPayLoan(payLoan);
            result.setPayInterest(payInterest);
            result.setTotalPayAhead(totalPayAhead);
            result.setSaveInterest(saveInterest);
            result.setMonths(0);
            result.setLastPayMonth(standFormat.format(now));

            // 一次性提前还款 等额本金
        }else if(type == 1 && repayType == 2) {
            double monthRate = rate / (100 * 12);//月利率
            double prePrincipal = principal / months;//每月还款本金
            double firstMonth = prePrincipal + principal * monthRate;//第一个月还款金额
            double decreaseMonth = prePrincipal * monthRate;//每月利息递减
            double interest = (months + 1) * principal * monthRate / 2;//还款总利息
            double totalMoney = principal + interest;//还款总额
            double payLoan = prePrincipal * payTimes;//已还本金
            double payInterest = (principal * payTimes - prePrincipal * (payTimes - 1) * payTimes / 2) * monthRate;//已还利息
            double payTotal = payLoan + payInterest;//已还总额
            double totalPayAhead = (principal - payLoan) * (1 + monthRate);//提前还款金额（剩余本金加上剩余本金当月利息）
            double saveInterest = totalMoney - payTotal - totalPayAhead;

            result.setTotalMoney(totalMoney);
            result.setPrincipal(principal);
            result.setInterest(interest);
            result.setFirstMonth(firstMonth);
            result.setDecreaseMonth(decreaseMonth);

            result.setPreLoan(-1.0);
            result.setPayTotal(payTotal);
            result.setPayLoan(payLoan);
            result.setPayInterest(payInterest);
            result.setTotalPayAhead(totalPayAhead);
            result.setSaveInterest(saveInterest);
            result.setMonths(0);
            result.setLastPayMonth(standFormat.format(now));

            //部分提前还款计算（等额本息、月供不变）
        }else if(type == 2 && repayType == 1 && prePayType == 1) {
            double monthRate = rate / (100 * 12);//月利率
            double preLoan = (principal * monthRate * Math.pow((1 + monthRate), months)) / (Math.pow((1 + monthRate), months) - 1);//每月还款金额
            double totalMoney = preLoan * months;//还款总额
            double interest = totalMoney - principal;//还款总利息
            double leftLoanBefore = principal * Math.pow(1 + monthRate, payTimes) - preLoan * (Math.pow(1 + monthRate, payTimes) - 1) / monthRate;//提前还款前欠银行的钱
            double leftLoan = principal * Math.pow(1 + monthRate, payTimes + 1) - preLoan * (Math.pow(1 + monthRate, payTimes + 1) - 1) / monthRate-prePayLoan;//提前还款后欠银行的钱
            double payLoan = principal - leftLoanBefore;//已还本金
            double payTotal = preLoan * payTimes ;//已还总金额
            double payInterest = payTotal - payLoan;//已还利息
            double aheadTotalMoney = prePayLoan + preLoan;//提前还款总额
            //计算剩余还款期限
            int leftMonth = (int) Math.floor(Math.log(preLoan / (preLoan - leftLoan * monthRate)) / Math.log(1 + monthRate));
            double newPreLoan = (leftLoan * monthRate * Math.pow((1 + monthRate), leftMonth)) / (Math.pow((1 + monthRate), leftMonth) - 1);//剩余贷款每月还款金额
            double leftTotalMoney = newPreLoan * leftMonth;//剩余还款总额
            double leftInterest = leftTotalMoney - (leftLoan-prePayLoan);
            double saveInterest = totalMoney - aheadTotalMoney - leftTotalMoney-payTotal;

            result.setTotalMoney(totalMoney);
            result.setPrincipal(principal);
            result.setInterest(interest);
            result.setPreLoan(preLoan);
            result.setPayTotal(payTotal);
            result.setPayLoan(payLoan);
            result.setPayInterest(payInterest);
            result.setAheadTotalMoney(aheadTotalMoney);
            result.setLeftTotalMoney(leftTotalMoney);
            result.setLeftInterest(leftInterest);
            result.setNewPreLoan(newPreLoan);
            result.setSaveInterest(saveInterest);
            result.setMonths(leftMonth);

            String lastPayMonth = standFormat.format(DateUtils.addMonths(now, leftMonth));
            result.setLastPayMonth(lastPayMonth);

            //部分提前还款计算(等额本金、月供不变)
        }else if(type == 2 && repayType == 2 && prePayType == 1) {
            double monthRate = rate / (100 * 12);//月利率
            double prePrincipal = principal / months;//每月还款本金
            double firstMonth = prePrincipal + principal * monthRate;//第一个月还款金额
            double decreaseMonth = prePrincipal * monthRate;//每月利息递减
            double interest = (months + 1) * principal * monthRate / 2;//还款总利息
            double totalMoney = principal + interest;//还款总额
            double payLoan = prePrincipal * payTimes;//已还本金
            double payInterest = (principal * payTimes - prePrincipal * (payTimes - 1) * payTimes / 2) * monthRate;//已还利息
            double payTotal = payLoan + payInterest;//已还总额
            double aheadTotalMoney = (principal - payLoan) *  monthRate+prePayLoan+prePrincipal;//提前还款金额
            double leftLoan = principal - prePayLoan - payLoan-prePrincipal;//剩余金额
            int leftMonth = (int) Math.floor(leftLoan / prePrincipal);
            double newPrePrincipal = leftLoan / leftMonth;//新的每月还款本金
            double newFirstMonth = newPrePrincipal + leftLoan * monthRate;//新的第一个月还款金额
            double newDecreaseMonth = newPrePrincipal * monthRate;//新的每月利息递减
            double leftInterest = (leftMonth + 1) * leftLoan * monthRate / 2;//还款总利息
            double leftTotalMoney = leftLoan + leftInterest;//还款总额
            double saveInterest = totalMoney-payTotal-aheadTotalMoney-leftTotalMoney;

            result.setTotalMoney(totalMoney);
            result.setPrincipal(principal);
            result.setInterest(interest);
            result.setFirstMonth(firstMonth);
            result.setDecreaseMonth(decreaseMonth);
            result.setPayTotal(payTotal);
            result.setPayLoan(payLoan);
            result.setPayInterest(payInterest);
            result.setAheadTotalMoney(aheadTotalMoney);
            result.setLeftTotalMoney(leftTotalMoney);
            result.setLeftInterest(leftInterest);
            result.setNewFirstMonth(newFirstMonth);
            result.setNewDecreaseMonth(newDecreaseMonth);
            result.setSaveInterest(saveInterest);
            result.setMonths(leftMonth);

            String lastPayMonth = standFormat.format(DateUtils.addMonths(now, leftMonth));
            result.setLastPayMonth(lastPayMonth);
            //部分提前还款计算（等额本息、期限不变）
        }else if(type == 2 && repayType == 1 && prePayType == 2) {
            double monthRate = rate / (100 * 12);//月利率
            double preLoan = (principal * monthRate * Math.pow((1 + monthRate), months)) / (Math.pow((1 + monthRate), months) - 1);//每月还款金额
            double totalMoney = preLoan * months;//还款总额
            double interest = totalMoney - principal;//还款总利息
            double leftLoanBefore = principal * Math.pow(1 + monthRate, payTimes ) - preLoan * (Math.pow(1 + monthRate, payTimes ) - 1) / monthRate;//提前还款前欠银行的钱
            double leftLoan = principal * Math.pow(1 + monthRate, payTimes + 1) - preLoan * (Math.pow(1 + monthRate, payTimes + 1) - 1) / monthRate;//提前还款后银行的钱
            double payLoan = principal - leftLoanBefore;//已还本金
            double payTotal = preLoan * payTimes;//已还总金额
            double payInterest = payTotal - payLoan;//已还利息
            double aheadTotalMoney = preLoan + prePayLoan;//下个月还款金额
            double newPreLoan = ((leftLoan - prePayLoan) * monthRate * Math.pow((1 + monthRate), months - payTimes - 1)) / (Math.pow((1 + monthRate), months - payTimes - 1) - 1);//下个月起每月还款金额
            double leftTotalMoney = newPreLoan*(months-payTimes);
            double leftInterest =leftTotalMoney -(leftLoan - prePayLoan);
            double saveInterest = totalMoney-payTotal-aheadTotalMoney-leftTotalMoney;

            result.setTotalMoney(totalMoney);
            result.setPrincipal(principal);
            result.setInterest(interest);
            result.setPreLoan(preLoan);
            result.setPayTotal(payTotal);
            result.setPayLoan(payLoan);
            result.setPayInterest(payInterest);
            result.setAheadTotalMoney(aheadTotalMoney);
            result.setLeftTotalMoney(leftTotalMoney);
            result.setLeftInterest(leftInterest);
            result.setNewPreLoan(newPreLoan);
            result.setSaveInterest(saveInterest);
            result.setMonths(months);

            String lastPayMonth = standFormat.format(DateUtils.addMonths(now, months));
            result.setLastPayMonth(lastPayMonth);
            //部分提前还款计算（等额本金、期限不变）
        }else if(type == 2 && repayType == 2 && prePayType == 2) {
            double monthRate = rate / (100 * 12);//月利率
            double prePrincipal = principal / months;//每月还款本金
            double firstMonth = prePrincipal + principal * monthRate;//第一个月还款金额
            double decreaseMonth = prePrincipal * monthRate;//每月利息递减
            double interest = (months + 1) * principal * monthRate / 2;//还款总利息
            double totalMoney = principal + interest;//还款总额
            double payLoan = prePrincipal * payTimes;//已还本金
            double payInterest = (principal * payTimes - prePrincipal * (payTimes - 1) * payTimes / 2) * monthRate;//已还利息
            double payTotal = payLoan + payInterest;//已还总额
            double aheadTotalMoney = (principal - payLoan) *  monthRate+prePayLoan+prePrincipal;//提前还款金额
            int leftMonth = months - payTimes-1;
            double leftLoan = principal - prePayLoan - payLoan-prePrincipal;
            double newPrePrincipal = leftLoan / leftMonth;//新的每月还款本金
            double newFirstMonth = newPrePrincipal + leftLoan * monthRate;//新的第一个月还款金额
            double newDecreaseMonth = newPrePrincipal * monthRate;//新的每月利息递减
            double leftInterest = (leftMonth + 1) * leftLoan * monthRate / 2;//还款总利息
            double leftTotalMoney = leftLoan + leftInterest;//还款总额
            double saveInterest = totalMoney-payTotal-aheadTotalMoney-leftTotalMoney;

            result.setTotalMoney(totalMoney);
            result.setPrincipal(principal);
            result.setInterest(interest);
            result.setFirstMonth(firstMonth);
            result.setDecreaseMonth(decreaseMonth);
            result.setPayTotal(payTotal);
            result.setPayLoan(payLoan);
            result.setPayInterest(payInterest);
            result.setAheadTotalMoney(aheadTotalMoney);
            result.setLeftTotalMoney(leftTotalMoney);
            result.setLeftInterest(leftInterest);
            result.setNewFirstMonth(newFirstMonth);
            result.setNewDecreaseMonth(newDecreaseMonth);
            result.setSaveInterest(saveInterest);
            result.setMonths(leftMonth);

            String lastPayMonth = standFormat.format(DateUtils.addMonths(now, leftMonth));
            result.setLastPayMonth(lastPayMonth);
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
