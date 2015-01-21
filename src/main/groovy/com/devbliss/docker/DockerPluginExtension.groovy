package com.devbliss.docker

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * @author Christian Soth <christian.soth@devbliss.com> on 12.01.15.
 *
 * Configuration extension for docker tasks.
 */
class DockerPluginExtension {

  @Input
  def dockerHost

  @Input
  @Optional
  def authConfigPlain
  
  @Input
  @Optional
  def authConfigEncoded

  @Input
  def imageName
  
  @Input
  @Optional
  def versionTag

  @Input
  @Optional
  def registryName
  
  @Input
  def repositoryName

  @Input
  def buildContextDirectory

  @Input
  def dependingContainers
}
