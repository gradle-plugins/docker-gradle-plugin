package com.devbliss.docker.task

import com.devbliss.docker.Configuration
import com.devbliss.docker.handler.ProgressHandler
import com.devbliss.docker.wrapper.ServiceDependency
import com.devbliss.docker.wrapper.ServiceDockerContainer
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * This class pulls and run depending containers in your host vm.
 * The depending containers are configured in you gradle build file.
 */
@Log
class StartDependenciesTask extends AbstractDockerClusterTask {

    @Input
    @Optional
    String dependingContainers
    @Input
    String dockerRepository
    @Input
    String dockerRegistry
    @Input
    String versionTag

    StartDependenciesTask() {
        super()
        description = "Start depending containers for this Project"
    }

    @TaskAction
    public void run() {
        log.info "Already handled " + dockerAlreadyHandledList
        if (dependingContainers == null) {
            return
        }
        List<ServiceDependency> dependingContainersList = ServiceDependency.parseServiceDependencies(dependingContainers)
        startAndUpdateDepedendencies(dependingContainersList)

        if (dockerAlreadyHandledList.size() == 0) {
            ProgressHandler progressHandler = new ProgressHandler(dockerClient, dependingContainersList)
            progressHandler.waitUnilDependenciesRun()
        }
    }

    String getCommandArgs(List<ServiceDependency> dependingContainersList) {
        Set newHandledSet = prepareNewContainerAlreadyHandledList(dependingContainersList)
        return "-P${Configuration.DOCKER_ALREADY_HANDLED_PROPERTY}=" + newHandledSet.join(",")
    }

    void startAndUpdateDepedendencies(List<ServiceDependency> dependingContainersList) {
        String commandArgs = getCommandArgs(dependingContainersList)
        List<String> runningContainers = getRunningContainers()
        log.info "Running containers => " + runningContainers
        dependingContainersList.each() { ServiceDependency dep ->
            if (!dockerAlreadyHandledList.contains(dep.getName())) {
                startContainer(dep, commandArgs, runningContainers)
            }
        }
    }

    List<String> getRunningContainers() {
        List<ServiceDockerContainer> serviceDockerContainer = ServiceDockerContainer.getServiceContainers(dockerClient)
        List<String> runningContainers = []
        serviceDockerContainer.each() { ServiceDockerContainer container ->
            if (container.isRunning()) {
                runningContainers.add(container.getName())
            }
        }
        return runningContainers
    }

    Set<String> prepareNewContainerAlreadyHandledList(List<ServiceDependency> additionalDependencies) {
        Set newList = [] as Set
        newList.addAll(dockerAlreadyHandledList)
        newList.addAll additionalDependencies.collect { ServiceDependency dep ->
            return dep.getName()
        }
        return newList
    }

    void startContainer(ServiceDependency serviceDependency, String commandArgs, List<String> runningContainers) {
        String image = "${dockerRegistry}/${dockerRepository}/${serviceDependency.getImageName()}"
        String name = serviceDependency.getName()
        if (runningContainers.contains(name)) {
            startDependenciesNonBlockingExec(name, commandArgs)
        } else {
            Map hostConf = prepareHostConfig(serviceDependency)
            log.info("Start Container: " + name + " => " + image + " => " + hostConf)
            dockerClient.run(image.toString(), ["HostConfig": hostConf, "Cmd":commandArgs], versionTag, name)
        }
    }

    void startDependenciesNonBlockingExec(String containerName, String commandArgs) {
        List command = ["./gradlew", Configuration.TASK_NAME_START_DEPENDENCIES, commandArgs]
        Map execConfig = [
        "AttachStdin" : false,
        "Detach"      : true,
        "Tty"         : false]

        log.info "Update " + containerName + " CommandArgs: "+"./gradlew startDependencies '" + commandArgs + "'"
        dockerClient.exec(containerName, command, execConfig)
    }

    Map prepareHostConfig(ServiceDependency serviceDependency) {
        String[] port = serviceDependency.getPortConfiguration()
        String tcpPort = "${port[1]}/tcp".toString()
        Map hostConf = ["PortBindings": [:]]
        hostConf["PortBindings"].put(tcpPort, [["HostPort": port[0]]])
        return hostConf
    }
}
