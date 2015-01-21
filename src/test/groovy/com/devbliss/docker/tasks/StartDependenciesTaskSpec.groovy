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

  def "add task to startServiceDependencies project"() {
    when:
    task.execute()

    then:
    project.getTasksByName("startServiceDependencies", false).size() == 1
  }
}
