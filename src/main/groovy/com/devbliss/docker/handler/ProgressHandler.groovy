package com.devbliss.docker.handler

import com.devbliss.docker.util.DependencyStringUtils
import de.gesellix.docker.client.DockerClient
import groovy.util.logging.Log

@Log
class ProgressHandler {

    public static final String RECEIVED_DEPENDENCIES = "checked_deps"
    public static final String RUNNING = "running"

    DockerClient dockerClient
    List<String> dependingContainersList
    ProgressOutputGenerator progressOutputGenerator

    ProgressHandler(DockerClient dockerClient, List<String> dependingContainersList) {
        this.dockerClient = dockerClient
        this.dependingContainersList = dependingContainersList
        this.progressOutputGenerator = new ProgressOutputGenerator()
    }

    public void waitUnilDependenciesRun() {
        boolean allRun = false;
        Map<String, Map<String,Boolean>> containerList = prepareStartMap()
        progressOutputGenerator.printServices(containerList)
        while(!allRun) {
            setRunningStates(containerList)
            updateDependenciesMap(containerList)
            progressOutputGenerator.printServices(containerList)
            allRun = checkAllRunning(containerList)
        }
    }

    void setRunningStates(Map<String, Map<String,Boolean>> containerList) {
        List containers = dockerClient.ps()
        containers.each { container ->
            setRunningStateForContainer(containerList, container)
        }
    }

    void updateDependenciesMap(Map<String, Map<String,Boolean>> containerList) {
        Map<String, Map<String,Boolean>> additionalContainer = new HashMap()
        containerList.each { container ->
            if (!container.getValue().get(RECEIVED_DEPENDENCIES) && container.getValue().get(RUNNING)) {
                log.info "Request dependencies of service ${container.getKey()}"
                Map<String, Map<String,Boolean>> newDependencies = getContainerDependencies(
                    container.getKey(), containerList)
                container.getValue().put(RECEIVED_DEPENDENCIES, true)
                if (newDependencies != null && newDependencies.size() > 0) {
                    additionalContainer.putAll(newDependencies)
                }
            }
        }
        if (additionalContainer.size() > 0) {
            containerList.putAll(additionalContainer)
        }
    }

    void setRunningStateForContainer(Map<String, Map<String,Boolean>> containerList, Map container) {
        String containerName = DependencyStringUtils.getServiceNameFromContainer(container)
        if (containerList.containsKey(containerName)) {
            if (isContainerRunning(container)) {
                log.info "Set running state true for ${containerName}"
                containerList.get(containerName).put(RUNNING, true)
            }
        }
    }

    Map<String, Map<String,Boolean>> getContainerDependencies(String serviceName, Map<String, Map<String,Boolean>> containerList) {
        Map<String, Map<String,Boolean>> additionalContainer = new HashMap()
        List<String> dependencies = getServiceDependencies(serviceName)
        dependencies.each { dep ->
            if (!containerList.containsKey(dep)) {
                additionalContainer.put(dep, createNewContainerItem())
            }
        }
        return additionalContainer
    }

    Map<String, Map<String,Boolean>> prepareStartMap() {
        Map<String, Map<String,Boolean>> startList = new HashMap()
        dependingContainersList.each { dep ->
            startList.put(DependencyStringUtils.getDependencyNameAndPort(dep)[0], createNewContainerItem())
        }
        return startList
    }

    Map<String,Boolean> createNewContainerItem() {
        Map<String, Boolean> newDepItem = new HashMap()
        newDepItem.put(RECEIVED_DEPENDENCIES, false)
        newDepItem.put(RUNNING, false)
        return newDepItem
    }

    boolean checkAllRunning(Map<String, Map<String,Boolean>> containerList) {
        return containerList.findAll { container ->
            Map<String, Boolean> value = container.getValue()
            !value.get(RUNNING).and(value.get(RECEIVED_DEPENDENCIES))
        }.size() == 0
    }

    List<String> getServiceDependencies(String serviceName) {
        java.util.LinkedHashMap depsMap = dockerClient.exec(serviceName, ["./gradlew", "serviceDependencies"])
        DependingContainerParser parser = new DependingContainerParser(depsMap.plain);
        List<String> deps = parser.getParsedDependencies();
        return deps
    }

    boolean isContainerRunning(Map container) {
        return container.Status.contains('Up')
    }
}
