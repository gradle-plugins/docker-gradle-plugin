package com.devbliss.docker

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
