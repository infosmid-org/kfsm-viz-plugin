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

class KFSMVizPluginExtension {
    KFSMVizPluginExtension(String fsmClassName) {
        this.fsmClassName = fsmClassName
    }
    File input = null
    File outputFolder = null
    String fsmClassName = null
    String output = null
    String plantUmlName = null
    String asciidocName = null
    Boolean isGeneratePlantUml = false
    Boolean isGeneratePlantUmlSimple = null
    Boolean isGenerateAsciidoc = false

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        KFSMVizPluginExtension that = (KFSMVizPluginExtension) o

        if (fsmClassName != that.fsmClassName) return false
        if (input != that.input) return false

        return true
    }

    int hashCode() {
        int result
        result = (input != null ? input.hashCode() : 0)
        result = 31 * result + (fsmClassName != null ? fsmClassName.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KFSMVizPluginExtension.class.getSimpleName() + "[", "]")
                .add("input=" + input)
                .add("fsmClassName='" + fsmClassName + "'")
                .add("outputFolder=" + outputFolder)
                .add("output='" + output + "'")
                .add("plantUmlName='" + plantUmlName + "'")
                .add("asciidocName='" + asciidocName + "'")
                .add("isGeneratePlantUml=" + isGeneratePlantUml)
                .add("isGeneratePlantUmlSimple=" + isGeneratePlantUmlSimple)
                .add("isGenerateAsciidoc=" + isGenerateAsciidoc)
                .toString();
    }
}
