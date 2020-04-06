package com.softbankrobotics.peppercovidinfo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.softbankrobotics.peppercovidinfo.R
import com.softbankrobotics.peppercovidinfo.RichMessage
import kotlinx.android.synthetic.main.fragment_message.*

class MessageFragment : Fragment() {
    private var message: RichMessage? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        message?.let {
            if (it.resourceToShow == null) {
                mainImage.visibility = View.GONE
            } else {
                mainImage.setImageResource(it.resourceToShow)
                mainImage.visibility = View.VISIBLE
            }
            mainLabel.text = it.phraseToShow
            mainLabel.visibility = View.VISIBLE
        }
    }

    fun setMessage(message: RichMessage) {
        this.message = message
    }

    companion object {
        @JvmStatic
        fun newInstance() = MessageFragment()
    }
}
