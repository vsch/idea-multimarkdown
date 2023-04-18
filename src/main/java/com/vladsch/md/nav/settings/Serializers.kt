// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings

import com.vladsch.plugin.util.nullIf
import org.jdom.Element
import org.jdom.Text
import java.util.*

abstract class StateHolderImpl(val factory: () -> Item<StateHolder>) : Item<StateHolder>, StateHolderContainer {
    override fun saveState(element: Element?): Element? {
        return stateHolder.saveState(element)
    }

    override fun loadState(element: Element?): Unit {
        if (element == null) return
        alternateStateHolder?.loadState(element)
        stateHolder.loadState(element)
    }

    override fun createItem(element: Element?): Item<StateHolder> {
        val item = factory()
        item.loadState(element)
        return item
    }

    abstract override fun getStateHolder(): StateHolder
}

open class GenericStringItem<T : Any>(val getter: () -> String?, val setter: (T) -> Unit, val factory: (String?) -> T)

open class TextContentItem<T : Any>(val getter: () -> String?, val setter: (T) -> Unit, val factory: (String?) -> T) : Item<T> {
    constructor(contentItem: GenericStringItem<T>) : this(contentItem.getter, contentItem.setter, contentItem.factory)

    override fun saveState(element: Element?): Element? {
        val value = getter()
        if (value != null) element?.addContent(Text(value))
        return element
    }

    override fun loadState(element: Element?) {
        val value = element?.content
        if (value == null && getter() == null) return
        setter(createItem(element))
    }

    override fun createItem(element: Element?): T {
        val content = element?.content
        return factory(if (content != null && content.size > 0 && content[0] is Text) content[0].value else null)
    }
}

open class AttributeItem<T : Any>(val optionName: String, val getter: () -> String?, val setter: (T) -> Unit, val factory: (String?) -> T) : Item<T> {
    constructor(optionName: String, contentItem: GenericStringItem<T>) : this(optionName, contentItem.getter, contentItem.setter, contentItem.factory)

    override fun saveState(element: Element?): Element? {
        val value = getter()
        if (value != null) element?.setAttribute(optionName, value)
        return element
    }

    override fun loadState(element: Element?) {
        // if it is read only (ie. returns null for value, then don't load it if it is not part of the element
        val value = element?.getAttributeValue(optionName)
        if (value == null && getter() == null) return
        setter(createItem(element))
    }

    override fun createItem(element: Element?): T {
        val value = element?.getAttributeValue(optionName)
        return factory(value)
    }
}

open class TagItemHolder(val optionName: String) : StateHolder {
    val items = ArrayList<Item<*>>()

    fun addItems(vararg items: Item<*>): TagItemHolder {
        this.items.addAll(items)
        return this
    }

    override fun loadState(element: Element?) {
        val myElement = if (this is ComponentItemHolder && element?.name == "component") element else element?.getChild(optionName) ?: return

        for (i in 0 .. items.lastIndex) {
            items[i].loadState(myElement)
        }
    }

    override fun saveState(element: Element?): Element? {
        assert(this !is ComponentItemHolder || element == null) { "ComponentItemHolder should only be used in component level state classes" }
        val myElement = Element(optionName)
        element?.addContent(myElement)

        for (item in items) {
            item.saveState(myElement)
        }

        return element ?: myElement
    }
}

open class ComponentItemHolder : TagItemHolder("component")

open class TagItem<T : StateHolder>(val optionName: String, val getter: () -> T, val setter: (T) -> Unit, val factory: (Element?) -> T) : Item<T> {
    override fun saveState(element: Element?): Element? {
        return if (element == null) {
            getter().saveState(Element(optionName))
        } else {
            element.addContent(getter().saveState(Element(optionName)))
        }
    }

    override fun loadState(element: Element?) {
        val myElement = element?.getChild(optionName)
        // do not load if the tag is not there
        if (element == null) return
        setter(createItem(myElement))
    }

    override fun createItem(element: Element?): T {
        return factory(element)
    }
}

open class GenericStringTagItem<T : Any>(val optionName: String, item: GenericStringItem<T>) : TextContentItem<T>(item) {
    override fun saveState(element: Element?): Element? {
        return if (element == null) {
            super.saveState(Element(optionName))
        } else {
            val saveState = super.saveState(Element(optionName))
            if (saveState?.content?.isEmpty() != true) element.addContent(saveState) else element
        }
    }

    override fun createItem(element: Element?): T {
        val myElement = element?.getChild(optionName)
        return super.createItem(myElement)
    }
}

open class ItemCollectionItem<T : Item<*>>(val optionName: String, val getter: (() -> Collection<T>)?, val setter: (Collection<T>) -> Unit, val itemFactory: (element: Element?) -> T) : Item<Collection<T>> {
    override fun saveState(element: Element?): Element? {
        val items = getter?.invoke()
        if (items != null) {
            val myElement = Element(optionName)
            element?.addContent(myElement)

            for (item in items) {
                assert(item !is AttributeItem<*>) { "AttributeItem cannot be used as an element of an ItemCollection item, only ContentItem and StateHolder are allowed" }
                item.saveState(myElement)
            }

            return element ?: myElement
        } else {
            return element ?: Element(optionName)
        }
    }

    override fun loadState(element: Element?) {
        if (element == null && getter == null) return
        setter(createItem(element))
    }

    override fun createItem(element: Element?): Collection<T> {
        val myElement = element?.getChild(optionName)
        val items = arrayListOf<T>()
        val dummyElement = itemFactory(null)

        if (myElement != null) {
            when (dummyElement) {
                is TextContentItem<*> -> {
                    for (itemContent in myElement.content) {
                        val fakeElement = Element("fake")
                        fakeElement.addContent(itemContent.clone())
                        items.add(itemFactory(fakeElement))
                    }
                }
                else -> {
                    for (itemElement in myElement.children) {
                        val fakeElement = Element("fake")
                        fakeElement.addContent(itemElement.clone())
                        items.add(itemFactory(fakeElement))
                    }
                }
            }
        }
        return items
    }
}

open class GenericStringCollectionItem<T : Any>(val optionName: String, val getter: (() -> Collection<T>)?, val setter: (Collection<T>) -> Unit, val itemGetter: (T) -> String, val itemFactory: (String) -> T?) : Item<Collection<T>> {
    override fun saveState(element: Element?): Element? {
        val items = getter?.invoke()
        if (items != null) {
            val myElement = Element(optionName)
            element?.addContent(myElement)

            for (item in items) {
                val itemElement = Element("option")
                myElement.addContent(itemElement)
                itemElement.setAttribute("name", itemGetter(item))
            }

            return element ?: myElement
        } else {
            return element ?: Element(optionName)
        }
    }

    override fun loadState(element: Element?) {
        // do not load if the tag is not there
        if (element == null) return
        setter(createItem(element))
    }

    override fun createItem(element: Element?): Collection<T> {
        val myElement = element?.getChild(optionName)
        val items = arrayListOf<T>()

        if (myElement != null) {
            for (itemElement in myElement.children) {
                if (itemElement.name == "option") {
                    val value = itemElement.getAttributeValue("name")
                    if (value != null) {
                        val item = itemFactory(value)
                        if (item != null) items.add(item)
                    }
                }
            }
        }
        return items
    }
}

open class ItemMapItem<K : Any, V : Item<*>>(val optionName: String, val getter: (() -> Map<K, V>)?, val setter: (Map<K, V>) -> Unit, val keyGetter: (K) -> String, val itemFactory: (key: String, value: Element?) -> Pair<K, V>) : Item<Map<K, V>> {

    override fun saveState(element: Element?): Element? {
        val map = getter?.invoke()
        if (map != null) {
            val myElement = Element(optionName)
            element?.addContent(myElement)

            for ((key1, value) in map.entries.toList().sortedBy({ keyGetter(it.key) })) {
                val itemElement = Element("option")
                myElement.addContent(itemElement)
                val key = keyGetter(key1)
                itemElement.addContent(value.saveState(Element(key)))
            }
            return element ?: myElement
        } else {
            return element ?: Element(optionName)
        }
    }

    override fun loadState(element: Element?) {
        // do not load if the tag is not there
        if (element == null) return
        setter(createItem(element))
    }

    override fun createItem(element: Element?): Map<K, V> {
        val myElement = element?.getChild(optionName)
        val items = HashMap<K, V>()

        if (myElement != null) {
            for (itemElement in myElement.children) {
                if (itemElement.name == "option" && itemElement.children.size == 1) {
                    val value = itemElement.children[0]
                    val key = value.name
                    val pair = itemFactory(key, value)
                    items[pair.first] = pair.second
                }
            }
        }
        return items
    }
}

open class MapItem<K : Any, V : Any>(val optionName: String, val getter: (() -> Map<K, V>)?, val setter: (Map<K, V>) -> Unit, val itemGetter: (K, V) -> Pair<String, String>, val itemFactory: (String, String?) -> Pair<K, V>?) : Item<Map<K, V>> {
    constructor(optionName: String, readOnly: Boolean, getter: (() -> Map<K, V>)?, setter: (Map<K, V>) -> Unit, itemGetter: (K, V) -> Pair<String, String>, itemFactory: (String, String?) -> Pair<K, V>?) :
        this(optionName, if (readOnly) null else getter, setter, itemGetter, itemFactory)

    override fun saveState(element: Element?): Element? {
        val map = getter?.invoke()
        if (map != null) {
            val myElement = Element(optionName)
            element?.addContent(myElement)

            for ((key, value) in map.entries.toList().sortedBy({ itemGetter(it.key, it.value).first })) {
                val itemElement = Element("option")
                myElement.addContent(itemElement)
                val pair = itemGetter(key, value)
                itemElement.setAttribute("name", pair.first)
                itemElement.setAttribute("value", pair.second)
            }
            return element ?: myElement
        } else {
            return element ?: Element(optionName)
        }
    }

    override fun loadState(element: Element?) {
        // do not load if the tag is not there
        if (element == null) return
        setter(createItem(element))
    }

    override fun createItem(element: Element?): Map<K, V> {
        val myElement = element?.getChild(optionName)
        val items = HashMap<K, V>()

        if (myElement != null) {
            for (itemElement in myElement.children) {
                if (itemElement.name == "option") {
                    val key = itemElement.getAttributeValue("name")
                    if (key != null) {
                        val value = itemElement.getAttributeValue("value")
                        val pair = itemFactory(key, value)
                        if (pair != null) items[pair.first] = pair.second
                    }
                }
            }
        }

        return items
    }
}

open class CollectionItem<T : Item<StateHolder>>(option: String, getter: (() -> Collection<T>)?, setter: (Collection<T>) -> Unit, itemFactory: (Element?) -> T)
    : ItemCollectionItem<T>(option, getter, setter, itemFactory) {

    constructor(option: String, readOnly: Boolean, getter: (() -> Collection<T>)?, setter: (Collection<T>) -> Unit, itemFactory: (Element?) -> T) :
        this(option, if (readOnly) null else getter, setter, itemFactory)
}

open class ItemHolderSetItem<T : Item<StateHolder>>(option: String, getter: (() -> Collection<T>)?, setter: (Set<T>) -> Unit, itemFactory: (Element?) -> T)
    : ItemCollectionItem<T>(option, getter, { setter(it.toSet()) }, itemFactory)

open class ItemHolderHashMapItem<K : Any, V : Item<*>>(option: String, getter: (() -> Map<K, V>)?, setter: (HashMap<K, V>) -> Unit, keyGetter: (K) -> String, itemFactory: (key: String, value: Element?) -> Pair<K, V>)
    : ItemMapItem<K, V>(option, getter, {
    val map = HashMap<K, V>()
    for (entry in it) map[entry.key] = entry.value
    setter(map)
}, keyGetter, itemFactory)

class BooleanItem(getter: (() -> Boolean)?, setter: (Boolean) -> Unit)
    : GenericStringItem<Boolean>({ getter?.invoke()?.toString() }, setter, { it?.toBoolean() ?: getter?.invoke() ?: false })

class IntItem(getter: (() -> Int)?, setter: (Int) -> Unit)
    : GenericStringItem<Int>({ getter?.invoke()?.toString() }, setter, { it?.toInt() ?: getter?.invoke() ?: 0 })

class FloatItem(getter: (() -> Float)?, setter: (Float) -> Unit)
    : GenericStringItem<Float>({ getter?.invoke()?.toString() }, setter, { it?.toFloat() ?: getter?.invoke() ?: 0f })

class DoubleItem(getter: (() -> Double)?, setter: (Double) -> Unit)
    : GenericStringItem<Double>({ getter?.invoke()?.toString() }, setter, { it?.toDouble() ?: getter?.invoke() ?: 0.0 })

class StringItem(getter: (() -> String?)?, setter: (String) -> Unit)
    : GenericStringItem<String>({ getter?.invoke() }, setter, { it ?: getter?.invoke() ?: "" })

class BooleanAttribute(option: String, getter: (() -> Boolean)?, setter: (Boolean) -> Unit)
    : AttributeItem<Boolean>(option, BooleanItem(getter, setter)) {

    constructor(option: String, readOnly: Boolean, getter: (() -> Boolean)?, setter: (Boolean) -> Unit) : this(option, if (readOnly) null else getter, setter)
}

class IntAttribute(option: String, getter: (() -> Int)?, setter: (Int) -> Unit)
    : AttributeItem<Int>(option, IntItem(getter, setter)) {

    constructor(option: String, readOnly: Boolean, getter: (() -> Int)?, setter: (Int) -> Unit) : this(option, if (readOnly) null else getter, setter)
}

class FloatAttribute(option: String, getter: (() -> Float)?, setter: (Float) -> Unit)
    : AttributeItem<Float>(option, FloatItem(getter, setter)) {

    constructor(option: String, readOnly: Boolean, getter: (() -> Float)?, setter: (Float) -> Unit) : this(option, if (readOnly) null else getter, setter)
}

class DoubleAttribute(option: String, getter: (() -> Double)?, setter: (Double) -> Unit)
    : AttributeItem<Double>(option, DoubleItem(getter, setter)) {

    constructor(option: String, readOnly: Boolean, getter: (() -> Double)?, setter: (Double) -> Unit) : this(option, if (readOnly) null else getter, setter)
}

class StringAttribute(option: String, getter: (() -> String)?, setter: (String) -> Unit)
    : AttributeItem<String>(option, StringItem(getter, setter)) {

    constructor(option: String, readOnly: Boolean, getter: (() -> String)?, setter: (String) -> Unit) : this(option, if (readOnly) null else getter, setter)
}

class BooleanContent(option: String, getter: (() -> Boolean)?, setter: (Boolean) -> Unit)
    : GenericStringTagItem<Boolean>(option, BooleanItem(getter, setter))

class IntContent(option: String, getter: (() -> Int)?, setter: (Int) -> Unit)
    : GenericStringTagItem<Int>(option, IntItem(getter, setter))

class FloatContent(option: String, getter: (() -> Float)?, setter: (Float) -> Unit)
    : GenericStringTagItem<Float>(option, FloatItem(getter, setter))

class DoubleContent(option: String, getter: (() -> Double)?, setter: (Double) -> Unit)
    : GenericStringTagItem<Double>(option, DoubleItem(getter, setter))

class StringContent(option: String, getter: (() -> String?)?, setter: (String) -> Unit)
    : GenericStringTagItem<String>(option, StringItem(getter, setter))

open class ArrayListItem<T : Any>(option: String, getter: (() -> Collection<T>)?, setter: (ArrayList<T>) -> Unit, itemGetter: (T) -> String, itemFactory: (String) -> T?)
    : GenericStringCollectionItem<T>(option, getter, { val collection = ArrayList<T>(); collection.addAll(it); setter(collection); }, itemGetter, itemFactory)

open class HashSetItem<T : Any>(option: String, getter: (() -> Collection<T>)?, setter: (HashSet<T>) -> Unit, itemGetter: (T) -> String, itemFactory: (String) -> T?)
    : GenericStringCollectionItem<T>(option, getter, { val collection = HashSet<T>(); collection.addAll(it); setter(collection); }, itemGetter, itemFactory)

open class ItemHashMapItem<K : Any, V : Item<*>>(option: String, getter: (() -> HashMap<K, V>)?, setter: (HashMap<K, V>) -> Unit, keyGetter: (K) -> String, keyFactory: (String) -> K, valueFactory: (Element?) -> V)
    : ItemMapItem<K, V>(option, getter, {
    val collection = HashMap<K, V>()
    for (item in it) collection[item.key] = item.value
    setter(collection)
}, keyGetter, { key, value -> Pair(keyFactory(key), valueFactory(value)) })

open class HashMapItem<K : Any, V : Any>(option: String, getter: (() -> Map<K, V>)?, setter: (HashMap<K, V>) -> Unit, itemGetter: (K, V) -> Pair<String, String>, itemSetter: (key: String, value: String?) -> Pair<K, V>)
    : MapItem<K, V>(option, getter, {
    val collection = HashMap<K, V>(); for (item in it) collection[item.key] = item.value
    setter(collection)
}, itemGetter, itemSetter)

open class ItemArrayListItem<T : Item<T>>(option: String, getter: (() -> Collection<T>)?, setter: (ArrayList<T>) -> Unit, itemFactory: (Element?) -> T)
    : ItemCollectionItem<T>(option, getter, { val collection = ArrayList<T>(); collection.addAll(it); setter(collection); }, itemFactory)

open class ItemHashSetItem<T : Item<*>>(option: String, getter: (() -> Collection<T>)?, setter: (HashSet<T>) -> Unit, itemFactory: (Element?) -> T)
    : ItemCollectionItem<T>(option, getter, { val collection = HashSet<T>(); collection.addAll(it); setter(collection); }, itemFactory)

open class BooleanArrayListItem(option: String, getter: (() -> Collection<Boolean>)?, setter: (ArrayList<Boolean>) -> Unit)
    : ArrayListItem<Boolean>(option, getter, { val collection = ArrayList<Boolean>(); collection.addAll(it); setter(collection); }, { it.toString() }, String::toBoolean)

open class IntArrayListItem(option: String, getter: (() -> Collection<Int>)?, setter: (ArrayList<Int>) -> Unit)
    : ArrayListItem<Int>(option, getter, { val collection = ArrayList<Int>(); collection.addAll(it); setter(collection); }, Int::toString, String::toInt)

open class FloatArrayListItem(option: String, getter: (() -> Collection<Float>)?, setter: (ArrayList<Float>) -> Unit)
    : ArrayListItem<Float>(option, getter, { val collection = ArrayList<Float>(); collection.addAll(it); setter(collection); }, Float::toString, String::toFloat)

open class DoubleArrayListItem(option: String, getter: (() -> Collection<Double>)?, setter: (ArrayList<Double>) -> Unit)
    : ArrayListItem<Double>(option, getter, { val collection = ArrayList<Double>(); collection.addAll(it); setter(collection); }, Double::toString, String::toDouble)

open class StringArrayListItem(option: String, getter: (() -> Collection<String>)?, setter: (ArrayList<String>) -> Unit)
    : ArrayListItem<String>(option, getter, { val collection = ArrayList<String>(); collection.addAll(it); setter(collection); }, { it }, { it })

open class BooleanHashSetItem(option: String, getter: (() -> Collection<Boolean>)?, setter: (HashSet<Boolean>) -> Unit)
    : HashSetItem<Boolean>(option, getter, { val collection = HashSet<Boolean>(); collection.addAll(it); setter(collection); }, { it.toString() }, String::toBoolean) {

    constructor(option: String, readOnly: Boolean, getter: (() -> Collection<Boolean>)?, setter: (HashSet<Boolean>) -> Unit) : this(option, getter.nullIf(readOnly), setter)
}

open class IntHashSetItem(option: String, getter: (() -> Collection<Int>)?, setter: (HashSet<Int>) -> Unit)
    : HashSetItem<Int>(option, getter, { val collection = HashSet<Int>(); collection.addAll(it); setter(collection); }, Int::toString, String::toInt) {

    constructor(option: String, readOnly: Boolean, getter: (() -> Collection<Int>)?, setter: (HashSet<Int>) -> Unit) : this(option, getter.nullIf(readOnly), setter)
}

open class FloatHashSetItem(option: String, getter: (() -> Collection<Float>)?, setter: (HashSet<Float>) -> Unit)
    : HashSetItem<Float>(option, getter, { val collection = HashSet<Float>(); collection.addAll(it); setter(collection); }, Float::toString, String::toFloat) {

    constructor(option: String, readOnly: Boolean, getter: (() -> Collection<Float>)?, setter: (HashSet<Float>) -> Unit) : this(option, getter.nullIf(readOnly), setter)
}

open class DoubleHashSetItem(option: String, getter: (() -> Collection<Double>)?, setter: (HashSet<Double>) -> Unit)
    : HashSetItem<Double>(option, getter, { val collection = HashSet<Double>(); collection.addAll(it); setter(collection); }, Double::toString, String::toDouble) {

    constructor(option: String, readOnly: Boolean, getter: (() -> Collection<Double>)?, setter: (HashSet<Double>) -> Unit) : this(option, getter.nullIf(readOnly), setter)
}

open class StringHashSetItem(option: String, getter: (() -> Collection<String>)?, setter: (HashSet<String>) -> Unit)
    : HashSetItem<String>(option, getter, { val collection = HashSet<String>(); collection.addAll(it); setter(collection); }, String::toString, String::toString) {

    constructor(option: String, readOnly: Boolean, getter: (() -> Collection<String>)?, setter: (HashSet<String>) -> Unit) : this(option, getter.nullIf(readOnly), setter)
}
