package com.softbankrobotics.peppercovidinfo



data class RichMessage(
    val phraseToShow : String,
    val resourceToShow: Int? = null,
    val phraseToSay: String = phraseToShow,
    val animResource : Int? = null
)

