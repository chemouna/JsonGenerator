package com.mounacheikhna.jsongenerator

import org.gradle.api.Project
import org.gradle.api.Task
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class GenerateJsonTest {

    private Project project

    @Before
    public void setUp() throws Exception {
        Project project = TestHelper.withExtensionEvaluatableProject()
        project.evaluate()
    }

    @Test
    public void testGenerateJsons() {
        Task generateJsonTask = project.tasks.create("generateJsonTask", GenerateJsonTask.class)
        generateJsonTask.locale("en_GB")
        generateJsonTask.firstPassConfig("${project.projectDir}/src/screenshots/assets/en_GB_screenshots.json", "##", "##", true)
        generateJsonTask.secondPassConfig("${project.projectDir}/src/screenshots/images.json", "", "", false)
        generateJsonTask.productFlavor("screenshots")
        generateJsonTask.execute()
    }


}
