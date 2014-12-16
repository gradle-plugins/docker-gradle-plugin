package com.devbliss.docker.task

import de.gesellix.docker.client.DockerClient
import de.gesellix.docker.client.DockerClientImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

abstract class AbstractDockerTask extends DefaultTask {

  @Input
  @Optional
  def dockerHost
  @Input
  @Optional
  def authConfigPlain
  @Input
  @Optional
  def authConfigEncoded

  DockerClient dockerClient

  def getDockerClient() {
    if (!dockerClient) {
      if (getDockerHost()) {
        dockerClient = new DockerClientImpl(dockerHost: getDockerHost())
      }
      else {
        dockerClient = new DockerClientImpl()
      }
    }
    dockerClient
  }

  def getAuthConfig() {
    if (getAuthConfigPlain()) {
      assert !getAuthConfigEncoded()
      return getDockerClient().encodeAuthConfig(getAuthConfigPlain())
    }
    if (getAuthConfigEncoded()) {
      return getAuthConfigEncoded()
    }
    ''
  }
}
