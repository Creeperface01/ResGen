package com.creeperface.nukkitx.resgen.data

import com.creeperface.nukkitx.resgen.util.str
import com.creeperface.nukkitx.resgen.util.textElement
import com.creeperface.nukkitx.resgen.util.toXML
import com.creeperface.nukkitx.resgen.util.xml
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * @author CreeperFace
 */
data class Resource(
        val id: Int,
        val name: String,
        val version: String,
        val author: User,
        val contributors: String,
        val sourceCode: String,
        val api: String,
        val tags: Collection<String>,
        val imgUrl: String,
        val content: String,
        val imageCollection: Collection<String>,
        val downloads: Int,
        val downloadLink: String,
        val size: String,
        val released: String,
        val lastUpdate: Long,
        val category: Category,
        val rating: Double,
        val likes: Int,
        val ratings: Collection<Rating>,
        val versions: Collection<Version>
): XMLSerializable {

    override fun toXML(doc: Document, tagName: String): Element {
        val element = doc.createElement("RESOURCE")

        element.appendChild(doc.textElement("ID", id))
        element.appendChild(doc.textElement("NAME", name))
        element.appendChild(doc.textElement("VERSION", version))
        element.appendChild(author.toXML(doc, "AUTHOR"))
        element.appendChild(doc.textElement("CONTRIBUTORS", contributors))
        element.appendChild(doc.textElement("SOURCE_CODE", sourceCode))
        element.appendChild(doc.textElement("API", api))
        element.appendChild(tags.toXML(doc, "TAGS", "TAG"))
        element.appendChild(doc.textElement("IMG_URL", imgUrl))
        element.appendChild(doc.textElement("CONTENT", content))
        element.appendChild(imageCollection.toXML(doc, "IMG_COLLECTION", "IMG"))
        element.appendChild(doc.textElement("DOWNLOADS", downloads))
        element.appendChild(doc.textElement("DOWNLOAD_LINK", downloadLink))
        element.appendChild(doc.textElement("SIZE", size))
        element.appendChild(doc.textElement("RELEASED", released))
        element.appendChild(doc.textElement("LAST_UPDATE", lastUpdate))
        element.appendChild(category.toXML(doc, "CATEGORY"))
        element.appendChild(doc.textElement("RATING", rating))
        element.appendChild(doc.textElement("LIKES", likes))
        element.appendChild(ratings.xml(doc, "RATINGS", "RATING"))
        element.appendChild(versions.xml(doc, "VERSIONS", "VERSION"))

        return element
    }
}