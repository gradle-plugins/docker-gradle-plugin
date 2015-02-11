package com.devbliss.docker.task

import com.devbliss.docker.Configuration
import com.devbliss.docker.handler.ProgressHandler
import com.devbliss.docker.util.DependencyStringUtils
import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * This class pulls and run depending containers in your host vm.
 * The depending containers are configured in you gradle build file.
 *
 * Created by Christian Soth <christian.soth@devbliss.com> on 09.01.15.
 */
@Log
class StartDependenciesTask extends AbstractDockerTask {

    @Input
    @Optional
    def dependingContainers
    @Input
    def dockerRepository
    @Input
    def dockerRegistry
    @Input
    def versionTag

    List<String> dockerAlreadyHandledList

    StartDependenciesTask() {
        description = "Pull images and start depending containers for this Project"
        group = "Devbliss"

        if (getProject().hasProperty(Configuration.dockerAlreadyHandledProperty)) {
            String dockerAlreadyHandled = getProject().getProperty(Configuration.dockerAlreadyHandledProperty)
            dockerAlreadyHandledList = DependencyStringUtils.splitServiceDependenciesString(dockerAlreadyHandled)
        } else {
            dockerAlreadyHandledList = new ArrayList<>()
        }
    }

    @TaskAction
    public void run() {
        log.info "Already handled " + dockerAlreadyHandledList
        if (dependingContainers == null) {
            return
        }
        List<String> dependingContainersList = DependencyStringUtils.splitServiceDependenciesString(dependingContainers)

        List<String> runningContainers = getRunningContainers()

        Set newHandledList = prepareNewdockerAlreadyHandledList(dependingContainersList)

        String commandArgs = "-P${Configuration.dockerAlreadyHandledProperty}=" + newHandledList.join(",")

        log.info "Running containers => " + runningContainers

        dependingContainersList.each() { dep ->
            def (name, port) = DependencyStringUtils.getDependencyNameAndPort(dep)
            if (!dockerAlreadyHandledList.contains(name)) {
                startContainer(name, "${dockerRegistry}/${dockerRepository}/${name.split("_")[0]}", port, commandArgs)
            }
        }

        ProgressHandler progressHandler = new ProgressHandler(dockerClient, dependingContainersList)
        progressHandler.waitUnilDependenciesRun()
    }

    Map prepareHostConfig(String portConfig) {
        def port = getPort(portConfig)
        def tcpPort = "${port[1]}/tcp".toString()
        def hostConf = ["PortBindings": [:]]
        hostConf["PortBindings"].put(tcpPort, [["HostPort": port[0]]])
        return hostConf
    }

    List<String> getRunningContainers() {
        List<String> runningContainers = []
        def dockerHostStatus = dockerClient.ps()
        dockerHostStatus.each() { container ->
            def name = container.Names[0].substring(1, container.Names[0].length())
            if (container.Status.contains('Up')) {
                runningContainers.add(name)
            }
        }
        return runningContainers
    }

    void startContainer(String name, String image, String port, command) {
        if (runningContainers.contains(name)) {
            log.info "Update " + name + " CommandArgs: "+"./gradlew startDependencies '" + command + "'"
            dockerClient.exec(name, ["./gradlew", Configuration.TASK_NAME_START_DEPENDENCIES, command])
        } else {
            Map hostConf = prepareHostConfig(port)
            log.info("Start Container: " + name + " => " + image + " => " + hostConf)
            dockerClient.run(image.toString(), ["HostConfig": hostConf, "Cmd":command], versionTag, name)
        }
    }

    String[] getPort(String port) {
        if (port.contains("-")) {
            return port.split("-").toList()
        }
        return [port, port]
    }

    Set<String> prepareNewdockerAlreadyHandledList(List<String> additionalDependencies) {
        Set newList = [] as Set
        newList.addAll(dockerAlreadyHandledList)
        newList.addAll additionalDependencies.collect { item ->
            def (name, port) = DependencyStringUtils.getDependencyNameAndPort(item)
            return name
        }
        return newList
    }
}
