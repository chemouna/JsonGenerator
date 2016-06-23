package com.mounacheikhna.jsongenerator

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.regex.Matcher


/**
 * Task to generate Json files per locale.
 */
public class GenerateJsonTask extends DefaultTask implements GenerateJsonSpec {

    private String locale
    private List<PassConfig> firstPassConfigs = new ArrayList<>()
    private PassConfig secondPassConfig
    private List<File> generatedFiles
    private String productFlavor
    private Map<String, String> outputFileNamesMappings = [:]

    @TaskAction
    public void performTask() {
        generatedFiles = new ArrayList<>()
        if (firstPassConfigs == null || firstPassConfigs.size() == 0) {
            throw new IllegalArgumentException("Please provide a config file.")
        }

        firstPassConfigs.each {
            if (!it.isValid()) return
        }
        generateJsonForLocale()
    }

    @Override
    public void locale(String locale) {
        this.locale = locale
    }

    public void addFirstPassConfig(String firstPassConfigPath, String startKeyDelimiter, String endKeyDelimiter,
                                   boolean escaped) {
        firstPassConfigs.add(
                new PassConfig(firstPassConfigPath, startKeyDelimiter, endKeyDelimiter, escaped))

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

    public void outputFileNamesMappings(Map<String, String> mapping) {
        this.outputFileNamesMappings.putAll(mapping)
    }

    private void generateJsonForLocale() {
        firstPassConfigs.each {
            it.extractValues()
        }
        if(secondPassConfig != null) {
            secondPassConfig.extractValues()
        }

        List<PassConfig> configs = firstPassConfigs
        String baseFolder = "${project.projectDir}/src/$productFlavor/res/raw"
        outputFileNamesMappings.each {
            placeholderFileName, newFileName ->
                println " placeholderFileName : $placeholderFileName and newFileName : $newFileName"
                File placeholderFile = new File("${baseFolder}/$placeholderFileName")
                File file = new File("${baseFolder}/$newFileName".toLowerCase())
                Map<String, String> values = new HashMap<>()
                configs.each {
                    values.putAll(it.extractedValues)
                }
                println " values to replace $values"
                fillPlaceholders(placeholderFile, file, values, configs)
        }
    }

    File fillPlaceholders(File inputFile, File outputFile, Map<String, String> values, List<PassConfig> configs) {
        def inputFileText = inputFile.getText('UTF-8')
        PassConfig config = configs.get(0)
        values.each { key, value ->
            if (config.escaped) {
                inputFileText = inputFileText.replaceAll(
                        "\\${config.startKeyDelimiter}$key\\${config.endKeyDelimiter}",
                        value.toString())
            } else {
                inputFileText = inputFileText.replaceAll(
                        "${config.startKeyDelimiter}$key${config.endKeyDelimiter}",
                        Matcher.quoteReplacement(value.toString()))
            }
        }
        outputFile.write(inputFileText, 'UTF-8')
        outputFile
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

        public boolean isValid() {
            if (filePath?.trim() && !(new File(filePath).exists())) {
                throw new IllegalArgumentException(
                        "Please provide a correct path to config file : $filePath ")
            }
            return true
        }
    }

}
