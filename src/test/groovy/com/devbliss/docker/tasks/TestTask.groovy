package com.devbliss.docker.tasks

import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 14.01.15.
 */

class TestTask extends AbstractDockerTask {

  @Input
  def imageName = project.devblissDocker.imageName

  @Input
  def versionTag = project.devblissDocker.versionTag

  @Input
  def registryName = project.devblissDocker.registryName

  @Input
  def repositoryName = project.devblissDocker.repositoryName

  @Input
  def buildContextDirectory = project.devblissDocker.buildContextDirectory

  @Input
  def dependingContainers = project.devblissDocker.dependingContainers

  @TaskAction
  def run() {
  }
}
