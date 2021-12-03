package com.github.mwalter.mavendependencychecker.services

import com.intellij.openapi.project.Project
import com.github.mwalter.mavendependencychecker.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
