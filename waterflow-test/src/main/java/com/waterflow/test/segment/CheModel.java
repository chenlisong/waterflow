package com.waterflow.test.segment;

public class CheModel {

    private String brandName;

    private String seriesName;

    private String modelName;

    private Long modelId;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public CheModel(String brandName, String seriesName, String modelName) {
        this.brandName = brandName;
        this.seriesName = seriesName;
        this.modelName = modelName;
    }

    public CheModel() {
    }
    public String simpleString() {
        return brandName + " " + seriesName + " " + modelName;
    }
}
