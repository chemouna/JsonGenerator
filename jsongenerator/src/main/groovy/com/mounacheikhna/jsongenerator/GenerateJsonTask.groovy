package com.mounacheikhna.jsongenerator

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


/**
 * Task to generate Json files per locale.
 */
public class GenerateJsonTask extends DefaultTask implements GenerateJsonSpec {

    private String locale

    private PassConfig firstPassConfig
    private PassConfig secondPassConfig
    private List<File> generatedFiles
    private String productFlavor
    private Map<String, String> jsonsMappings = [:]

    @TaskAction
    public void performTask() {
        generatedFiles = new ArrayList<>()
        if (firstPassConfig == null) {
            throw new IllegalArgumentException("Please provide a config file.")
        }
        if (firstPassConfig.validate()) {
            generateJsonForLocale()
        }
    }

    @Override
    public void locale(String locale) {
        this.locale = locale
    }

    @Override
    public void firstPassConfig(String firstPassConfigPath, String startKeyDelimiter, String endKeyDelimiter,
                         boolean escaped) {
        firstPassConfig =
                new PassConfig(firstPassConfigPath, startKeyDelimiter, endKeyDelimiter, escaped)
    }

    @Override
    public void secondPassConfig(String secondPassConfigPath, String startKeyDelimiter,
                          String endKeyDelimiter, boolean escaped) {
        secondPassConfig =
                new PassConfig(secondPassConfigPath, startKeyDelimiter, endKeyDelimiter, escaped)
    }

    @Override
    public void productFlavor(String productFlavor) {
        this.productFlavor = productFlavor
    }

    @Override
    public void jsonsMappings(Map<String, String> mapping) {
        this.jsonsMappings.putAll(mapping)
    }

    private void generateJsonForLocale() {
        firstPassConfig.extractValues()
        secondPassConfig.extractValues()
        JsonConfig jsonConfig = new JsonConfig(locale, productFlavor, firstPassConfig, secondPassConfig)
        jsonConfig.createJsons()
    }

    public class JsonConfig {
        private String locale
        private String baseFolder
        PassConfig firstPassConfig
        PassConfig secondPassConfig

        public JsonConfig(String locale, String productFlavor, PassConfig firstPassConfig,
                   PassConfig secondPassConfig) {
            this.locale = locale
            this.firstPassConfig = firstPassConfig
            this.secondPassConfig = secondPassConfig
            baseFolder = "${project.projectDir}/src/$productFlavor/res/raw"
        }

        void createJsons() {
            jsonsMappings.each {
                placeholder, newFileName ->
                    createJson(placeholder, newFileName.toLowerCase())
            }
        }

        File createJson(placeholderFileName, jsonFileName) {
            def placeholderFile = new File("${baseFolder}/$placeholderFileName")
            File file = new File("${baseFolder}/$jsonFileName")
            File newFile = fillPlaceholders(placeholderFile, file, firstPassConfig)
            return fillPlaceholders(newFile, file, secondPassConfig)
        }

        File fillPlaceholders(File inputFile, File outputFile, PassConfig passConfig) {
            def inputFileText = inputFile.getText('UTF-8')
            passConfig.extractedValues.each { key, value ->
                if (passConfig.escaped) {
                    inputFileText = inputFileText.replaceAll(
                            "\\${passConfig.startKeyDelimiter}$key\\${passConfig.endKeyDelimiter}",
                            value.toString())
                } else {
                    inputFileText = inputFileText.replaceAll(
                            "${passConfig.startKeyDelimiter}$key${passConfig.endKeyDelimiter}", value.toString())
                }
            }
            outputFile.write(inputFileText, 'UTF-8')
            outputFile
        }
    }

    public class PassConfig {
        String filePath
        String startKeyDelimiter
        String endKeyDelimiter
        boolean escaped

        Map<String, String> extractedValues = [:]

        public PassConfig(String filePath, String startKeyDelimiter, String endKeyDelimiter, boolean escaped) {
            this.filePath = filePath
            this.startKeyDelimiter = startKeyDelimiter
            this.endKeyDelimiter = endKeyDelimiter
            this.escaped = escaped
        }

        public void extractValues() {
            if (filePath?.trim()) {
                extractedValues = ParseUtils.valuesFromFile(new File(filePath))
            }
        }

        public boolean validate() {
            if (filePath?.trim() && !new File(filePath).exists()) {
                throw new IllegalArgumentException(
                        "Please provide a correct path to config file : $filePath ")
            }
            return true
        }
    }
}
