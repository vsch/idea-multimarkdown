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
package com.vladsch.idea.multimarkdown.util

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import java.util.*

abstract class LinkResolver(val projectResolver: LinkResolver.ProjectResolver, val containingFile: FileRef, val branchOrTag: String?) {
    val projectBasePath = projectResolver.projectBasePath
    val project = projectResolver.project

    interface ProjectResolver {
        val projectBasePath: String
        val project: Project?

        fun isUnderVcs(fileRef: FileRef): Boolean
        fun getVcsRoot(fileRef: FileRef): GitHubVcsRoot?;
        fun vcsRepoBasePath(fileRef: FileRef): String?
        fun vcsRootBase(fileRef: FileRef): String?
        fun projectFileList(fileTypes: List<String>?): List<FileRef>?
    }

    companion object {

        @JvmField val ANY = 0                                               // local, remote or URL
        @JvmField val PREFER_LOCAL = 1                                      // file ref that has local resolve, file references
        @JvmField val ONLY_REMOTE = 2                                       // file ref that has remote resolve, file references
        @JvmField val ONLY_URI = 4                                          // if no local or remote specified then both but as URL's only. URL for file on repo website, URI for local files
        @JvmField val ACCEPT_URI = 8                                        // if no local or remote specified then both but as URL's only. URL for file on repo website, URI for local files
        @JvmField val LOOSE_MATCH = 16                                      // inexact match for error detection
        @JvmField val ONLY_REMOTE_URI = 32                                  // remote ref whether file ref has remote resolve or not
        @JvmField val COMPLETION_MATCH = 64                                 // inexact match for error detection
        @JvmField val LOCAL_OR_REMOTE = PREFER_LOCAL or ONLY_REMOTE         // local or remote resolved files
        @JvmField val ONLY_LOCAL_URI = PREFER_LOCAL or ONLY_URI             // local or remote resolved files


        private val ALL = LOCAL_OR_REMOTE or ONLY_URI             // local, remote or URI, no conversion will be done, refs returned as they are resolved
        private val MATCH_MASK = LOOSE_MATCH or COMPLETION_MATCH
        internal val LINK_REF_WAS_URI = 0x8000000                          // original linkref was external, all resolution is done via relative links


        fun wantAny(options: Int): Boolean = (options and ALL == ANY) || (options and ALL == ALL)
        fun wantLocal(options: Int): Boolean = (options and LOCAL_OR_REMOTE == 0) || (options and PREFER_LOCAL != 0)
        fun wantRemote(options: Int): Boolean = (options and LOCAL_OR_REMOTE == 0) || (options and (ONLY_REMOTE or ONLY_REMOTE_URI) != 0)
        fun wantURI(options: Int): Boolean = (options and ALL == ANY) || (options and (ACCEPT_URI or ONLY_URI or ONLY_REMOTE_URI) != 0)
        fun wantOnlyURI(options: Int): Boolean = (options and (ONLY_URI or ONLY_REMOTE_URI) != 0)
        fun wantOnlyRemoteURI(options: Int): Boolean = (options and ONLY_REMOTE_URI != 0)
        fun wantLooseMatch(options: Int): Boolean = (options and MATCH_MASK != 0)
        fun wantCompletionMatch(options: Int): Boolean = (options and COMPLETION_MATCH != 0)
        fun wantSome(options: Int, flags: Int): Boolean = (options and flags != 0)
        fun wantAll(options: Int, flags: Int): Boolean = (options and flags == flags)
        fun linkRefWasURI(options: Int): Boolean = (options and LINK_REF_WAS_URI != 0)
    }

    abstract fun inspect(linkRef: LinkRef, targetRef: FileRef, referenceId: Any? = null): List<InspectionResult>
    abstract fun isResolved(linkRef: LinkRef, options: Int = 0, inList: List<PathInfo>? = null): Boolean
    abstract fun isResolvedTo(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean? = null, branchOrTag: String? = null): Boolean
    abstract fun linkAddress(linkRef: LinkRef, targetRef: PathInfo, withExtForWikiPage: Boolean? = null, branchOrTag: String? = null, anchor: String? = null): String
    abstract fun multiResolve(linkRef: LinkRef, options: Int = 0, inList: List<PathInfo>? = null): List<PathInfo>
    abstract fun relativePath(linkRef: LinkRef, targetRef: FileRef, withExtForWikiPage: Boolean, branchOrTag: String? = null): String
    abstract fun resolve(linkRef: LinkRef, options: Int = 0, inList: List<PathInfo>? = null): PathInfo?
}

