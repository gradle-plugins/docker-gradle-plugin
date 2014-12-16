package com.devbliss.docker.task

import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DockerImagesTask extends AbstractDockerTask {

  private static Logger logger = LoggerFactory.getLogger(DockerPsTask)

  def images

  @TaskAction
  def images() {
    logger.info "running images..."
    images = getDockerClient().images()
  }
}
