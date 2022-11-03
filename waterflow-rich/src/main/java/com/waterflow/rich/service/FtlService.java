package com.waterflow.rich.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.fit.cssbox.demo.ImageRenderer;
import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.layout.BrowserConfig;
import org.fit.cssbox.layout.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.w3c.dom.Document;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FtlService extends ImageRenderer{

    public static void renderHtml2Image(String url) throws Exception {
        //设置必要参数
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", true);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);

        //驱动支持（第二参数表明的是你的phantomjs引擎所在的路径）
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "/Users/chenlisong/Downloads/soft/phantomjs-2.1.1-macosx/bin/phantomjs");
        //创建无界面浏览器对象
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);

        //设置隐性等待（作用于全局）
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        long start = System.currentTimeMillis();
        //打开页面
//        driver.get("https://juejin.im/post/5bb24bafe51d450e4437fd96");
        driver.get(url);
        Thread.sleep(3 * 1000);
        JavascriptExecutor js = driver;
        for (int i = 0; i < 1; i++) {
            js.executeScript("window.scrollBy(0,1000)");
            //睡眠10s等js加载完成
            Thread.sleep(3 * 1000);
        }
        //指定了OutputType.FILE做为参数传递给getScreenshotAs()方法，其含义是将截取的屏幕以文件形式返回。
        File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        Thread.sleep(5000);
        //利用FileUtils工具类的copyFile()方法保存getScreenshotAs()返回的文件对象
        FileUtils.copyFile(srcFile, new File("juejin-01.png"));
        System.out.println("耗时：" + (System.currentTimeMillis() - start) + " 毫秒");
    }

    public static void transferHtml2Image(String htmlFilePath, String imageFilePath, Integer width, Integer height) {
        FtlService render = new FtlService();
        render.setWindowSize(new Dimension(width, height), false);
        String url = new File(htmlFilePath).toURI().toString();
        try {
            FileOutputStream out = new FileOutputStream(imageFilePath);
            render.renderURL(url, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void defineLogicalFonts(BrowserConfig config) {
        super.defineLogicalFonts(config);
        config.setImageLoadTimeout(1000 * 10);
        config.setLoadBackgroundImages(true);
        config.setUseHTML(true);
    }

    private static String getTemplate(String template, Map<String, Object> map) throws IOException, TemplateException,InterruptedException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        String templatePath = "/Users/chenlisong/Documents/code/files/Archive/";
        cfg.setDirectoryForTemplateLoading(new File(templatePath));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setTemplateUpdateDelayMilliseconds(1000l * 5);


        Template temp = cfg.getTemplate(template);
        StringWriter stringWriter = new StringWriter();
        temp.process(map, stringWriter);
        Thread.sleep(1000l * 5);

        String result = stringWriter.getBuffer().toString();

        stringWriter.flush();
        stringWriter.close();

        return result;
    }

    /**
     * 模板文件转图片
     * @param template 模板文件地址
     * @param map 数据map
     * @param tagPath 保存图片地址
     * @param width 图片宽度
     * @param height 图片高度
     * @throws Exception
     */
    public static void turnImage(String template, Map<String, Object> map, String tagPath, int width, int height) throws Exception {
        String html = getTemplate(template, map);

        byte[] bytes = html.getBytes();
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(bin);

        Java2DRenderer renderer = new Java2DRenderer(document, width, height);
        BufferedImage img = renderer.getImage();

        ImageIO.write(img, "jpg", new File(tagPath));
    }

}
