package com.creeperface.nukkitx.resgen.data

import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * @author CreeperFace
 */
interface XMLSerializable {

    fun toXML(doc: Document, tagName: String): Element
}