package com.creeperface.nukkitx.resgen

import com.creeperface.nukkitx.resgen.data.*
import org.jsoup.Jsoup
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


/**
 * @author CreeperFace
 */

const val BASE_URL = "https://nukkitx.com"

const val CONN_TIMEOUT = 20000

fun main(args: Array<String>) {
    val doc = Jsoup.connect("https://nukkitx.com/resources/").timeout(CONN_TIMEOUT).get()

    val pages = doc.select("div.pageNavLinkGroup > div.pageNav > nav > a[href~=.*page=\\d]:not(.text)")

    val resources = mutableListOf<Resource>()

    resources.addAll(parsePage(1))
    pages.map { it.text().toInt() }.forEach { page ->
        resources.addAll(parsePage(page))
    }

    println(resources.size)
    generateXML(resources)
}

fun parsePage(page: Int): List<Resource> {
    val doc = Jsoup.connect("https://nukkitx.com/resources/?page=$page").timeout(CONN_TIMEOUT).get()

    val items = doc.select(".resourceListItem")
    val resources = mutableListOf<Resource>()

    items.forEach { element ->
        val id = element.attr("id").split("-")[1].toInt()

//        val nameElement = element/*.select(".title")*/.select(".title > a[href$=.$id/]")

//        val name = nameElement.text()

//        resources.add(Resource(id.toInt(), name))
        getResourceInfo(id)?.let {
            resources.add(it)
        }
    }

    return resources
}

fun getResourceInfo(id: Int): Resource? {
    println("resource id: $id")

    try {
        val doc = Jsoup.connect("https://nukkitx.com/resources/$id").timeout(CONN_TIMEOUT).get()

        val content = doc.select("div.mainContent")

        val resourceInfo = content.select("div.resourceInfo")
        val mainInfo = resourceInfo.select("h1")

        val downloadButton = resourceInfo.select("ul.primaryLinks > li > label.downloadButton > a")
        val downloadLink = "$BASE_URL/" + downloadButton.attr("href")
        val size = downloadButton.select("small.minorText").text().replace(".jar", "").trim()

        val imgURL = "$BASE_URL/" + resourceInfo.select("div.resourceImage > img").attr("src")

        val name = mainInfo.text().trim()
        val version = mainInfo.select("span").text()

        val secContent = doc.select("div.secondaryContent > div.pairsJustified")

        val author = run {
            val e = secContent.select("dl.author > dd > a")

            return@run User(e.text(), "$BASE_URL/" + e.attr("href"))
        }

        val downloads = secContent.select("dl.downloadCount > dd").text().replace(",", "").toInt()
        val released = secContent.select("dl.firstRelease > dd > span").attr("title")

        val lastUpdateEl = secContent.select("dl.lastUpdate > dd > abbr").attr("data-time")
        val lastUpdate = if (lastUpdateEl.isEmpty()) 0.toLong() else lastUpdateEl.toLong()

        val category = run {
            val e = secContent.select("dl.resourceCategory > dd > a")

            return@run Category(e.text(), "$BASE_URL/" + e.attr("href"))
        }

        val rating = secContent.select("div.rating > dl > dd > span.ratings").attr("title").toDouble()
        val tags = run {
            val e = content.select("div.tagBlock > ul.tagList > li")

            val tags = mutableListOf<String>()
            e.forEach {
                tags.add(it.select("a").text())
            }

            return@run tags.toList()
        }

        val innerContent = content.select("div.innerContent")

        val primaryContent = innerContent.select("div.updateContainer.section > ol > li.primaryContent")
        val descriptionElement = primaryContent.select("article > blockquote.ugc")

        val imgCollection = primaryContent.select("div.imageCollection > ol > li > a")

        val imageCollection = mutableListOf<String>() //urls
        if (imgCollection.isNotEmpty()) {
            imgCollection.forEach { img ->
                imageCollection.add("$BASE_URL/" + img.attr("href"))
            }
        }

        val aboveInfo = descriptionElement.select("div.aboveInfo")

        val api = aboveInfo.select("dl.customResourceFieldcompatiblity > dd").text()

        val contributors = aboveInfo.select("dl.customResourceFieldcontributors > dd").text()

        val sourceCode = aboveInfo.select("dl.customResourceFieldsourcecode > dd > a").text()

        val descCloned = descriptionElement.clone()
        descCloned.select("div.customResourceFields.aboveInfo").remove()

        val description = descCloned.html()

        val likes = 0 //TODO: likes

        val ratings = run {
            val e = innerContent.select("div.section.reviews > ol > li.primaryContent.review.messageSimple")

            val ratings = mutableListOf<Rating>()
            e.forEach {
                val user = User(it.attr("data-author"), "$BASE_URL/" + e.select("a").attr("href"))

                val info = it.select("div.messageInfo")
                val messageContent = info.select("div.messageContent")

                val rating = messageContent.select("div.rating > dl > dd > span.ratings").attr("title").toDouble()
                val version = messageContent.select("span").text()
                val message = messageContent.select("article > blockquote").text()

                val dateEl = info.select("div.messageMeta > div.privateControls > a.item.muted.mawbutton[title=Permalink]")
                var date = dateEl.select("abbr").attr("title")

                if (date.isEmpty()) {
                    date = dateEl.select("span.DateTime").attr("title")
                }

                val response = run resp@{
                    val el = info.select("ol.messageResponse > li.comment.secondaryContent > div.commentInfo > div.commentContent > article > blockquote")

                    if (el.isEmpty()) {
                        return@resp ""
                    }

                    return@resp el.text()
                }

                ratings.add(Rating(user, rating, message, version, date, response))
            }

            return@run ratings.toList()
        }

        val versions = getVersionInfo(id)

        return Resource(
                id,
                name,
                version,
                author,
                contributors,
                sourceCode,
                api,
                tags,
                imgURL,
                description,
                imageCollection.toList(),
                downloads,
                downloadLink,
                size,
                released,
                lastUpdate,
                category,
                rating,
                likes,
                ratings,
                versions
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun getVersionInfo(id: Int): Collection<Version> {
    val versions = mutableListOf<Version>()

    val doc = Jsoup.connect("$BASE_URL/resources/$id/updates").timeout(CONN_TIMEOUT).get()

    val list = doc.select("div.innerContent > div.updateContainer > ol > li.primaryContent.messageSimple.resourceUpdate")

    val historyDos = Jsoup.connect("$BASE_URL/resources/$id/history").timeout(CONN_TIMEOUT).get()
    val historyList = historyDos.select("div.innerContent > table.dataTable > tbody > tr.dataRow:not(:has(th))")

    if (historyList.size <= 1) {
        return emptyList()
    }

    list.forEach {
        val title = it.select("h2.textHeading > a").text()
        val description = it.select("article > blockquote").text()
        val date = it.select("div.messageMeta > span.privateControls > span.item.muted > a.datePermalink > span.DateTime").attr("title")

//        println(it.select("div.messageMeta").html())
        val historyEl = historyList.find { he -> he.select("td.releaseDate > span.DateTime").attr("title") == date }
//        val historyEl = historyList.select("td.releaseDate > abbr[title=$date]")
//        val historyEl = historyList.select("td.releaseDate:has(span.DateTime[title=$date])")
//        println("ver element: ${historyEl?.html()}")
//        val likesEl = it.select("div#likes-")
//
//        val likes = 0

//        if(likesEl.isNotEmpty()) { //TODO: likes
//
//        } else {
//
//        }

        val version: String
        val downloads: Int
        val rating: Double
        val ratings: Int

        if (historyEl != null) {
            version = historyEl.select("td.version").text()
            downloads = historyEl.select("td.downloads").text().replace(",", "").toInt()

            val ratingEl = historyEl.select("td.rating > div.rating > dl > dd")

            rating = ratingEl.select("span.ratings").attr("title").toDouble()
            ratings = ratingEl.select("span.Hint").text().split(" ")[0].toInt()

        } else {
            version = "-1"
            downloads = -1
            rating = (-1).toDouble()
            ratings = -1
        }

        versions.add(Version(version, title, description, downloads, rating, ratings, "", date, 0))
    }

    return versions.toList()
}

fun generateXML(resources: List<Resource>) {
    val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    val doc = builder.newDocument()

    val root = doc.createElement("RESOURCES")

    resources.forEach {
        root.appendChild(it.toXML(doc, "RESOURCE"))
    }

    doc.appendChild(root)

    val transformerFactory = TransformerFactory.newInstance()
    val transformer = transformerFactory.newTransformer()
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

    val source = DOMSource(doc)
    val result = StreamResult(File("resources.xml"))

    transformer.transform(source, result)
}