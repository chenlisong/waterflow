package com.example.demo;

import com.waterflow.test.segment.CheModel;
import com.waterflow.test.segment.VinConvert;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.LoadLibs;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class OcrTest {

    Logger logger = LoggerFactory.getLogger(OcrTest.class);

    @Test
    public void ocrTest() throws Exception{

        File tmpFolder = LoadLibs.extractTessResources("win32-x86-64");

        System.setProperty("java.library.path", tmpFolder.getPath());

        File[] imageFiles = new File("/Users/chenlisong/Downloads/vins/").listFiles();

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("/Users/chenlisong/Downloads/tess/");
        tesseract.setLanguage("deu");
        tesseract.setTessVariable("user_defined_dpi", "300");

        for(File imageFile : imageFiles) {
            String result = tesseract.doOCR(imageFile);
            logger.info("file name is {}, result is {}", imageFile.getName(), result);
        }
    }
}
