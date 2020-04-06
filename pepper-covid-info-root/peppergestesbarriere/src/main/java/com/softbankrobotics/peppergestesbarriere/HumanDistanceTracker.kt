package com.softbankrobotics.peppergestesbarriere

import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.actuation.Frame
import com.aldebaran.qi.sdk.`object`.human.Human
import com.aldebaran.qi.sdk.util.FutureUtils
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

const val VERY_FAR_AWAY = 1000.0
// We have two distances for effective hysteresis
const val LOW_DIST_THRESHOLD = 0.8
const val HIGH_DIST_THRESHOLD = 0.975
const val CHECK_PERIOD_MILLISECONDS : Long = 500

class HumanDistanceTracker(
    private val qiContext: QiContext,
    private val onHumanIsTooCloseChanged: (Boolean) -> Unit,
    private val onNearestDistanceChanged: ((Double) -> Unit)?
) {
    private var running = false
    private var robotFrame : Frame = qiContext.actuation.robotFrame()
    private var humanIsTooClose = false
    private var numOkayChecks = 0
    private var smoothedDistance : Double? = null

    private fun checkHumansDistance(humans : List<Human>) {
        val measured = humans.map(this::distanceToPepper).min()
        smoothedDistance = if (measured != null) {
            // Average of the prev 2
            0.5 * (smoothedDistance ?: measured) + 0.5 * measured
        } else {
            // null (i.e. far away)
            null
        }
        val distance = smoothedDistance ?: VERY_FAR_AWAY
        if (humanIsTooClose) {
            if (distance > HIGH_DIST_THRESHOLD) {
                // increase counter
                numOkayChecks += 1
            } else {
                // reset counter
                numOkayChecks = 0
            }
            // Now check exit condition
            if (numOkayChecks > 3) {
                humanIsTooClose = false
                onHumanIsTooCloseChanged(humanIsTooClose)
            }
        } else {
            if (distance <= LOW_DIST_THRESHOLD) {
                humanIsTooClose = true
                numOkayChecks = 0
                onHumanIsTooCloseChanged(humanIsTooClose)
            }
        }
        // For debug:
        onNearestDistanceChanged?.invoke(distance)
    }

    private fun checkHumansAround() {
        if (running) {
            qiContext.humanAwareness.async().humansAround.andThenCompose { humans ->
                checkHumansDistance(humans)
                FutureUtils.wait(CHECK_PERIOD_MILLISECONDS, TimeUnit.MILLISECONDS)
            }.andThenConsume {
                checkHumansAround()
            }
        }
    }

    fun start() {
        running = true
        checkHumansAround()
    }

    private fun distanceToPepper(human: Human): Double {
        val delta = human.headFrame.computeTransform(robotFrame).transform.translation
        val x = delta.x
        val y = delta.y
        return sqrt(x * x + y * y)
    }

    fun stop() {
        running = true
        qiContext.humanAwareness.removeAllOnHumansAroundChangedListeners()
    }

}