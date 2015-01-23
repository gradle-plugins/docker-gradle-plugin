package com.devbliss.docker

import org.gradle.api.Task
import com.devbliss.docker.task.StartDependenciesTask
import com.devbliss.docker.task.BuildAndPushDockerImageTask
import com.devbliss.docker.Configuration
import de.gesellix.docker.client.DockerClient
import de.gesellix.gradle.docker.tasks.DockerPsTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 21.01.15.
 */
class ConfigurationSpec extends Specification {
  def project
  def Configuration task
  def name

  def setup() {
    project = ProjectBuilder.builder().build()
//    project.apply plugin: 'com.devbliss.docker'
    name = "service1"
  }

  def "check default tasks"() {
    given:
    Task testTask = project.task('bootRepackage', type: BuildAndPushDockerImageTask)

    when:
    task = new Configuration(project)

    then:
    1 * testTask.dependsOn('bootRepackage')
    1 * project.tasks.buildAndPushDockerImage.finalizedBy('pushDockerImage')
  }

  def "configureStartServiceDependenciesTasks"() {
    
  }
}
