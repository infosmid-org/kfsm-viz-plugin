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

import org.gradle.api.Action

class VizPluginExtension {

    List<KFSMVizGenParam> fsms = []

    def List<KFSMVizPluginExtension> getFsms()  {
        return fsms
    }


    def fsm(String className, Closure closure) {
        def fsm = new KFSMVizPluginExtension(className)
        fsms += fsm
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = fsm
        closure.call(fsm)
        return fsm
    }

    public void fsm(String className, Action<KFSMVizPluginExtension> action) {
        def fsm = new KFSMVizPluginExtension(className)
        fsms += fsm
        action.execute(fsm)
    }
}
