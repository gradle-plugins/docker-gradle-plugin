package com.devbliss.docker.handler

import com.devbliss.docker.Configuration
import com.devbliss.docker.handler.DependingContainerParser
import de.gesellix.docker.client.DockerClient
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DependingContainerParserSpec extends Specification {

    final String dependenciesString = '''
:serviceDependencies
Running Container   ------>[]
Depending Container ------>[eureka-server, course-service, dementity]

BUILD SUCCESSFUL

Total time: 22.87 secs
'''

    DependingContainerParser parser

    def setup() {
        parser = new DependingContainerParser(dependenciesString)
    }

    def "getParsedDependencies"() {
    when:
    List<String> parsedDeps = parser.getParsedDependencies()

    then:
    parsedDeps == ["eureka-server", "course-service", "dementity"]
  }
}
