package com.devbliss.docker.task

import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DockerInfoTask extends AbstractDockerTask {

  private static Logger logger = LoggerFactory.getLogger(DockerInfoTask)

  def info

  DockerInfoTask() {
    super("get system-wide information")
  }

  @TaskAction
  def info() {
    logger.info "running info..."
    info = getDockerClient().info()
  }
}
