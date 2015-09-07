/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * This file is based on the IntelliJ SimplePlugin tutorial
 *
 */
package com.vladsch.idea.multimarkdown;

import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MultiMarkdownPlugin implements ProjectComponent {

    private Project project;
    private PluginClassLoader myClassLoader;

    public MultiMarkdownPlugin(Project project) {
        this.project = project;
        myClassLoader = null;
    }

    public PluginClassLoader getClassLoader() {
        if (myClassLoader == null) {
            myClassLoader = (PluginClassLoader)getClass().getClassLoader();
            String javaHome = System.getProperties().getProperty("java.home");
            String javaVersion = System.getProperties().getProperty("java.version");
            ArrayList<String> libs = new ArrayList<String>(1);
            libs.add(javaHome + "/lib/ext");
            myClassLoader.addLibDirectories(libs);
        }
        return myClassLoader;
    }

    public void projectOpened() {
    }

    public Project getProject() {
        return project;
    }

    public void projectClosed() {

    }

    public void initComponent() {
        // empty
    }

    public void disposeComponent() {
        // empty
    }

    @NotNull
    public String getComponentName() {
        return this.getClass().getName();
    }

    public static MultiMarkdownPlugin getInstance(Project project) {
        return project.getComponent(MultiMarkdownPlugin.class);
    }
}
