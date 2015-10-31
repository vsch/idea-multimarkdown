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
package com.vladsch.idea.multimarkdown.license;

public class JsonStringer {
    protected StringBuilder json = new StringBuilder(100);
    protected boolean isEmpty = true;

    public JsonStringer() {
        json.append('{');
    }

    public void add(String name, String value) {
        if (!isEmpty) {
            json.append(',');
        } else {
            isEmpty = false;
        }
        json.append("\"").append(name).append("\":\"").append(value.replace("\"", "\\\"").replace("\r","").replace("\n", "\\n")).append("\"");
    }

    public String toString() {
        return json.toString() + "}";
    }
}
