package com.example.demo;

import com.waterflow.test.segment.CheModel;
import com.waterflow.test.segment.VinConvert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VinConvertTest {

    VinConvert vinConvert = new VinConvert();

    Logger logger = LoggerFactory.getLogger(VinConvertTest.class);

    @Test
    public void vinConvertTest() throws Exception{

        String vins = "LBV7Y6107MSZ32986,LGG8D3D18HZ520405,LVGBY80EXJG085964,LP4TG41B2JL148084,LSVAN4184C2036698,LGBF5DE02DR030803,LNBSCB3F6KW165212,LVSHFSAF79F012918,LS4ASE2E1KJ147057,LSVAN4188B2748758,LFB1E6072LJE58474,WBABU31048LH65266,LSGBC5342HG112964,LFV3A28K1B3037175,LB37724Z5JX087763,LFV2A21GX93156631,LSGTB54M09Y125276,LSGKR53LXGA001733,JS3JB43V0C4201568,LJNMFWBY6MN243207,LFMA8E2A1F0104440,LSVGP40G2JN015258,LVVDB21B3HD183279,LVSFDFABXDN275534,LSVG726R7K2029785,LDC931L37A1359055,LBES6AFD0KW095244,LFV3A28K1G3015054,LL66HAB05EB057646,LGBF1AE0XCR143360,LDCT81149C2061932,JTHBW46G2A2035503,LHGCM462X72503721,LVZA53P99JA020145,LSGGA54Y4DH039498,LBECFAHC7FZ172754,LC0CE4CC9K0024866,LFV3A23C3J3033496,LS5A3BBD9CA503612,LS5A3DBE7GA013745,LFV3A23C8D3049343,LFMAP90A6A0054432,LBVEY97014SA41921,LFMA180C4J0187848,LVHRU5864K5023636,LGXCD4D32H0122077,JTHBG96S765038661,LGWEE6A5XHH510736,WP1AG2921HKA03424,LE4WG7HB1KL525245,LE4WG4CB8FL021528,LFV3A28W4G3027363,LC0C14DF7G0050250,WBA6A0106EDY68829,LVSHKADL6GF407595,WDCFB4KB1LA133644,LHGTG1812J8045591,LE40B8BB8LL530625,LGBH92E05KY798506,LSVG226R1H2069433,VF1VYRTY6BC365383,LE40G4DB7HL124382,LK6ADAE24LB200907,WBA11AP09MCE96174,LE4ZG8DB8LL424343,LC0CG6CF2E0028827,LSVAH2180A2471310,LBV61AF0XMS002933,LBECFAHB3FZ217681,ZAMSS57E1G1184978,HHDYEYE2782929292,LGBF1AE05AR008865,WDDBF4DB4JJ674096,LB37724Z0JX107370,LBETLBFCXGED77423,LE40G4KBXHL155922,7H7H2S7H7H2USUH7H,HG5U8UYG6G6Y6Y638,LBETLBFCXGYRUEUH3,LFV3A28W9K3801969,LVHFK7875M6032971,LBETLBFCXGYYEUDH7,LWVCAE78XHA187457,LS4ASB3R2BA743746,LGBH92E08KY856396,LVVDC11B3DD209669,LJDMAA221E0537971,LFMGSE723FS095225,W1NFB5HB9MA327805,LVGBP87E1HG100497,LVSHCFAU4ME145987,LS5A3ASR2FD003820,LJD2AA14XM0262805,LS5A3DKEXLA700809,WDDDJ5GB0AA164465,5YJSA8HY6JF251441,LBV8W3105JMM65639,LFMAP86C5G0178994,HT4H67J5H5444GVT4,LS5A3DKEXLA700809";
        for(String vin : vins.split(",")) {
            CheModel cheModel = vinConvert.search(vin);

            String resp = cheModel == null ? "null" : cheModel.simpleString();

            logger.info("vin is {}, resp chemodel is {}", vin, resp);

            Thread.sleep(500);
        }
    }
}