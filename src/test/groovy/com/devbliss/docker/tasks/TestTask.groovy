package com.devbliss.docker.tasks

import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 14.01.15.
 */

class TestTask extends AbstractDockerTask {

  @Input
  def imageName

  @Input
  def versionTag

  @Input
  def registryName

  @Input
  def repositoryName

  @Input
  def buildContextDirectory

  @Input
  def dependingContainers

  @TaskAction
  def run() {

  }
}
