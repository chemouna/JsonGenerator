package com.mounacheikhna.jsongenerator

import groovy.json.JsonSlurper
import org.apache.commons.collections.map.HashedMap
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


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
    private JsonSlurper slurper

    @TaskAction
    void performTask() {
        if(!new File("${getProject().projectDir.getPath()}/$propertiesPath").exists()) {
            throw new IllegalArgumentException("Please provide a correct path to properties file : $propertiesPath ")
        }
        slurper = new JsonSlurper()

        generateDynamicJsons(locale, jsonPropertiesFilePath, propertiesPath, imagesPropertiesFilePath)
    }

    void generateDynamicJsons(String locale, String jsonPropetiesPath, String propertiesPath, String imagesPropertiesFilePath) {
        File generatedFolder = new File("${project.projectDir.getPath()}/generated")
        generatedFiles.mkdirs()
        def definitions = slurper.parse(new File("${getProject().projectDir.getPath()}/definitions.json"))
        println "definitions : $definitions"

        definitions.locales.each {
            println "local data : $it"
            String generatedFileName = it.generatedName.replace("{{locale}}", locale);
            File generatedFile = new File("${generatedFolder.getPath()}/$generatedFileName")
            File templateFile = new File("${it.template}.json")

            fillPlaceholders(templateFile, generatedFile, new HashedMap<>());
        }
    }

    static def fillPlaceholders(File inputFile, File outputFile, Map<String, String> properties) {
        def inputFileText = inputFile.getText('UTF-8')
        properties.each {
            key, value ->
                if(inputFileText.contains("{{$key}}")) {
                    inputFileText = inputFileText.replaceAll("{{$key}}", value.toString())
                }
        }
        outputFile.write(inputFileText, 'UTF-8')
        outputFile
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
    void jsonPropertiesFilePath(String jsonPropertiesFilePath) {
        this.jsonPropertiesFilePath = jsonPropertiesFilePath
    }

    interface GenerateDynamicJsonSpec  {
        void locale(String locale)
        void productFlavor(String productFlavor)
        void propertiesPath(String propertiesPath)
        void imagesPropertiesFilePath(String imagesPropertiesFilePath)
        void jsonPropertiesFilePath(String productFlavor)
    }
}
