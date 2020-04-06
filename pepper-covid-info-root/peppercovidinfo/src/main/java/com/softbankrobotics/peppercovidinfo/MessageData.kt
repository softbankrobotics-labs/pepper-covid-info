package com.softbankrobotics.peppercovidinfo

import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region

data class MessageData(
    val locale: Locale,
    val messages: List<RichMessage>,
    val too_close_message: String,
    val no_touch_message: String
)

val messageDataFromLaureReen = MessageData(
    locale = Locale(Language.ENGLISH, Region.UNITED_STATES),
    messages = listOf(
        RichMessage(
            phraseToShow = "Avoid shaking hands, greet people from afar.",
            resourceToShow = R.drawable.iconfinder_handshake_avoid_contact_5964542,
            animResource = R.raw.hello_a010),
        RichMessage(
            phraseToShow = "Avoid touching your face and your mouth.",
            resourceToShow = R.drawable.iconfinder_avoid_touch_eyes_mouth_face_5964543),
        RichMessage(
            phraseToShow = "Wash your hands often, especially after touching unknown surfaces.",
            resourceToShow = R.drawable.iconfinder_coronovirus_wash_hands_soap_5932586),
        RichMessage(
            phraseToShow = "Use a tissue to cover your mouth when coughing or sneezing, and throw it away.",
            resourceToShow = R.drawable.iconfinder_cough_tissue_close_mouth_5925234),
        RichMessage(
            phraseToShow = "If you have a fever, cough and difficulty breathing, call your doctor.",
            resourceToShow = R.drawable.iconfinder_fever_high_temperature_5925230),
        RichMessage(
            phraseToShow = "Stay at home if possible.",
            resourceToShow = R.drawable.iconfinder_stay_home_coronovirus_5964549),
        RichMessage(
            phraseToShow = "Wear a mask if you are coughing or sneezing.",
            resourceToShow = R.drawable.iconfinder_facial_mask_coronavirus_5964544),
        RichMessage(
            phraseToShow = "Limit physical contact with other people.",
            resourceToShow = R.drawable.iconfinder_limit_physical_contact_5964545),
        RichMessage(
            phraseToShow = "Wash your hands regularly with soap or sanitizer gel, for at least 20 seconds.",
            resourceToShow = R.drawable.iconfinder_wash_hands_regulary_5964550)
        /*
        // Awkward
        RichMessage(
            phraseToShow = "If you believe you have caught the disease, stay at home for 14 days.",
            resourceToShow = R.drawable.iconfinder_coronovirus_home_quarantine_stay_5932590),
         */
        /*
        // Says same think as other message
        RichMessage(
            phraseToShow = "Wash you hands regularly with soap or sanitizer gel.",
            resourceToShow = R.drawable.iconfinder_wash_hand_soap_5925226),
        */
    ),
    too_close_message = "Keep a safe distance away from other people, even me!",
    no_touch_message = "Avoid touching me! Wash your hands as soon as you can."
)