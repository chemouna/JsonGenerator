package com.mounacheikhna.jsongenerator

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by m.cheikhna on 18/02/2016.
 */
class GenerateJsonPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("jsonGenerator", GenerateJsonExtension)
        project.afterEvaluate {
            project.tasks.create(name: "GenerateJsonFiles",
                    type: GenerateJsonTask,
                    group: "Generate Json") {
                firstPassConfig("${project.projectDir}/src/$screenshotProductFlavor/assets/$localeFileName", "##", "##", false)
                secondPassConfig("${project.projectDir}/${project.screenshots.imagesConfigFilePath}", "", "", false)
                productFlavor("custom")
                jsonsMappings([:])
            }
        }
    }
}
