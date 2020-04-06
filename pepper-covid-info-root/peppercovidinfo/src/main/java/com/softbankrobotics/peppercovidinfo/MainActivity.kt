package com.softbankrobotics.peppercovidinfo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import com.aldebaran.qi.sdk.util.FutureUtils
import com.softbankrobotics.peppercovidinfo.fragments.IdleFragment
import com.softbankrobotics.peppercovidinfo.fragments.MessageFragment
import com.softbankrobotics.peppercovidinfo.fragments.NoTouchFragment
import com.softbankrobotics.peppercovidinfo.fragments.TooCloseFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

private const val DELAY_BETWEEN_LOOPS_SECONDS : Long = 15

class MainActivity : RobotActivity(), RobotLifecycleCallbacks {
    private lateinit var qiContext : QiContext

    // Loop handling
    private var runningLoop = false
    private var loopFuture : Future<Void>? = null

    private val messageData = messageDataFromLaureReen
    private var currentMessageIndex = 0

    private lateinit var messagePlayers : List<MessagePlayer>
    private lateinit var sayTooClose : Say
    private lateinit var sayNoTouch : Say

    // Helpers
    private var humanDistanceTracker : HumanDistanceTracker? = null

    // State
    private var touchWarning = false
    private var humanIsTooClose = false

    // Fragments
    private val idleFragment = IdleFragment.newInstance()
    private val messageFragment = MessageFragment.newInstance()
    private val tooCloseFragment = TooCloseFragment.newInstance()
    private val noTouchFragment = NoTouchFragment.newInstance()

    /***************************
     * Fragment helpers
     **************************/

    private fun setFragment(newFragment : Fragment) {
        val transaction = supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, newFragment)
        }
        transaction.commit();
    }

    /***************************
     * Loop
     **************************/

    private fun startNextLoop() {
        if (runningLoop) {
            loopFuture = FutureUtils.wait(DELAY_BETWEEN_LOOPS_SECONDS, TimeUnit.SECONDS)
                .andThenCompose { showMessage(getNextMessage()) }
                .andThenConsume { startNextLoop() }
        }
    }

    private fun getNextMessage(): MessagePlayer {
        if (currentMessageIndex >= messagePlayers.size) {
            currentMessageIndex = 0
        }
        val messagePlayer = messagePlayers[currentMessageIndex]
        currentMessageIndex += 1
        return messagePlayer
    }

    private fun clearScreen() {
        setFragment(idleFragment)
    }

    private fun showMessage(messagePlayer: MessagePlayer, removeAfterwards : Boolean = true) : Future<Void> {
        messageFragment.setMessage(messagePlayer.message)
        setFragment(messageFragment)
        return messagePlayer.run()
            .andThenConsume {
                if (removeAfterwards) {
                    clearScreen()
                }
            }
    }

    private fun startMessageLoop() {
        runningLoop = true
        startNextLoop()
    }

    private fun stopMessageLoop() {
        runningLoop = false
        loopFuture?.requestCancellation()
    }

    /***************************
     * Humans too close
     **************************/

    private fun onHumanToCloseChanged(isTooClose : Boolean) {
        humanIsTooClose = isTooClose
        if (!touchWarning) {
            if (isTooClose) {
                stopMessageLoop()
                sayTooClose.async().run()
                setFragment(tooCloseFragment)
            } else {
                setFragment(idleFragment)
                startMessageLoop()
            }
        }
    }

    private fun onNearestDistanceChanged(distance : Double) {
        // Debug
        runOnUiThread { debugDistanceLabel.text = "$distance" }
        tooCloseFragment.onNearestDistanceChanged(distance)
    }

    /***************************
     * Touched screen
     **************************/

    private fun handleScreenTouched() {
        if (!touchWarning) {
            stopMessageLoop()
            touchWarning = true
            setFragment(noTouchFragment)
            sayNoTouch.async().run()
            FutureUtils.wait(5, TimeUnit.SECONDS).andThenConsume {
                touchTimeoutDone()
            }
        }
    }

    private fun touchTimeoutDone() {
        touchWarning = false
        if (humanIsTooClose) {
            setFragment(tooCloseFragment)
        } else {
            setFragment(idleFragment)
            startMessageLoop()
        }
    }

    /***************************
     * Robot Lifecycle
     **************************/

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i(TAG, "Robot focus gained")
        this.qiContext = qiContext
        // Initialize list
        messagePlayers = messageData.messages.map { msg ->
            MessagePlayer(
                qiContext,
                messageData.locale,
                msg
            )
        }
        sayTooClose = buildSay(qiContext, messageData.locale, messageData.too_close_message)
        sayNoTouch = buildSay(qiContext, messageData.locale, messageData.no_touch_message)

        // Now start doing stuff
        startMessageLoop()
        humanDistanceTracker = HumanDistanceTracker(qiContext,
            this::onHumanToCloseChanged,
        this::onNearestDistanceChanged)
        humanDistanceTracker?.start()
    }

    override fun onRobotFocusLost() {
        Log.w(TAG, "Robot focus lost")
        stopMessageLoop()
    }

    override fun onRobotFocusRefused(reason: String?) {
        Log.e(TAG, "Robot focus refused because $reason")
    }


    /***************************
     * Android Lifecycle
     **************************/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        QiSDK.register(this, this)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY)
        setFragment(idleFragment)
        mainLayout.setOnTouchListener { _, _ ->
            handleScreenTouched()
            false
        }
    }

    override fun onDestroy() {
        stopMessageLoop()
        humanDistanceTracker?.stop()
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

}
