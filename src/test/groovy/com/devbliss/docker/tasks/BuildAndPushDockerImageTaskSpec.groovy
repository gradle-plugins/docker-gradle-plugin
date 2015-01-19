package com.devbliss.docker.tasks

import com.devbliss.docker.task.BuildAndPushDockerImageTask
import de.gesellix.docker.client.DockerClient
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 15.01.15.
 */
class BuildAndPushDockerImageTaskSpec extends Specification {

  def project
  def task
  def dockerClient = Mock(DockerClient)

  def setup() {
    project = ProjectBuilder.builder().build()
    task = project.task('buildAndPushDockerImageDevbliss', type: BuildAndPushDockerImageTask)
    task.dockerClient = dockerClient
  }

  def "depends on bootRepackage, buildDockerImage, pushDockerImage"() {

    URL dockerfile = getClass().getResource('/docker/Dockerfile')
    def baseDir = new File(dockerfile.toURI()).parentFile

    println("++++++++++++++++++" + baseDir)
    println("++++++++++++++++++" + dockerfile)

    given:
    task.buildContextDirectory = baseDir
    task.imageName = "user/imageName"

    when:
    task.execute()

    then:
    project.getTasksByName("buildAndPushDockerImageDevbliss", false).size() == 1


    and:
    println "---" + project.getTasksByName("buildAndPushDockerImageDevbliss", false).first()
    task.dependsOn.any { it == project.getTasksByName("buildAndPushDockerImageDevbliss", false).first() }

  }


}
