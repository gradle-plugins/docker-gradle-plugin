package com.devbliss.docker.task

import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DockerPsTask extends AbstractDockerTask {

  private static Logger logger = LoggerFactory.getLogger(DockerPsTask)

  def containers

  @TaskAction
  def ps() {
    logger.info "running ps..."
    containers = getDockerClient().ps()
  }
}
