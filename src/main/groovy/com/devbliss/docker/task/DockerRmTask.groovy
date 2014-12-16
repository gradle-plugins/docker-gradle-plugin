package com.devbliss.docker.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DockerRmTask extends AbstractDockerTask {

  private static Logger logger = LoggerFactory.getLogger(DockerRmTask)

  @Input
  def containerId

  DockerRmTask() {
    super("removes a container")
  }

  @TaskAction
  def rm() {
    logger.info "running rm..."
    getDockerClient().rm(getContainerId())
  }
}
