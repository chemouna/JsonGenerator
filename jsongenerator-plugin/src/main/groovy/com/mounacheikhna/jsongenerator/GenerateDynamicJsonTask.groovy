package com.mounacheikhna.jsongenerator

/**
 * Created by m.cheikhna on 16/02/2016.
 */
class GenerateDynamicJsonTask extends DefaultTask implements GenerateDynamicJsonSpec {

    private String locale
    private String propertiesPath
    private String imagesPropertiesFilePath
    private String jsonPropertiesFilePath

    private List<File> generatedFiles
    private String productFlavor

    @TaskAction
    void performTask() {
        generatedFiles = new ArrayList<>()
        if(!new File(propertiesPath).exists()) {
            throw new IllegalArgumentException("Please provide a correct path to properties file : $propertiesPath ")
        }
        generateDynamicJsons(locale, jsonPropertiesFilePath, propertiesPath, imagesPropertiesFilePath)
    }

    void generateDynamicJsons(String locale, String jsonPropetiesPath, String propertiesPath, String imagesPropertiesFilePath) {

        //for now since in properties file we only have -> But maybe a json config instead would be better
        Properties properties = ParseUtils.parseProperties(jsonPropetiesPath)
        properties.findAll { k, v -> k.contains("template") }.each {
            generatedJsonName, templateName ->
                println " "
        }
    }

    @Override
    void locale(String locale) {
        this.locale = locale
    }

    @Override
    void productFlavor(String productFlavor) {
        this.productFlavor = productFlavor
    }

    @Override
    void propertiesPath(String propertiesPath) {
        this.propertiesPath = propertiesPath
    }

    @Override
    void imagesPropertiesFilePath(String imagesPropertiesFilePath) {
        this.imagesPropertiesFilePath = imagesPropertiesFilePath
    }

    @Override
    void jsonPropertiesFilePath(String productFlavor) {
        this.productFlavor = productFlavor
    }

    interface GenerateDynamicJsonSpec  {
        void locale(String locale)
        void productFlavor(String productFlavor)
        void propertiesPath(String propertiesPath)
        void imagesPropertiesFilePath(String imagesPropertiesFilePath)
        void jsonPropertiesFilePath(String productFlavor)
    }
}
