package com.creeperface.nukkitx.resgen.data

import com.creeperface.nukkitx.resgen.util.textElement
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * @author CreeperFace
 */
data class Category(val name: String, val url: String): XMLSerializable {

    override fun toXML(doc: Document, tagName: String): Element {
        val el = doc.createElement(tagName)

        el.appendChild(doc.textElement("NAME", this.name))
        el.appendChild(doc.textElement("URL", this.url))

        return el
    }
}