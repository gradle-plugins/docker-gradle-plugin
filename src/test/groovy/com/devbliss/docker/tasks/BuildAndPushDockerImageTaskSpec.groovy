package com.devbliss.docker.tasks

import com.devbliss.docker.task.BuildAndPushDockerImageTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 15.01.15.
 */
class BuildAndPushDockerImageTaskSpec extends Specification {

  def project
  def task

  def setup() {
    project = ProjectBuilder.builder().build()
    task = project.task('buildAndPushDockerImage', type: BuildAndPushDockerImageTask)
  }

  def "add task to buildAndPushDockerImage project"() {
    when:
    task.execute()

    then:
    project.getTasksByName("buildAndPushDockerImage", false).size() == 1
  }
}
