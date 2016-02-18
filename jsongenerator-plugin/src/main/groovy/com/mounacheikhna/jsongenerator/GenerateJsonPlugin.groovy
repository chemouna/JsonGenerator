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
                    type: GenerateDynamicJsonTask,
                    group: "Generate Json") {
                propertiesPath project.jsonGenerator.propertiesPath
                imagesPropertiesPath project.jsonGenerator.imagesPropertiesPath
                jsonPropertiesPath project.jsonGenerator.jsonPropertiesPath
            }
        }
    }
}
