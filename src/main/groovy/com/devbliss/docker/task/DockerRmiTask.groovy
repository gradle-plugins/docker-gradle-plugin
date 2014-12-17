package com.devbliss.docker.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DockerRmiTask extends AbstractDockerTask {

  private static Logger logger = LoggerFactory.getLogger(DockerRmiTask)

  @Input
  def imageId

  DockerRmiTask() {
    super("removes an image or tag")
  }

  @TaskAction
  def rmi() {
    logger.info "running rmi..."
    getDockerClient().rmi(getImageId())
  }
}
