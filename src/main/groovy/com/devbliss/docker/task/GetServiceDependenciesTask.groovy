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

    def notRunningServiceDependencies = []

    @TaskAction
    public void run() {

        if (dependingContainers != null) {
            List<String> dependingContainersList = dependingContainers.replaceAll("\\s", "").split(",")
            splitDependingContainersString(dependingContainersList)
            println("Depending Container: " + notRunningServiceDependencies)
        } else {
            log.info("No depending container for service.")
        }
    }

    void splitDependingContainersString(List<String> dependingContainersList) {
        log.info("#### Depending container: " + dependingContainersList)
        dependingContainersList.each() { dep ->
            def (name, port) = dep.split("#").toList()
            log.info("#### Splitted Depending Container string " + name.split("_")[0])
            getDependingServiceFromContainer(name.split("_")[0])
        }
    }

    void getDependingServiceFromContainer(String name) {

        String gradleProperties = dockerClient.exec(name, ["cat", "gradle.properties"])
        gradleProperties.eachLine {
            if (it.startsWith("dependingEcosystemServices=")) {
                def cleanString = it.substring(it.indexOf("=") + 1)
                List<String> dependingContainersList = cleanString.replaceAll("\\s", "").split(",")

                dependingContainersList.each() { dep ->
                    def (serviceName, port) = dep.split("#").toList()
                    serviceName = serviceName.split("_")[0]

                    if (!notRunningServiceDependencies.contains(serviceName)) {
                        notRunningServiceDependencies.add(serviceName)
                        getDependingServiceFromContainer(serviceName)
                    }
                }
            }
        }
    }
}
