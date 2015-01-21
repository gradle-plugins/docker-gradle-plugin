package com.devbliss.docker.task

import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This class pulls and run depending containers in your host vm.
 * The depending containers are configured in you gradle build file.
 *
 * Created by Christian Soth <christian.soth@devbliss.com> on 09.01.15.
 */
@Log
class StartDependenciesTask extends AbstractDockerTask {

  public static final dockerAlreadyHandledProperty = "docker.alreadyHandled";

  @Input
  def dependingContainers
  @Input
  def dockerRepository
  @Input
  def dockerRegistry
  @Input
  def versionTag

  def runningContainers = []
  def existingContainers = []

  def dockerHostStatus

  List<String> dockerAlreadyHandledList

  StartDependenciesTask() {
    description = "Pull images and start depending containers for this Project"
    group = "Devbliss"

    String dockerAlreadyHandled = getProject().hasProperty(dockerAlreadyHandledProperty) ? getProject().getProperty(dockerAlreadyHandledProperty) : null;
    if (dockerAlreadyHandled != null) {
      dockerAlreadyHandledList = dockerAlreadyHandled.split(',')
    } else {
      dockerAlreadyHandledList = new ArrayList<>()
    }
  }

  @TaskAction
  public void run() {
    println "List is " + dockerAlreadyHandledList
    return;
    splitDependingContainersStringAndPullImage()
    setContainerExts()

    def alreadyHandled = [];

    dockerHostStatus = dockerClient.ps()

    dockerHostStatus.each() { container ->
      dependingContainers.replaceAll("\\s", "").split(",").each() { dep ->
        def (name, port) = dep.split("#").toList()
        port = getPort(port)
        if (name.equals(container.Names[0].substring(1, container.Names[0].length()))) {
          if (!container.Image.contains(name) || !runningContainers.contains(name)) {

            stopAndRemoveContainer(name)

            def tcpPort = "${port[1]}/tcp".toString()
            def hostConf = ["PortBindings": [:]]
            hostConf["PortBindings"].put(tcpPort, [["HostPort": port[0]]])

            startContainer(name, "${dockerRegistry}/${dockerRepository}/${name.split("_")[0]}", hostConf)
          }
          alreadyHandled.add(name)
        }
      }
    }
    dependingContainers.replaceAll("\\s", "").split(",").each() { dep ->
      def (name, port) = dep.split("#").toList()
      port = getPort(port)
      if (!alreadyHandled.contains(name)) {
        def tcpPort = "${port[0]}/tcp".toString()
        def hostConf = ["PortBindings": [:]]
        hostConf["PortBindings"].put(tcpPort, [["HostPort": port[1]]])
        startContainer(name, "${dockerRegistry}/${dockerRepository}/${name.split("_")[0]}", hostConf)
      }
    }
  }

  def splitDependingContainersStringAndPullImage() {
    log.info("depending container: " + dependingContainers)

    dependingContainers.replaceAll("\\s", "").split(",").each() { dep ->
      def (name, port) = dep.split("#").toList()
      pullImageFromRegistry(name.split("_")[0])
    }
  }

  def pullImageFromRegistry(name) {
    def imageName = "${dockerRepository}/${name}"
    def tag = versionTag
    def registry = dockerRegistry

    log.info "docker pull"
    dockerClient.pull(imageName, tag, registry)
  }

  def setContainerExts() {

    dockerHostStatus = dockerClient.ps()

    dockerHostStatus.each() { container ->
      def name = container.Names[0].substring(1, container.Names[0].length())
      existingContainers.add(name)
      if (container.Status.contains('Up')) {
        runningContainers.add(name)
      }
    }
  }

  def stopAndRemoveContainer(name) {
    dockerClient.stop(name)
    dockerClient.rm(name)
  }

  def startContainer(name, image, hostConfiguration) {
    log.info("Start Container: " + name + " => " + image + " => " + hostConfiguration)
    dockerClient.run(image.toString(), ["HostConfig": hostConfiguration], versionTag, name)
  }

  def getPort(port) {
    if (port.contains("-")) {
      return port.split("-").toList()
    }
    return [port, port]
  }
}
