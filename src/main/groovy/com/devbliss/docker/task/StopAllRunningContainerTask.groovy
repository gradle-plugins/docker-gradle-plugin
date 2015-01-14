package com.devbliss.docker.task

import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import org.gradle.api.tasks.TaskAction

/**
 * This class stops all running containers in your configured host vm.
 *
 * Created by Christian Soth <christian.soth@devbliss.com> on 14.01.15.
 */
class StopAllRunningContainerTask extends AbstractDockerTask {

  StopAllRunningContainerTask() {
    description = "Stops all running docker container in your host vm"
    group = "Devbliss"
  }

  def dockerHostStatus = dockerClient.ps()

  @TaskAction
  public void run() {
    dockerHostStatus.each() { container ->
      if (container.Status.contains('Up')) {
        println "Container " + container.Names + " is runing"

        def containerId = container.Names[0].substring(1, container.Names[0].length())
        dockerClient.stop(containerId)
      }
    }
  }
}
