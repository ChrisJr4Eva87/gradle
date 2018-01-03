/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.play.integtest.fixtures

import org.gradle.integtests.fixtures.executer.GradleHandle
import org.gradle.util.VersionNumber

abstract class PlayMultiVersionRunApplicationIntegrationTest extends PlayMultiVersionApplicationIntegrationTest {
    RunningPlayApp runningApp
    GradleHandle build

    def setup() {
        runningApp = new RunningPlayApp(testDirectory)
    }

    def startBuild(tasks) {
        build = executer.withTasks(tasks).withForceInteractive(true).withStdinPipe().noDeprecationChecks().start()
        runningApp.initialize(build)
    }

    def patchForPlay26() {
        if (versionNumber >= VersionNumber.parse('2.6.0')) {
            buildFile << """ 
dependencies {
    play "com.typesafe.play:play-guice_2.12:${version.toString()}"
}
"""
            String routes = file('conf/routes').text
            // method at in object Assets is deprecated (since 2.6.0): Inject Assets and use Assets#at
            // https://www.playframework.com/documentation/2.4.x/ScalaRouting#Dependency-Injection
            file('conf/routes').write(routes.replace('controllers.Assets', '@controllers.Assets'))
        }
    }
}
