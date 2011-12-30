/*
 * Copyright (c) 2011 Julien Nicoulaud <julien.nicoulaud@gmail.com>
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
 */
package net.nicoulaj.idea.markdown.editor;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.AsyncResult;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.vfs.ex.http.HttpFileSystem;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;

import java.net.URL;

/**
 * <p>MarkdownPathResolver</p>
 *
 * @author Roger Grantham
 */
public class MarkdownPathResolver {

	/**
	 * Not to be instantiated
	 */
	private MarkdownPathResolver() {
		// no op
	}

	/**
	 * Makes a simple attempt to convert the URL into a VirtualFile
	 * @param target url from which a VirtualFile is sought
	 * @return VirtualFile or null
	 */
	public static VirtualFile findVirtualFile(URL target){
		VirtualFileSystem vfs = VirtualFileManager.getInstance().getFileSystem(target.getProtocol());
		final AsyncResult<DataContext> dataContext = DataManager.getInstance().getDataContextFromFocus();
		final Project project = DataKeys.PROJECT.getData(dataContext.getResult());
		return  vfs.findFileByPath(target.getFile());
	}

	/**
	 * Interprets <var>target</var> as a path relative to the currently active editor
	 * @param target relative path from which a VirtualFile is sought
	 * @return VirtualFile or null
	 */
	public static VirtualFile resolveRelativePath(String target){
		final AsyncResult<DataContext> dataContextResult = DataManager.getInstance().getDataContextFromFocus();
		final DataContext dataContext = dataContextResult.getResult();
		Project project;
		if (dataContext == null){
			final Project[] opened = ProjectManager.getInstance().getOpenProjects();
			if (opened.length == 0){
				return null;
			} else {
				project = opened[0];
			}
		} else {
			project = DataKeys.PROJECT.getData(dataContext);
		}
		VirtualFile virtualTarget;
		// treat as relative
		final VirtualFile[] selected = FileEditorManager.getInstance(project).getSelectedFiles();
		if (selected.length == 0){
			// no such file, but not sure how this could happen
			return null;
		}
		virtualTarget = target.matches("^[.][.]")
						? selected[0].findFileByRelativePath(target)
						: selected[0].getParent().findFileByRelativePath(target); //if a sibling or lower in tree, query from parent
		return virtualTarget;
	}


	/**
	 * Interprets <var>target</var> as a class reference
	 * @param target from which a VirtualFile is sought
	 * @return VirtualFile or null
	 */
	public static VirtualFile resolveClassReference(String target){
		final AsyncResult<DataContext> dataContext = DataManager.getInstance().getDataContextFromFocus();
		final Project project = DataKeys.PROJECT.getData(dataContext.getResult());
		if (project == null){
			return null;
		}
		final PsiClass classpathResource = JavaPsiFacade.getInstance(project).findClass(target, GlobalSearchScope.projectScope(project));
		VirtualFile virtualTarget = null;
		if (classpathResource != null) {
			virtualTarget = classpathResource.getContainingFile().getVirtualFile();
		}
		return virtualTarget;
	}

}
