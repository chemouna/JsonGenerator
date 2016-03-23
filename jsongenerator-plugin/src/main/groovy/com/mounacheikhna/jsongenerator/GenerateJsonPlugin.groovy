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

                //TODO: this is a good use case for gradle container here
                firstPassConfig(project.jsonGenerator.firstPassConfig,
                        project.jsonGenerator.firstPassConfig.startDelimiter,
                        project.jsonGenerator.firstPassConfig.endDelimiter,
                        project.jsonGenerator.firstPassConfig.escape)

                secondPassConfig(project.jsonGenerator.secondPa ssConfig,
                        project.jsonGenerator.secondPassConfig.startDelimiter,
                        project.jsonGenerator.secondPassConfig.endDelimiter,
                        project.jsonGenerator.secondPassConfig.escape)

                productFlavor("custom")
                jsonsMappings([:])
            }
        }
    }
}
