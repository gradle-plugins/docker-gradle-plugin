package org.gradliss.docker.tasks

import org.gradliss.docker.task.StopAllRunningContainersTask
import de.gesellix.docker.client.DockerClient
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class StopAllRunningContainersTaskSpec extends Specification {

    def project
    StopAllRunningContainersTask task
    def dockerClient = Mock(DockerClient)

    def setup() {
        project = ProjectBuilder.builder().build()
        task = project.task('stopAllRunningContainers', type: StopAllRunningContainersTask)

    }

    def "stopAllRunningContainers in project"() {
        given:
        task.dockerClient = dockerClient
        dockerClient.ps() >> ["content": [["Names": ["_service2"], "Status": "Up"], ["Names": ["_service3"], "Status": "Exited"]]]

        when:
        task.execute()

        then:
        1 * dockerClient.stop("service2")
    }
}
