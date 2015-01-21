package com.devbliss.docker.tasks

import com.devbliss.docker.task.StopAllRunningContainersTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 21.01.15.
 */
class StopAllRunningContainersTaskSpec extends Specification {

  def project
  def task

  def setup() {
    project = ProjectBuilder.builder().build()
    task = project.task('stopAllRunningContainers', type: StopAllRunningContainersTask)
  }

  def "add task to stopAllRunningContainers project"() {
    when:
    task.execute()

    then:
    project.getTasksByName("startServiceDependencies", false).size() == 1
  }
}
