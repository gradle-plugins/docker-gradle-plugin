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
      dockerAlreadyHandledList = dockerAlreadyHandled.replaceAll("\\s", "").split(',')
    } else {
      dockerAlreadyHandledList = new ArrayList<>()
    }
  }

  @TaskAction
  public void run() {
    log.info "Already handled " + dockerAlreadyHandledList
    def dependingContainersList = dependingContainers.replaceAll("\\s", "").split(",")
    splitDependingContainersStringAndPullImage(dependingContainersList)
    setContainerExts()

    def newHandledList = prepareNewdockerAlreadyHandledList(dependingContainersList)
    
    cleanupOldDependencies(dependingContainersList)
    
    String commandArgs = "-P${dockerAlreadyHandledProperty}=" + newHandledList.join(",")

    dependingContainersList.each() { dep ->
      def (name, port) = dep.split("#").toList()
      if (!dockerAlreadyHandledList.contains(name)) {
        if (runningContainers.contains(name)) {
          updateContainerDependencies(name, commandArgs)
        } else {
          port = getPort(port)
          def tcpPort = "${port[0]}/tcp".toString()
          def hostConf = ["PortBindings": [:]]
          hostConf["PortBindings"].put(tcpPort, [["HostPort": port[1]]])
          startContainer(name, "${dockerRepository}/${name.split("_")[0]}", hostConf, commandArgs)
        }
      }
    }
  }

  def void cleanupOldDependencies(dependingContainersList) {
    dockerHostStatus.each() { container ->
      dependingContainersList.each() { dep ->
        def (name, port) = dep.split("#").toList()
        if (name.equals(container.Names[0].substring(1, container.Names[0].length()))) {
          if (!container.Image.contains(name.split("_")[0]) || !runningContainers.contains(name)) {
            stopAndRemoveContainer(name)
          }
        }
      }
    }
  }

  def splitDependingContainersStringAndPullImage(dependingContainersList) {
    log.info("depending container: " + dependingContainersList)
    dependingContainersList.each() { dep ->
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
    existingContainers.remove(name)
    if (runningContainers.contains(name)) {
      runningContainers.remove(name)
    }
  }

  def void updateContainerDependencies(String name, commandArgs) {
    log.info "Update " + name + " CommandArgs: "+"./gradlew startDependencies '" + commandArgs + "'"
    dockerClient.exec(name, ["./gradlew", "startDependencies", commandArgs])
  }

  def void startContainer(name, image, hostConfiguration, command, entryPoint) {
    log.info("Start Container: " + name + " => " + image + " => " + hostConfiguration)
    dockerClient.run(image.toString(), ["HostConfig": hostConfiguration, "Cmd":command], versionTag, name)
  }

  def getPort(port) {
    if (port.contains("-")) {
      return port.split("-").toList()
    }
    return [port, port]
  }
  
  def List<String> getdockerAlreadyHandledList() {
    return dockerAlreadyHandledList
  }
  
  def Set<String> prepareNewdockerAlreadyHandledList(additional) {
    def newList = [] as Set
    newList.addAll(dockerAlreadyHandledList)
    additional.each({ item ->
        def (name, port) = item.split("#").toList()
        newList.addAll(name)
      })
    return newList
  }
}
