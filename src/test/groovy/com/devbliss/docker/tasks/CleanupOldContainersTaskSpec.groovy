package com.devbliss.docker.tasks

import com.devbliss.docker.Configuration
import com.devbliss.docker.task.CleanupOldContainersTask
import de.gesellix.docker.client.DockerClient
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 15.01.15.
 */
class CleanupOldContainersTaskSpec extends Specification {

    Project project
    CleanupOldContainersTask task
    DockerClient dockerClient
    String name
    String name2
    String name3
    String registry
    String repository
    String tag

    def setup() {
        project = ProjectBuilder.builder().build()
        task = project.task('cleanupOldContainers', type: CleanupOldContainersTask)
        dockerClient = Mock(DockerClient)
        task.dockerClient = dockerClient
        name = "service1"
        name2 = "service2"
        name3 = "service3"
    }

    def "cleanupOldDependencies"() {
        given:
        dockerClient.ps() >> [["Names":["_$name"], "Image":"435hi3u5h345", "Status": "Up"],
            ["Names":["_$name2"], "Image":"435hi3u5h345/$name2", "Status":"Exited"],
            ["Names":["_$name3"], "Image":"435hi3u5h345", "Status":"Exited"]]
        task.dependingContainers = "${name}#8080,${name2}#8082,${name3}#8081"
        task.dockerAlreadyHandledList = [name3]

        when:
        task.run()

        then:
        1 * dockerClient.stop(name)
        1 * dockerClient.rm(name)
        1 * dockerClient.stop(name2)
        1 * dockerClient.rm(name2)
        0 * dockerClient.stop(name3)
        0 * dockerClient.rm(name3)
    }

    def "stopAndRemoveContainer"() {
    given:

    when:
    task.stopAndRemoveContainer(name)

    then:
    1 * dockerClient.stop(name)
    1 * dockerClient.rm(name)
  }
}
