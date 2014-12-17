package com.devbliss.docker.task

import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DockerVersionTask extends AbstractDockerTask {

  private static Logger logger = LoggerFactory.getLogger(DockerVersionTask)

  def version

  DockerVersionTask() {
    super("get the docker version information")
  }

  @TaskAction
  def version() {
    logger.info "running version..."
    version = getDockerClient().version()
  }
}
