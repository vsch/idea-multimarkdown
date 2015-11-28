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
package com.vladsch.idea.multimarkdown


object Test {
    val isNullable: String? = null
    val isNotNullable = ""
    val test1 = with<String?, String?>(isNullable) { if (this.isNullOrEmpty()) isNotNullable else this }
    val test2 = with<String?, String>(isNullable) { if (this == null || this.isEmpty()) isNotNullable else this }
}
