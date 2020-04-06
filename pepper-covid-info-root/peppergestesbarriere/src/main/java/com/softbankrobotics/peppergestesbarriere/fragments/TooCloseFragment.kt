package com.softbankrobotics.peppergestesbarriere.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.softbankrobotics.peppergestesbarriere.R
import kotlinx.android.synthetic.main.fragment_too_close.*
import kotlin.math.min
import kotlin.math.roundToInt

class TooCloseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_too_close, container, false)
    }

    fun onNearestDistanceChanged(distance: Double) {
        activity?.runOnUiThread {
            if (isVisible) {
                val clampedDistance = min(distance, 2.0)
                // round to the nearest 5:
                val roundedCentimeters = (clampedDistance * 20.0).roundToInt() * 5
                val meters = roundedCentimeters / 100
                val centimeters = roundedCentimeters - (100 * meters)
                val paddedCm ="$centimeters".padStart(2, '0')
                distanceLabel.text = "${meters}m${paddedCm}"
                val color = if (distance >= 0.975) {
                    // Good distance: blue
                    distanceCheck.visibility = View.VISIBLE
                    Color.parseColor("#000090")
                } else {
                    // bad distance: red
                    distanceCheck.visibility = View.INVISIBLE
                    Color.parseColor("#e0000f")
                }
                distanceLabel.setTextColor(color)
            }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TooCloseFragment()
    }
}
