package com.devbliss.docker

/**
 * @author Christian Soth <christian.soth@devbliss.com> on 12.01.15.
 *
 * Configuration extension for docker tasks.
 */
class DockerPluginExtension {

  def dockerHost

  def authConfigPlain
  def authConfigEncoded

  def imageName
  def versionTag

  def registryName
  def repositoryName

  def buildContextDirectory

  def dependingContainers
}
