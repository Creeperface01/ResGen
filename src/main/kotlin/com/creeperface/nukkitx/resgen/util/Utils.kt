package com.creeperface.nukkitx.resgen.util

import com.creeperface.nukkitx.resgen.data.XMLSerializable
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * @author CreeperFace
 */

fun Document.textElement(tagName: String, value: Any?) = textElement(tagName, value.str())

fun Document.textElement(tagName: String, value: String) : Element {
    val el = this.createElement(tagName)
    el.appendChild(this.createTextNode(value))

    return el
}

fun Any?.str() = this.toString()

fun Collection<XMLSerializable>.xml(doc: Document, tagName: String, innerTagName: String): Element {
    val el = doc.createElement(tagName)

    this.forEach {
        el.appendChild(it.toXML(doc, innerTagName))
    }

    return el
}

fun <T> Collection<T>.toXML(doc: Document, tagName: String, innerTagName: String): Element {
    val el = doc.createElement(tagName)

    this.forEach {
        el.appendChild(doc.textElement(innerTagName, it.str()))
    }

    return el
}