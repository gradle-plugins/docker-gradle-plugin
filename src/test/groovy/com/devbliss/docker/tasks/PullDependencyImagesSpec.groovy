package com.devbliss.docker.tasks

import com.devbliss.docker.Configuration
import com.devbliss.docker.task.PullDependencyImages
import de.gesellix.docker.client.DockerClient
import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class PullDependencyImagesSpec extends Specification {

    Project project
    PullDependencyImages task
    DockerClient dockerClient
    String name
    String name2
    String name3
    String registry
    String repository
    String tag

    def setup() {
        project = ProjectBuilder.builder().build()
        task = project.task('cleanupOldContainers', type: PullDependencyImages)
        dockerClient = Mock(DockerClient)
        task.dockerClient = dockerClient
        name = "service1"
        registry = "dockerRegistry"
        repository = "dockerRepository"
        tag = "tag"
        name2 = "service2"
        name3 = "service3"
    }

    def "pullImageFromRegistry"() {
        given:
        task.dockerRepository = repository
        task.versionTag = tag
        task.dockerRegistry = registry

        when:
        task.pullImageFromRegistry(name)

        then:
        1 * dockerClient.pull("${repository}/${name}", tag, registry)
    }

    def "splitDependingContainersStringAndPullImage"() {
        given:
        task.dependingContainers = "${name}#8080,${name2}#8082,${name3}#8081"
        task.dockerRepository = repository
        task.versionTag = tag
        task.dockerRegistry = registry
        task.dockerAlreadyHandledList = [name3]

        when:
        task.run()

        then:
        1 * dockerClient.pull("${repository}/${name}", tag, registry)
        1 * dockerClient.pull("${repository}/${name2}", tag, registry)
        0 * dockerClient.pull("${repository}/${name3}", tag, registry)
    }
}
