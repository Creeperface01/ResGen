package com.creeperface.nukkitx.resgen.data

import com.creeperface.nukkitx.resgen.util.str
import com.creeperface.nukkitx.resgen.util.textElement
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * @author CreeperFace
 */
data class Version(
        val version: String,
        val title: String,
        val message: String,
        val downloads: Int,
        val rating: Double,
        val ratings: Int,
        val downloadLink: String,
        val date: String,
        val likes: Int
): XMLSerializable {

    override fun toXML(doc: Document, tagName: String): Element {
        val el = doc.createElement(tagName)

        el.appendChild(doc.textElement("VERSION", this.version))
        el.appendChild(doc.textElement("TITLE", this.title))
        el.appendChild(doc.textElement("MESSAGE", this.message))
        el.appendChild(doc.textElement("DOWNLOADS", this.downloads))
        el.appendChild(doc.textElement("RATING", this.rating))
        el.appendChild(doc.textElement("RATINGS", this.ratings))
        el.appendChild(doc.textElement("DOWNLOAD_LINK", this.downloadLink))
        el.appendChild(doc.textElement("DATE", this.date))
        el.appendChild(doc.textElement("LIKES", this.likes))

        return el
    }
}