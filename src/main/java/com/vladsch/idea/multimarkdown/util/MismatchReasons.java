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
package com.vladsch.idea.multimarkdown.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class MismatchReasons {
    protected final HashMap<String, MismatchReason> reasons = new HashMap<String, MismatchReason>(10);

    public boolean hasErrors() {
        return hasSeverity(MismatchReason.Severity.ERROR);
    }

    public boolean hasWarnings() {
        return hasSeverity(MismatchReason.Severity.WARNING);
    }

    public boolean hasWeakWarnings() {
        return hasSeverity(MismatchReason.Severity.WEAK_WARNING);
    }

    public boolean hasInfo() {
        return hasSeverity(MismatchReason.Severity.INFO);
    }

    public boolean hasSeverity(MismatchReason.Severity severity) {
        for (MismatchReason reason : reasons.values()) {
            if (reason.severity == severity) return true;
        }
        return false;
    }

    public boolean has(String id) {
        return reasons.containsKey(id);
    }

    public MismatchReason get(String id) {
        return reasons.containsKey(id) ? reasons.get(id) : null;
    }

    public int size() {return reasons.size();}
    public MismatchReason put(String key, MismatchReason value) {return reasons.put(key, value);}
    public MismatchReason remove(String key) {return reasons.remove(key);}
    public Set<String> keySet() {return reasons.keySet();}
    public boolean containsValue(MismatchReason value) {return reasons.containsValue(value);}
    public Collection<MismatchReason> values() {return reasons.values();}
    public void clear() {reasons.clear();}
    public boolean containsKey(String key) {return reasons.containsKey(key);}
    public boolean isEmpty() {return reasons.isEmpty();}
    public boolean remove(String key, MismatchReason value) {return reasons.remove(key, value);}
    public MismatchReason putIfAbsent(String key, MismatchReason value) {return reasons.putIfAbsent(key, value);}
}
