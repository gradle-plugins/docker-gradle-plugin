package com.devbliss.docker

class DockerPluginExtension {

  def dockerHost
  def authConfigPlain
  def authConfigEncoded

  def imageName
  def registryName
  def repositoryName
  def tag

  def buildContextDirectory

  def dependingContainers
}
