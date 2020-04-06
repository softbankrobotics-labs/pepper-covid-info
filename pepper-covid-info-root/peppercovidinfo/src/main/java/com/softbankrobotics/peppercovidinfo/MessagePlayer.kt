package com.softbankrobotics.peppercovidinfo

import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder

fun buildSay(qiContext : QiContext, locale : Locale, text : String) : Say {
    return SayBuilder.with(qiContext)
        .withLocale(locale)
        .withText(text)
        .build()
}

class MessagePlayer(private val qiContext : QiContext,
                    locale: Locale,
                    val message: RichMessage
                    ) {
    private val say = buildSay(qiContext, locale, message.phraseToSay)
    private val animate : Animate? = buildAnimate()

    private fun buildAnimate(): Animate? {
        if (message.animResource == null) {
            return null
        }
        val animation = AnimationBuilder.with(qiContext)
            .withResources(message.animResource)
            .build()
        return AnimateBuilder
            .with(qiContext)
            .withAnimation(animation)
            .build()
    }

    fun run() : Future<Void> {
        return if (animate == null) {
            say.async().run()
        } else {
            Future.waitAll(say.async().run(), animate.async().run())
        }
    }
}

