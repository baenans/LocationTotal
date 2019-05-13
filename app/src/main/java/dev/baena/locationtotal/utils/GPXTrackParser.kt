package dev.baena.locationtotal.utils

import android.util.Log
import org.osmdroid.util.GeoPoint
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.FileReader
import java.lang.Exception

class GPXTrackParser {
    companion object {
        val TAG = GPXTrackParser::class.java.canonicalName
        var TRKPT_TAG = "trkpt"
        val LAT_IDX = 0
        val LNG_IDX = 1

        fun parseFile(fileName: String): List<GeoPoint> {
            val geopoints = mutableListOf<GeoPoint>()
            try {
                val file = File(fileName)
                if (!file.exists()) return geopoints

                val parser =
                    XmlPullParserFactory.newInstance().apply {
                        isNamespaceAware = true
                    }.newPullParser()

                parser.setInput(FileReader(file))

                var eventType = parser.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.name == TRKPT_TAG) {
                            geopoints.add(
                                GeoPoint(
                                    parser.getAttributeValue(LAT_IDX).toDouble(),
                                    parser.getAttributeValue(LNG_IDX).toDouble()
                                )
                            )
                        }
                    }
                    eventType = parser.next()
                }
                return geopoints
            } catch (e: Exception) {
                return geopoints
            }
        }
    }
}