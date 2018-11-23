package com.uwblueprint.dancefest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class CritiqueFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_critique_form)
        fetchEntryData(1.0)
    }

    override fun onResume() {
        super.onResume()
        loadEntry()
    }

    override fun onPause() {
        super.onPause()
        saveEntry()
    }

    private fun fetchEntryData(id: Double) {


    }

    private fun loadEntry() {
        // loads the Entry

    }

    private fun saveEntry() {
        // saves the the current data into Database

    }
}
