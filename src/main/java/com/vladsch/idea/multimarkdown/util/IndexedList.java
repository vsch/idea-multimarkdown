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

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

// fast filtering of elements based on elemKey
// main use to get files whose names match
// this is an ordered list with hash map access by key
public abstract class IndexedList<K, T> {
    final protected ArrayList<T> list;
    final protected Map<K, ArrayList<Integer>> map;

    public IndexedList() {
        this.list = new ArrayList<T>();
        this.map = new HashMap<K, ArrayList<Integer>>();
    }

    public IndexedList(Collection<? extends T> list) {
        this.list = new ArrayList<T>(list.size());
        this.map = new HashMap<K, ArrayList<Integer>>();
        addAll(list);
    }

    public IndexedList(T... list) {
        this.list = new ArrayList<T>(list.length);
        this.map = new HashMap<K, ArrayList<Integer>>();
        addAll(list);
    }

    @NotNull
    abstract public K elemKey(@NotNull T item);

    protected void addAll(Collection<? extends T> list) {
        int index = this.list.size();
        for (T item : list) {
            add(index++, item);
        }

        this.list.addAll(list);
    }

    protected void addAll(T... list) {
        int index = this.list.size();
        for (T item : list) {
            add(index++, item);
            this.list.add(item);
        }
    }

    protected void addAll(@NotNull K key, @NotNull List<T> list, @NotNull List<Integer> indices) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<Integer>(2));
        }

        ArrayList<Integer> indexList = map.get(key);

        int iMax = indices.size();
        int index = this.list.size();
        for (int i = 0; i < iMax; i++) {
            indexList.add(i + index);
            this.list.add(list.get(indices.get(i)));
        }
    }

    protected void add(int index, T item) {
        K key = elemKey(item);
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<Integer>(2));
        }

        map.get(key).add(index);
    }

    public void addTo(@NotNull IndexedList<K, T> indexedList, K... keys) {
        for (K key : keys) {
            if (map.containsKey(key)) {
                indexedList.addAll(key, this.list, map.get(key));
            }
        }
    }

    public void addTo(@NotNull IndexedList<K, T> indexedList, Collection<? extends K> keys) {
        for (K key : keys) {
            if (map.containsKey(key)) {
                indexedList.addAll(key, this.list, map.get(key));
            }
        }
    }

    public List<T> sorted(Comparator<? super T> c) {
        ArrayList<T> newList = new ArrayList<T>(list.size());
        newList.addAll(list);
        newList.sort(c);
        return newList;
    }

    // delegate access to list
    public boolean isEmpty() {return list.isEmpty();}
    public int size() {return list.size();}
    public Object[] toArray() {return list.toArray();}
    public <T1> T1[] toArray(T1[] a) {return list.toArray(a);}
    public T get(int index) {return list.get(index);}
    public void forEach(Consumer<? super T> action) {list.forEach(action);}

    // delegate access to map
    public boolean containsKey(K key) {return map.containsKey(key);}
    public Set<K> keySet() {return map.keySet();}
    //public void forEach(BiConsumer<? super K, ? super ArrayList<Integer>> action) {map.forEach(action);}
}
