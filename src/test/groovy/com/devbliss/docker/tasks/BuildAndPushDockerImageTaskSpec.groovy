package com.devbliss.docker.tasks

import com.devbliss.docker.task.BuildAndPushDockerImageTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class BuildAndPushDockerImageTaskSpec extends Specification {

    def project
    def task

    def setup() {
        project = ProjectBuilder.builder().build()
        task = project.task('buildAndPushDockerImage', type: BuildAndPushDockerImageTask)
    }

    def "execute task does not throw any exception"() {
        when:
        task.execute()

        then:
        assert (true)
    }
}
