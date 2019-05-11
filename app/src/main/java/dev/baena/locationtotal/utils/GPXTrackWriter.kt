package dev.baena.locationtotal.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class GPXTrackWriter(context: Context) {

    companion object {
        val TAG = GPXTrackWriter::class.java.canonicalName
        const val FOLDER_NAME = "tracks"
    }

    var mFile: File
    var mFileHasBeenClosed: Boolean = false

    init {
        val dateString = getDateStringNow()
        ensureFolderExists(context)
        mFile = File(context.filesDir, "/$FOLDER_NAME/$dateString.gpx")
        Log.v(TAG, mFile.absolutePath);
        mFile.createNewFile()
        initializeGPXFile(dateString)
    }

    private fun initializeGPXFile(dateString: String) {
        appendToFile(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                    "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" creator=\"Oregon 400t\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">\n" +
                    "  <metadata>\n" +
                    "    <link href=\"http://lt.baena.dev\">\n" +
                    "      <text>LocationTotal App</text>\n" +
                    "    </link>\n" +
                    "    <time>${dateString}</time>\n" +
                    "  </metadata>\n" +
                    "  <trk>\n" +
                    "    <name>LocationTotal track recorded on ${dateString}</name>\n" +
                    "    <trkseg>\n"
        )
    }

    fun addTrackPoint(lat: Double, lng: Double, ele: Double, date: Date): Boolean {
        if (mFileHasBeenClosed) return false
        appendToFile(
            "      <trkpt lat=\"${String.format("%.5f", lat)}\" lon=\"${String.format("%.5f", lng)}\">\n" +
                    "        <ele>${String.format("%.2f", ele)}</ele>\n" +
                    "        <time>${getDateString(date)}</time>\n" +
                    "      </trkpt>\n"
        )
        return true
    }

    fun closeGPXFile(): Boolean {
        if (mFileHasBeenClosed) return false
        appendToFile(
            "    </trkseg>\n" +
                    "  </trk>\n" +
                    "</gpx>"
        )
        mFileHasBeenClosed = true
        return true
    }

    private fun ensureFolderExists(context: Context) {
        val folder = File(context.filesDir, "$FOLDER_NAME")
        if (!folder.exists())
            folder.mkdir()
    }

    private fun appendToFile(content: String) {
        val outputStream = OutputStreamWriter(FileOutputStream(mFile, true))
        outputStream.append(content)
        outputStream.flush()
    }

    private fun getDateString(date: Date): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK).format(date)

    private fun getDateStringNow(): String = getDateString(Date())

}