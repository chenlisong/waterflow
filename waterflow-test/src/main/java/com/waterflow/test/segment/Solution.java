package com.waterflow.test.segment;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.sample.IKAnalzyerDemo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Solution {

    static Logger logger = LoggerFactory.getLogger(Solution.class);

    private Directory directory = null;

    public List<CheModel> loadFile(String filePath) {
        File file = FileUtils.getFile(filePath);
        if(!file.isFile()) {
            return null;
        }

        List<CheModel> cheModels = new ArrayList<>();
        try {
            List<String> list = FileUtils.readLines(file);
            for(String line : list) {
                String[] array = line.split(",");
                if(array.length == 4) {
                    CheModel cheModel = new CheModel();
                    cheModel.setBrandName(array[0]);
                    cheModel.setSeriesName(array[1]);
                    cheModel.setModelName(array[2]);
                    cheModel.setModelId(Long.parseLong(array[3]));
                    cheModels.add(cheModel);
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return cheModels;
    }

    public void buildIndex(List<CheModel> cheModels) throws IOException {
        Analyzer analyzer = new IKAnalyzer();

        Directory ramDirectory = new RAMDirectory();
        IndexWriterConfig iwcl = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
        IndexWriter indexWriter = new IndexWriter(ramDirectory, iwcl);

        for(CheModel cheModel: cheModels) {
            Document doc = new Document();
            doc.add(new TextField("brand", cheModel.getBrandName(), Field.Store.YES));
            doc.add(new TextField("series", cheModel.getSeriesName(), Field.Store.YES));
            doc.add(new TextField("model", cheModel.getModelName(), Field.Store.YES));
            doc.add(new StringField("modelId", cheModel.getModelId() + "", Field.Store.YES));
            indexWriter.addDocument(doc);
        }
//        Document doc = new Document();
//        doc.add(new TextField("brand", "奥迪", Field.Store.YES));
//        doc.add(new TextField("series", "A4L", Field.Store.YES));
//        doc.add(new TextField("model", "2007款 140KM 性能型", Field.Store.YES));
//
//        doc.add(new StringField("modelId", "1001", Field.Store.YES));
//        indexWriter.addDocument(doc);
        indexWriter.close();

        this.directory = ramDirectory;
    }

    public CheModel query(CheModel queryModel) throws Exception{

        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher search = new IndexSearcher(reader);
        QueryParser brandQueryParse = new QueryParser("brand", new IKAnalyzer());
        Query branQuery = brandQueryParse.parse(queryModel.getBrandName());

        QueryParser seriesQueryParse = new QueryParser("series", new IKAnalyzer());
        Query seriesQuery = seriesQueryParse.parse(queryModel.getSeriesName());

        QueryParser modelQueryParse = new QueryParser("model", new IKAnalyzer());
        Query modelQuery = modelQueryParse.parse(queryModel.getModelName());

        BooleanQuery distQuery = new BooleanQuery();
        distQuery.add(branQuery, BooleanClause.Occur.SHOULD);
        distQuery.add(seriesQuery, BooleanClause.Occur.SHOULD);
        distQuery.add(modelQuery, BooleanClause.Occur.SHOULD);

        TopDocs scoreDocs = search.search(distQuery, 3);

        if(scoreDocs.totalHits <= 0) {
            return null;
        }

        CheModel distModel = new CheModel();

        for(int i=0;i <scoreDocs.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = scoreDocs.scoreDocs[i];
            Document doc = search.doc(scoreDoc.doc);

            if(i == 0) {
                distModel.setBrandName(doc.get("brand"));
                distModel.setSeriesName(doc.get("series"));
                distModel.setModelName(doc.get("model"));
            }

            logger.info("search hit model value is {} {} {}, score:{}",
                    doc.get("brand"), doc.get("series"), doc.get("model"), scoreDoc.score);
        }
        return distModel;
    }

    public static void main(String[] args) throws Exception{
        Solution solution = new Solution();
        List<CheModel> cheModels = solution.loadFile("/Users/chenlisong/Desktop/che-model-withid.csv");
        logger.info("build index data suc. size is {}", cheModels.size());

        solution.buildIndex(cheModels);
        CheModel queryModel = new CheModel("奥迪", "A4L", "2019款技术版本");
        CheModel dist = solution.query(queryModel);

        logger.info("dist model is {}, query is {} ", dist.simpleString(), queryModel.simpleString());

    }

}
