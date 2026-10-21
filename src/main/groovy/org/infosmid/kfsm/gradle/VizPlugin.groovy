/*
 * Copyright (c) 2020-2021. Open JumpCO
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.infosmid.kfsm.gradle


import org.gradle.api.Plugin
import org.gradle.api.Project

class VizPlugin implements Plugin<Project> {

    public final static KFSM_VIZ = "kfsmViz"
    public final static FSM_CONFIG = "fsm"

    void apply(Project project) {
        def kfsmViz = project.extensions.create(KFSM_VIZ, VizPluginExtension)
        def vizTask = project.tasks.register('generateFsmViz', KFSMVizualisationTask) { task ->
            group = 'documentation'
        }
        // inputs and outputs must be declared before execution for up-to-date checks
        project.afterEvaluate {
            vizTask.configure { task ->
                task.configureTask(kfsmViz)
            }
        }
    }

}
