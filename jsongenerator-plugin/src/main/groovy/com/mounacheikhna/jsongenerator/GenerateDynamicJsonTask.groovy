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
        generateDynamicJsons(locale, jsonPropertiesFilePath)
    }

    void generateDynamicJsons(String locale, String jsonPropertiesPath) {
        File generatedFolder = new File("${project.projectDir.getPath()}/generated")
        generatedFolder.mkdirs()
        File dataFolder = new File("${project.projectDir.getPath()}/data")
        def definitions = this.slurper.parse(new File("${getProject().projectDir.getPath()}/${jsonPropertiesPath}")) //

        def ourSlurper = this.slurper
        definitions.locales.each {
            String generatedFileName = it.generatedName.replace("{{locale}}", locale);
            File generatedFile = new File("${generatedFolder.getPath()}/$generatedFileName")
            File templateFile = new File("${project.projectDir}/templates/${it.template}.json")

            def data = ourSlurper.parse(new File("${dataFolder}/${locale}.json")) //here
            fillPlaceholders(templateFile, generatedFile, data)
        }
    }

    static def fillPlaceholders(File inputFile, File outputFile, Object data) {
        def inputFileText = inputFile.getText('UTF-8')
        data.findAll({ it -> inputFileText.contains("{{${it.key}}") })
                .collect { inputFileText = inputFileText.replaceAll("\\{\\{"+ it.key +"\\}\\}", it.value.toString()) }
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
