package dev.baena.locationtotal.models

data class Note (
    val id: Int?,
    val text: String,
    val lat: Double,
    val lng: Double
)

data class Track (
    val name: String,
    val path: String
)


