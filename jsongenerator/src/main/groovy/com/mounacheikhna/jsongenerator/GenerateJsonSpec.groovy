package com.mounacheikhna.jsongenerator

public interface GenerateJsonSpec {

    public void locale(String locale)
    public void firstPassConfig(String firstPassConfigPath, String startKeyDelimiter,
                         String endKeyDelimiter, boolean escaped)
    public void secondPassConfig(String secondPassConfigPath, String startKeyDelimiter,
                          String endKeyDelimiter, boolean escaped)
    public void productFlavor(String productFlavor)
    public void jsonsMappings(Map<String, String> mapping)

}