package com.devbliss.docker.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DockerStartTask extends AbstractDockerTask {

  private static Logger logger = LoggerFactory.getLogger(DockerStopTask)

  @Input
  def containerId

  DockerStartTask() {
    super("starts a container")
  }

  @TaskAction
  def start() {
    logger.info "running start..."
    getDockerClient().startContainer(getContainerId())
  }
}
