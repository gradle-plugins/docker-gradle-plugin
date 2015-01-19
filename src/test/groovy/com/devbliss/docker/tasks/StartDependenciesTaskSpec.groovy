package com.devbliss.docker.tasks

import com.devbliss.docker.task.StartDependenciesTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 19.01.15.
 */
class StartDependenciesTaskSpec extends Specification {

  def project
  def task

  def setup() {
    project = ProjectBuilder.builder().build()
    task = project.task('startServiceDependencies', type: StartDependenciesTask)
  }

  def "split depending container string"() {
    when:
    task.execute()

    then:
    task.name == "service1"
  }
}
