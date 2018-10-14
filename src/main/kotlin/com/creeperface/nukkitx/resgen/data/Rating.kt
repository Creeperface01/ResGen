package com.creeperface.nukkitx.resgen.data

import com.creeperface.nukkitx.resgen.util.str
import com.creeperface.nukkitx.resgen.util.textElement
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * @author CreeperFace
 */
data class Rating(
        val user: User,
        val rating: Double,
        val message: String,
        val version: String,
        val date: String,
        val response: String = ""
): XMLSerializable {

    override fun toXML(doc: Document, tagName: String): Element {
        val el = doc.createElement(tagName)

        el.appendChild(this.user.toXML(doc, "USER"))
        el.appendChild(doc.textElement("RATING", this.rating))
        el.appendChild(doc.textElement("MESSAGE", this.message))
        el.appendChild(doc.textElement("VERSION", this.version))
        el.appendChild(doc.textElement("DATE", this.date))
        el.appendChild(doc.textElement("RESPONSE", this.response))

        return el
    }

}