package com.mounacheikhna.jsongenerator

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

/**
 * Created by m.cheikhna on 16/02/2016.
 */
class GenerateDynamicJsonTest {

    public static final String FIXTURE_WORKING_DIR = new File("src/test/fixtures/app")
    private Project project

    @Before
    public void setUp() throws Exception {
        project = ProjectBuilder.builder().withProjectDir(new File(FIXTURE_WORKING_DIR)).build()
        project.apply plugin: 'java'
        project.evaluate()
    }

    @Test
    public void testGenerateJson() {
        Task generateTask = project.tasks.create("generateDynamicJson", GenerateDynamicJsonTask.class)
        generateTask.propertiesPath("locales.properties")
        generateTask.imagesPropertiesFilePath("images.properties")
        generateTask.jsonPropertiesFilePath("definitions.json")
        generateTask.productFlavor("experiment")
        generateTask.execute()
    }

}
