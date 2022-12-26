package com.waterflow.rich.service;

import com.waterflow.rich.bean.BuyPoint;
import com.waterflow.rich.bean.CheckView;
import com.waterflow.rich.bean.Quote;
import com.waterflow.rich.bean.QuoteView;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.grab.QuoteGrab;
import com.waterflow.rich.strategy.RichBean;
import com.waterflow.rich.strategy.StdStrategy;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HkService {

    @Autowired
    QuoteGrab quoteGrab;



}
