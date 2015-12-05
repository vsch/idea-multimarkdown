/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.util

object Want : TestDataAware {
    enum class FileType(val flags: Int) {
        NONE(0),
        REF(1),
        URI(2),
        URL(3);

        internal val unboxed: Int get() = FileType.unboxed(flags)

        companion object : EnumBitField<FileType>(2) {
            override fun boxed(flags: Int): FileType {
                val masked = flags(flags)
                return when (masked) {
                    REF.flags -> REF
                    URI.flags -> URI
                    URL.flags -> URL
                    else -> NONE
                }
            }
        }
    }

    enum class LocalType(val type: FileType) {
        NONE(FileType.NONE),
        REF(FileType.REF),
        URI(FileType.URI),
        URL(FileType.URL);

        internal val unboxed: Int get() = unboxed(type.unboxed)
        internal val flags: Int get() = type.flags

        companion object : EnumBitField<FileType>(FileType.bits) {
            override fun boxed(flags: Int): FileType {
                return FileType.boxed(flags(flags))
            }
        }
    }

    enum class RemoteType(val type: FileType) {
        NONE(FileType.NONE),
        REF(FileType.REF),
        URI(FileType.URI),
        URL(FileType.URL);

        internal val unboxed: Int get() = unboxed(type.unboxed)
        internal val flags: Int get() = type.flags

        companion object : EnumBitField<FileType>(FileType.bits, LocalType) {
            override fun boxed(flags: Int): FileType {
                return FileType.boxed(flags(flags))
            }
        }
    }

    enum class MatchType(val flags: Int) {
        EXACT(0),
        LOOSE(1),
        COMPLETION(2);

        internal val unboxed: Int get() = unboxed(flags)

        companion object : EnumBitField<MatchType>(2, RemoteType) {
            override fun boxed(flags: Int): MatchType {
                val masked = flags(flags)
                return when (masked) {
                    LOOSE.flags -> LOOSE
                    COMPLETION.flags -> COMPLETION
                    else -> EXACT
                }
            }
        }
    }

    enum class LinkType(val flags: Int) {
        NONE(0),
        URL(1);

        internal val unboxed: Int get() = unboxed(flags)

        companion object : EnumBitField<LinkType>(1, MatchType) {
            override fun boxed(flags: Int): LinkType {
                val masked = flags(flags)
                return when (masked) {
                    URL.flags -> URL
                    else -> NONE
                }
            }
        }
    }

    sealed class Options : TestDataAware {
        abstract class Remotes : Options() {
            abstract val unboxed: RemoteType
        }

        abstract class Locals : Options() {
            abstract val unboxed: LocalType
        }

        abstract class Matches : Options() {
            abstract val unboxed: MatchType
        }

        abstract class Links : Options() {
            abstract val unboxed: LinkType
        }

        override fun testData(): String {
            return when (this) {
                is Remotes -> "Remote." + unboxed.type
                is Locals -> "Local."  + unboxed.type
                is Matches -> "Match." + unboxed
                is Links -> "Links." + unboxed
            }
        }

        override fun className(inParent: java.lang.Class<Any>?): String {
            return when (this) {
                is Remotes -> "Remote"
                is Locals -> "Local"
                is Matches -> "Match"
                is Links -> "Links"
            }
        }
    }

    // @formatter:off
    object RemoteNone : Options.Remotes() { override val unboxed:RemoteType get() = RemoteType.NONE }
    object RemoteRef : Options.Remotes() { override val unboxed:RemoteType get() = RemoteType.REF }
    object RemoteUri : Options.Remotes() { override val unboxed:RemoteType get() = RemoteType.URI }
    object RemoteUrl : Options.Remotes() { override val unboxed:RemoteType get() = RemoteType.URL }

    object LocalNone : Options.Locals() { override val unboxed:LocalType get() = LocalType.NONE }
    object LocalRef : Options.Locals() { override val unboxed:LocalType get() = LocalType.REF }
    object LocalUri : Options.Locals() { override val unboxed:LocalType get() = LocalType.URI }
    object LocalUrl : Options.Locals() { override val unboxed:LocalType get() = LocalType.URL }

    object MatchExact : Options.Matches() { override val unboxed:MatchType get() = MatchType.EXACT }
    object MatchLoose : Options.Matches() { override val unboxed:MatchType get() = MatchType.LOOSE }
    object MatchCompletion : Options.Matches() { override val unboxed:MatchType get() = MatchType.COMPLETION }

    object LinksNone : Options.Links() { override val unboxed:LinkType get() = LinkType.NONE }
    object LinksUrl : Options.Links() { override val unboxed:LinkType get() = LinkType.URL }
    // @formatter:on

    fun assertNull(option: Options?) {
        assert(option == null, { "option ${(option as Options).className()} is already set to ${option.testData()}" })
    }

    fun unboxed(vararg options: Options): Int {
        var remote: Options.Remotes? = null
        var local: Options.Locals? = null
        var match: Options.Matches? = null
        var links: Options.Links? = null
        for (option in options) {
            when (option) {
                is Options.Remotes -> {
                    assertNull(remote); remote = option
                }
                is Options.Locals -> {
                    assertNull(local); local = option
                }
                is Options.Matches -> {
                    assertNull(match); match = option
                }
                is Options.Links -> {
                    assertNull(links); links = option
                }
            }
        }

        var flags: Int = 0

        flags = flags or (local?.unboxed ?: LocalRef.unboxed).unboxed
        flags = flags or (remote?.unboxed ?: RemoteRef.unboxed).unboxed
        flags = flags or (match?.unboxed ?: MatchExact.unboxed).unboxed
        flags = flags or (links?.unboxed ?: LinksUrl.unboxed).unboxed

        return flags
    }

    operator fun invoke(vararg options: Options) = unboxed(*options)

    fun localType(options: Int): Options.Locals {
        return Local.boxed(LocalType.boxed(options))
    }

    fun remoteType(options: Int): Options.Remotes {
        return Remote.boxed(RemoteType.boxed(options))
    }

    fun matchType(options: Int): Options.Matches {
        return Match.boxed(MatchType.boxed(options))
    }

    fun linksType(options: Int): Options.Links {
        return Links.boxed(LinkType.boxed(options))
    }

    fun looseMatch(options: Int): Boolean = MatchType.unboxedFlags(options) != MatchType.EXACT.unboxed
    fun completionMatch(options: Int): Boolean = MatchType.unboxedFlags(options) == MatchType.COMPLETION.unboxed
    fun links(options: Int): Boolean = LinkType.unboxedFlags(options) == LinkType.URL.unboxed

    fun local(options: Int): Boolean = LocalType.unboxedFlags(options) != LocalType.NONE.unboxed
    fun localREF(options: Int): Boolean = LocalType.unboxedFlags(options) == LocalType.REF.unboxed
    fun localURI(options: Int): Boolean = LocalType.unboxedFlags(options) == LocalType.URI.unboxed
    fun localURL(options: Int): Boolean = LocalType.unboxedFlags(options) == LocalType.URL.unboxed

    fun remote(options: Int): Boolean = RemoteType.unboxedFlags(options) != RemoteType.NONE.unboxed
    fun remoteREF(options: Int): Boolean = RemoteType.unboxedFlags(options) == RemoteType.REF.unboxed
    fun remoteURI(options: Int): Boolean = RemoteType.unboxedFlags(options) == RemoteType.URI.unboxed
    fun remoteURL(options: Int): Boolean = RemoteType.unboxedFlags(options) == RemoteType.URL.unboxed
}

object Local {
    val NONE: Want.Options.Locals get() = Want.LocalNone
    val REF: Want.Options.Locals get() = Want.LocalRef
    val URI: Want.Options.Locals get() = Want.LocalUri
    val URL: Want.Options.Locals get() = Want.LocalUrl

    fun boxed(fileType: Want.FileType): Want.Options.Locals {
        return when (fileType) {
            Want.FileType.REF -> REF
            Want.FileType.URI -> URI
            Want.FileType.URL -> URL
            else -> NONE
        }
    }
}

object Remote {
    val NONE: Want.Options.Remotes get() = Want.RemoteNone
    val REF: Want.Options.Remotes get() = Want.RemoteRef
    val URI: Want.Options.Remotes get() = Want.RemoteUri
    val URL: Want.Options.Remotes get() = Want.RemoteUrl

    fun boxed(fileType: Want.FileType): Want.Options.Remotes {
        return when (fileType) {
            Want.FileType.REF -> REF
            Want.FileType.URI -> URI
            Want.FileType.URL -> URL
            else -> NONE
        }
    }
}

object Match {
    val EXACT: Want.Options.Matches get() = Want.MatchExact
    val LOOSE: Want.Options.Matches get() = Want.MatchLoose
    val COMPLETION: Want.Options.Matches get() = Want.MatchCompletion

    fun boxed(fileType: Want.MatchType): Want.Options.Matches {
        return when (fileType) {
            Want.MatchType.LOOSE -> LOOSE
            Want.MatchType.COMPLETION -> COMPLETION
            else -> EXACT
        }
    }
}

object Links {
    val NONE: Want.Options.Links get() = Want.LinksNone
    val URL: Want.Options.Links get() = Want.LinksUrl

    fun boxed(fileType: Want.LinkType): Want.Options.Links {
        return when (fileType) {
            Want.LinkType.URL -> URL
            else -> NONE
        }
    }
}

