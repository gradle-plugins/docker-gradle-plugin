package com.devbliss.docker.tasks

import com.devbliss.docker.task.StopAllRunningContainersTask
import de.gesellix.docker.client.DockerClient
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class StopAllRunningContainersTaskSpec extends Specification {

    def project
    def task
    def dockerHostStatus
    def container
    def dockerClient = Mock(DockerClient)

    def setup() {
        project = ProjectBuilder.builder().build()
        task = project.task('stopAllRunningContainers', type: StopAllRunningContainersTask)
    }

    def "stopAllRunningContainers in project"() {
        given:
        task.dockerClient = dockerClient

        when:
        task.execute()

        then: "TODO check container"
        println "--" + task.dockerHostStatus
    }
}
