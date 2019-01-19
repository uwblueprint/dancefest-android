package com.uwblueprint.dancefest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_performance.view.*

class PerformanceFragment : Fragment() {

    val ARG_TEST = "TEST"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_performance, container, false)
        arguments?.takeIf { it.containsKey(ARG_TEST) }?.apply {
            rootView.text_test.text = getString(ARG_TEST)
        }
        return rootView
    }
}