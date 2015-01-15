package com.devbliss.docker.task

import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This class stops all running containers in your configured host vm.
 *
 * Created by Christian Soth <christian.soth@devbliss.com> on 14.01.15.
 */
class StopAllRunningContainersTask extends AbstractDockerTask {

  private static Logger LOGGER = LoggerFactory.getLogger(StopAllRunningContainersTask)

  StopAllRunningContainersTask() {
    description = "Stops all running docker containers in your host vm"
    group = "Devbliss"
  }

  def dockerHostStatus

  @TaskAction
  public void run() {
    dockerHostStatus = dockerClient.ps()
    dockerHostStatus.each() { container ->
      if (container.Status.contains('Up')) {
        LOGGER.info("Container " + container.Names + " is running")

        def containerId = container.Names[0].substring(1, container.Names[0].length())
        dockerClient.stop(containerId)
        LOGGER.info("Container " + container.Names + " was stopped")
      }
    }
  }
}
