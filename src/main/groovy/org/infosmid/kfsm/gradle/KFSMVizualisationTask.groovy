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


import org.infosmid.kfsm.viz.Parser
import org.infosmid.kfsm.viz.Visualization
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
class KFSMVizualisationTask extends DefaultTask {
    @OutputFiles
    Set<File> outputFiles = new HashSet<>()
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    Set<File> inputFiles = new HashSet<>()

    @Input
    List<KFSMVizGenParam> fsmParams = []

    @TaskAction
    def generateVisualization() {
        fsmParams.forEach {
            project.logger.lifecycle("${this.name}:reading ${it.className} from ${it.input}")
            assert it.input.exists()
            def fsmContent = Parser.parseStateMachine(it.className, it.input)
            if (it.outputPlantUml != null) {
                project.logger.lifecycle("${this.name}:creating ${it.outputPlantUml}")
                if (!it.outputPlantUml.parentFile.exists()) {
                    it.outputPlantUml.parentFile.mkdirs()
                }
                it.outputPlantUml.text = Visualization.plantUml(fsmContent, true)
            }
            if (it.outputPlantUmlSimple != null) {
                project.logger.lifecycle("${this.name}:creating ${it.outputPlantUmlSimple}")
                if (!it.outputPlantUmlSimple.parentFile.exists()) {
                    it.outputPlantUmlSimple.parentFile.mkdirs()
                }
                it.outputPlantUmlSimple.text = Visualization.plantUml(fsmContent, false)
            }
            if (it.outputAsciiDoc != null) {
                project.logger.lifecycle("${this.name}:creating ${it.outputAsciiDoc}")
                if (!it.outputAsciiDoc.parentFile.exists()) {
                    it.outputAsciiDoc.parentFile.mkdirs()
                }
                it.outputAsciiDoc.text = Visualization.asciiDoc(fsmContent)
            }
        }
    }

    def configureTask(VizPluginExtension kfsmViz) {
        kfsmViz.getFsms().forEach {
            project.logger.debug("${this.name}:$it")
            String className = it.fsmClassName
            if (!className) throw new RuntimeException("fsmClassName required")
            File inputFile = it.input
            if (!inputFile) throw new RuntimeException("input required")
            def taskParam = new KFSMVizGenParam(className, inputFile)

            if (it.isGenerateAsciidoc || it.asciidocName != null) {
                def fileName = it.asciidocName ?: it.output ?: "${it.fsmClassName}.adoc"
                taskParam.outputAsciiDoc = new File(
                        it.outputFolder ?: project.file("${project.buildDir}/generated"),
                        fileName.contains(".") ? fileName : fileName + ".adoc"
                )
            }
            if (it.isGeneratePlantUml || it.plantUmlName != null) {
                def fileName = it.plantUmlName ?: it.output ?: "${it.fsmClassName}.plantuml"
                taskParam.outputPlantUml = new File(
                        it.outputFolder ?: project.file("${project.buildDir}/generated"),
                        fileName.contains(".") ? fileName : fileName + ".plantuml"
                )
            }
            if (it.isGeneratePlantUmlSimple || (it.isGeneratePlantUml && it.isGeneratePlantUmlSimple == null) || it.plantUmlName != null) {
                String fileName = it.plantUmlName ?: "${it.output}-simple" ?: "${it.fsmClassName}-simple"
                taskParam.outputPlantUmlSimple = new File(
                        it.outputFolder ?: project.file("${project.buildDir}/generated"),
                        fileName.contains(".") ? fileName : fileName + ".plantuml"
                )
            }
            fsmParams.add(taskParam)
            inputFiles.add(taskParam.input)
            if (taskParam.outputAsciiDoc != null) {
                outputFiles.add(taskParam.outputAsciiDoc)
            }
            if (taskParam.outputPlantUml != null) {
                outputFiles.add(taskParam.outputPlantUml)
            }
            if (taskParam.outputPlantUmlSimple != null) {
                outputFiles.add(taskParam.outputPlantUmlSimple)
            }
            project.logger.debug("inputFiles:{}", inputFiles)
            project.logger.debug("outputFiles:{}", outputFiles)
        }
    }
}
