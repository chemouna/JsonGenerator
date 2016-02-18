package com.mounacheikhna.jsongenerator

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


/**
 * Created by m.cheikhna on 16/02/2016.
 */
class GenerateDynamicJsonTask extends DefaultTask implements GenerateDynamicJsonSpec {

    private String propertiesPath
    private String imagesPropertiesPath
    private String jsonPropertiesPath

    private JsonSlurper slurper

    @TaskAction
    void performTask() {
        if(!new File("${getProject().projectDir.getPath()}/$propertiesPath").exists()) {
            throw new IllegalArgumentException("Please provide a correct path to properties file : $propertiesPath ")
        }
        slurper = new JsonSlurper()
        def definitions = this.slurper.parse(new File("${getProject().projectDir.getPath()}/${jsonPropertiesPath}"))

        definitions.supportedLocales.each {
            String it -> generateDynamicJsons(it, definitions)
        }
    }

    void generateDynamicJsons(String locale, Object definitions) {
        File generatedFolder = new File("${project.projectDir.getPath()}/generated")
        generatedFolder.mkdirs()
        File dataFolder = new File("${project.projectDir.getPath()}/data")
        def ourSlurper = this.slurper
        definitions.jsons.each {
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
                .collect { inputFileText = inputFileText.replaceAll("\\{\\{${it.key}\\}\\}", it.value.toString()) }
        outputFile.write(inputFileText, 'UTF-8')
        outputFile
    }


    @Override
    void propertiesPath(String propertiesPath) {
        this.propertiesPath = propertiesPath
    }

    @Override
    void imagesPropertiesPath(String imagesPropertiesPath) {
        this.imagesPropertiesPath = imagesPropertiesPath
    }

    @Override
    void jsonPropertiesPath(String jsonPropertiesFilePath) {
        this.jsonPropertiesPath = jsonPropertiesFilePath
    }

    interface GenerateDynamicJsonSpec  {
        void propertiesPath(String propertiesPath)
        void imagesPropertiesPath(String imagesPropertiesPath)
        void jsonPropertiesPath(String jsonPropertiesPath)
    }
}
