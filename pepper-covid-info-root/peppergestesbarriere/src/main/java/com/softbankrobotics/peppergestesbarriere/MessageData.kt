package com.softbankrobotics.peppergestesbarriere

import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region

data class MessageData(
    val locale: Locale,
    val messages: List<RichMessage>,
    val too_close_message: String,
    val no_touch_message: String
)

val messageDataFromFrenchGov = MessageData(
    locale = Locale(Language.FRENCH, Region.FRANCE),
    messages = listOf(
        RichMessage("Lavez-vous très régulièrement les mains", R.drawable.ic_lavmains),
        RichMessage("Toussez ou éternuez dans votre coude ou dans un mouchoir", R.drawable.ic_toussecoude,
            "Toussez ou éternuez dans votre coude \\pau=150\\ ou dans un mouchoir.",
            R.raw.elbow_cough),
        RichMessage("Utilisez un mouchoir à usage unique et jetez-le", R.drawable.ic_mouchoirs),
        RichMessage("Saluez sans se serrer la main, évitez les embrassades", R.drawable.ic_mains,
            "Saluez sans vous serrer la main, et évitez les an \\rspd=120\\ brassades. \\rspd=100\\",
            R.raw.hello_a010)),
    too_close_message = "Maintenez une distance de sécurité, même avec moi!",
    no_touch_message = "Ne me touchez pas, et touchez le moins de surfaces possibles!"
)