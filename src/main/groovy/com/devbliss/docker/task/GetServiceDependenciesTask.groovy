package com.devbliss.docker.task

import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 09.02.15.
 */

@Log
class GetServiceDependenciesTask extends AbstractDockerTask {

  GetServiceDependenciesTask() {
    description = "Get dependencies for any individual Service."
    group = "Devbliss"
  }

  @Input
  @Optional
  def dependingContainers
  @Input
  def dockerRepository
  @Input
  def dockerRegistry
  @Input
  def versionTag

  def runningServiceDependencies = []
  def notRunningServiceDependencies = []

  @TaskAction
  public void run() {
    List<String> dependingContainersList = dependingContainers.replaceAll("\\s", "").split(",")
    splitDependingContainersString(dependingContainersList)

    println "Running Container   ------>" + runningServiceDependencies
    println "Depending Container ------>" + notRunningServiceDependencies

  }

  def splitDependingContainersString(List<String> dependingContainersList) {
    log.info("#### Depending container: " + dependingContainersList)
    dependingContainersList.each() { dep ->
      def (name, port) = dep.split("#").toList()
      log.info("#### Splitted Depending Container string " + name.split("_")[0])
      checkIfContainerIsRunning(name)
    }
  }

  void checkIfContainerIsRunning(String name) {
    def inspectContainerResponse = dockerClient.inspectContainer(name)
    log.info("#### Check running state from container: " + name + " = " + inspectContainerResponse.State.Running)

    if (inspectContainerResponse.State.Running == true) {
      println "Service running: " + name
      runningServiceDependencies.add(name)
      getDependingServiceFromContainer(name)
    } else {
      notRunningServiceDependencies.add(name)
    }
  }

  void getDependingServiceFromContainer(String name) {
    def gradleProperties = dockerClient.exec(name, ["cat", "gradle.properties"])

    gradleProperties.eachLine {
      if (it.startsWith("dependingEcosystemServices=")) {
        def cleanString = it.substring(it.indexOf("=") + 1)
        List<String> dependingContainersList = cleanString.replaceAll("\\s", "").split(",")

        dependingContainersList.each() { dep ->
          def (serviceName, port) = dep.split("#").toList()
          serviceName = serviceName.split("_")[0]
          if (!runningServiceDependencies.contains(serviceName) && !notRunningServiceDependencies.contains
                  (serviceName)) {
            notRunningServiceDependencies.add(serviceName)
          }
        }
      }
    }
  }
}
