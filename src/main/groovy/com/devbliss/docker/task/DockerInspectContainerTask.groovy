package com.devbliss.docker.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DockerInspectContainerTask extends AbstractDockerTask {

  private static Logger logger = LoggerFactory.getLogger(DockerInspectContainerTask)

  @Input
  def containerId

  def containerInfo

  DockerInspectContainerTask() {
    super("inspects a container")
  }

  @TaskAction
  def inspect() {
    logger.info "running inspect container..."

    containerInfo = getDockerClient().inspectContainer(getContainerId())
  }
}
