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

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class TestPathInfo constructor(val fullPath: String, val getExt: String, val getFilePath: String, val getFilePathNoExt: String, val getPath: String, val getFileName: String, val getFileNameNoExt: String, val getHasExt: Boolean, val isRelative: Boolean, val isExternal: Boolean, val isURI: Boolean, val isAbsolute: Boolean, val withExt: String, val append: String, val addWithExt: String?, val addAppend: Array<String>) {

    val pathInfo = PathInfo(fullPath);

    /* @formatter:off */
    @Test fun test_ext() { assertEquals(getExt, pathInfo.ext) }
    @Test fun test_filePath() { assertEquals(getFilePath, pathInfo.filePath) }
    @Test fun test_filePathNoExt() { assertEquals(getFilePathNoExt, pathInfo.filePathNoExt) }
    @Test fun test_path() { assertEquals(getPath, pathInfo.path) }
    @Test fun test_fileName() { assertEquals(getFileName, pathInfo.fileName) }
    @Test fun test_fileNameNoExt() { assertEquals(getFileNameNoExt, pathInfo.fileNameNoExt) }
    @Test fun test_hasExt() { assertEquals(getHasExt, pathInfo.hasExt) }
    @Test fun test_isRelative() { assertEquals(isRelative, pathInfo.isRelative) }
    @Test fun test_isExternal() { assertEquals(isExternal, pathInfo.isExternal) }
    @Test fun test_isURI() { assertEquals(isURI, pathInfo.isURI) }
    @Test fun test_isAbsolute() { assertEquals(isAbsolute, pathInfo.isAbsolute) }
    @Test fun test_withExt() { assertEquals(withExt, pathInfo.withExt(addWithExt).toString()) }
    @Test fun test_append() { assertEquals(append, pathInfo.append(*addAppend).toString()) }
    /* @formatter:on */

    companion object {
        @Parameterized.Parameters(name = "{index}: filePath = {0}")
        @JvmStatic
        public fun data(): Collection<Array<Any?>> {
            return arrayListOf(
            /* 00 */  pathInfoTestData(".", null),
            /* 01 */  pathInfoTestData(".ext", null),

            /* 02 */  pathInfoTestData("fileName", null),
            /* 03 */  pathInfoTestData("fileName.", null),
            /* 04 */  pathInfoTestData("fileName.ext", null),

            /* 05 */  pathInfoTestData("./fileName", null),
            /* 06 */  pathInfoTestData("./SubDir1/fileName", null),
            /* 07 */  pathInfoTestData("./SubDir1.sub/fileName", null),
            /* 08 */  pathInfoTestData("./SubDir1/SubDir2/fileName", null),
            /* 09 */  pathInfoTestData("././SubDir1/fileName", null),

            /* 10 */  pathInfoTestData("./fileName.", null),
            /* 11 */  pathInfoTestData("./SubDir1/fileName.", null),
            /* 12 */  pathInfoTestData("./SubDir1.sub/fileName.", null),
            /* 13 */  pathInfoTestData("./SubDir1/SubDir2/fileName.", null),
            /* 14 */  pathInfoTestData("././SubDir1/fileName.", null),

            /* 15 */  pathInfoTestData("./fileName.ext", null),
            /* 16 */  pathInfoTestData("./SubDir1/fileName.ext", null),
            /* 17 */  pathInfoTestData("./SubDir1.sub/fileName.ext", null),
            /* 18 */  pathInfoTestData("./SubDir1/SubDir2/fileName.ext", null),
            /* 19 */  pathInfoTestData("././SubDir1/fileName.ext", null),

            /* 20 */  pathInfoTestData("/fileName", null),
            /* 21 */  pathInfoTestData("/SubDir1/fileName", null),
            /* 22 */  pathInfoTestData("/SubDir1.sub/fileName", null),
            /* 23 */  pathInfoTestData("/SubDir1/SubDir2/fileName", null),
            /* 24 */  pathInfoTestData("/./SubDir1/fileName", null),

            /* 25 */  pathInfoTestData("/fileName.", null),
            /* 26 */  pathInfoTestData("/SubDir1/fileName.", null),
            /* 27 */  pathInfoTestData("/SubDir1.sub/fileName.", null),
            /* 28 */  pathInfoTestData("/SubDir1/SubDir2/fileName.", null),
            /* 29 */  pathInfoTestData("/./SubDir1/fileName.", null),

            /* 30 */  pathInfoTestData("/fileName.ext", null),
            /* 31 */  pathInfoTestData("/SubDir1/fileName.ext", null),
            /* 32 */  pathInfoTestData("/SubDir1.sub/fileName.ext", null),
            /* 33 */  pathInfoTestData("/SubDir1/SubDir2/fileName.ext", null),
            /* 34 */  pathInfoTestData("/./SubDir1/fileName.ext", null),

            /* 35 */  pathInfoTestData("http://fileName", null),
            /* 36 */  pathInfoTestData("https://fileName", null),
            /* 37 */  pathInfoTestData("ftp://fileName", null),
            /* 38 */  pathInfoTestData("file://fileName", null),
            /* 39 */  pathInfoTestData("mailto:test@test.com", null),

            /* 40 */  pathInfoTestData(".", ""),
            /* 41 */  pathInfoTestData(".ext", ""),
            /* 42 */  pathInfoTestData("fileName", ""),
            /* 43 */  pathInfoTestData("fileName.", ""),
            /* 44 */  pathInfoTestData("fileName.ext", ""),

            /* 45 */  pathInfoTestData(".", "."),
            /* 46 */  pathInfoTestData(".ext", "."),
            /* 47 */  pathInfoTestData("fileName", "."),
            /* 48 */  pathInfoTestData("fileName.", "."),
            /* 49 */  pathInfoTestData("fileName.ext", "."),

            /* 50 */  pathInfoTestData(".", ".ext"),
            /* 51 */  pathInfoTestData(".ext", ".ext"),
            /* 52 */  pathInfoTestData("fileName", ".ext"),
            /* 53 */  pathInfoTestData("fileName.", ".ext"),
            /* 54 */  pathInfoTestData("fileName.ext", ".ext"),

            /* 55 */  pathInfoTestData(".", ".ext2"),
            /* 56 */  pathInfoTestData(".ext", ".ext2"),
            /* 57 */  pathInfoTestData("fileName", ".ext2"),
            /* 58 */  pathInfoTestData("fileName.", ".ext2"),
            /* 59 */  pathInfoTestData("fileName.ext", ".ext2")

            )
        }

        // ext
        // filePath
        // filePathNoExt
        // path
        // fileName
        // fileNameNoExt
        // hasExt
        // isRelative
        // isExternal
        // isURI
        // isAbsolute
        // withExt
        // append
        fun pathInfoTestData(path: String, withExt: String?, vararg append: String): Array<Any?> {
            val pathInfo: FilePathInfo = FilePathInfo(FilePathInfo.removeEnd(path, "."))

            return arrayOf<Any?>(
                    pathInfo.fullFilePath,
                    pathInfo.ext,
                    pathInfo.filePath,
                    pathInfo.filePathNoExt,
                    pathInfo.path,
                    pathInfo.fileName,
                    pathInfo.fileNameNoExt,
                    pathInfo.hasExt(),
                    pathInfo.isRelative,
                    pathInfo.isExternalReference,
                    pathInfo.isURI,
                    pathInfo.isAbsoluteReference,
                    if (pathInfo.fullFilePath.isEmpty()) "" else pathInfo.withExt(withExt).fullFilePath,
                    pathInfo.append(*append).fullFilePath
                    , withExt
                    , append
            );
        }
    }
}
