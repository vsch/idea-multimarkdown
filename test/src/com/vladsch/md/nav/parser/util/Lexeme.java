/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.vladsch.md.nav.parser.util;

import com.intellij.psi.tree.IElementType;

public class Lexeme {
    final protected IElementType myType;
    final protected int myStart;
    final protected int myEnd;

    public Lexeme(IElementType type, int start, int end) {
        myType = type;
        myStart = start;
        myEnd = end;
    }

    public IElementType getType() {
        return myType;
    }

    public int getStart() {
        return myStart;
    }

    public int getEnd() {
        return myEnd;
    }

    @Override
    public String toString() {
        return myType.toString() + "[" + myStart + ", " + myEnd + "]";
    }
}
