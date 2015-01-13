package com.devbliss.docker.task

import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import de.gesellix.gradle.docker.tasks.DockerRmTask
import de.gesellix.gradle.docker.tasks.DockerRunTask
import de.gesellix.gradle.docker.tasks.DockerStopTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 09.01.15.
 */
class StartDependenciesTask extends AbstractDockerTask {

  private static Logger LOGGER = LoggerFactory.getLogger(StartDependenciesTask)

  def dependingContainers = project.devblissDocker.dependingContainers
  def dockerRepository = project.devblissDocker.repositoryName
  def dockerRegistry = project.devblissDocker.registryName
  def versionTag = project.devblissDocker.versionTag

  def runningContainers = []
  def existingContainers = []

  StartDependenciesTask() {
    description = "Pull images and start depending containers for this container"
    group = "Devbliss"
  }

  @TaskAction
  public void run() {
    splitDependingContainersString()
//    setContainerExts()
//    doLast {
    def alreadyHandled = [];
//      containers.each() { container ->
    dependingContainers.replaceAll("\\s", "").split(",").each() { dep ->
      def (name, port) = dep.split("#").toList()
      port = getPort(port)
//          if (name.equals(container.Names[0].substring(1, container.Names[0].length()))) {
//            if (!container.Image.contains(name) || !project.ext.runningContainers.contains(name)) {
//      stopContainer(name)
//      removeContainer(name)
      def tcpPort = "${port[1]}/tcp".toString()
      def hostConf = ["PortBindings": [:]]
      hostConf["PortBindings"].put(tcpPort, [["HostPort": port[0]]])
      startContainer(name, "${dockerRegistry}/${dockerRepository}/${name.split("_")[0]}", hostConf)
    }
//    alreadyHandled.add(name)
//          }
//        }
//      }
//    dependingContainers.replaceAll("\\s", "").split(",").each() { dep ->
//      def (name, port) = dep.split("#").toList()
//      port = getPort(port)
//      if (!alreadyHandled.contains(name)) {
//        def tcpPort = "${port[0]}/tcp".toString()
//        def hostConf = ["PortBindings": [:]]
//        hostConf["PortBindings"].put(tcpPort, [["HostPort": port[1]]])
//        startContainer(name, "${dockerRegistry}/${dockerRepository}/${name.split("_")[0]}", hostConf)
//      }
//    }
//    }

  }

  def splitDependingContainersString() {
    LOGGER.info("Depending container: " + dependingContainers)

    dependingContainers.replaceAll("\\s", "").split(",").each() { dep ->
      def (name, port) = dep.split("#").toList()
      pullImage(name.split("_")[0])
    }
  }

  def pullImage(name) {
    def imageName = "${dockerRepository}/${name}"
    def tag = versionTag
    def registry = dockerRegistry

    logger.info "docker pull"
    dockerClient.pull(imageName, tag, registry)
  }

  def getPort(port) {
    if (port.contains("-")) {
      return port.split("-").toList()
    }
    return [port, port]
  }

  def setContainerExts() {

    doLast {
      containers.each() { container ->
        def name = container.Names[0].substring(1, container.Names[0].length())
        existingContainers.add(name)
        if (container.Status.contains('Up')) {
          runningContainers.add(name)
        }
      }
    }
  }


  def removeContainer(name) {
//    if (ext.existingContainers.contains(name)) {
    dockerClient.rm(name)
//    }
  }

  def stopContainer(name) {
//    if (ext.runningContainers.contains(name)) {
    dockerClient.stop(name)
//    }
  }
//  def run(fromImage, containerConfig, tag = "", name = "") {
  def startContainer(name, image, hostConfiguration) {
    println "Start Container: " + name + " => " + image + " => " + hostConfiguration
//    def taskToRun = task(type: DockerRunTask) {}
//    taskToRun.imageName = image.toString()
//    taskToRun.containerName = name
//    taskToRun.hostConfiguration = hostConfiguration
//    taskToRun.execute()
    dockerClient.run(image.toString(), hostConfiguration, versionTag, name)
  }
}
