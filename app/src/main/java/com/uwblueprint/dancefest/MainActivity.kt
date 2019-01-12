package com.uwblueprint.dancefest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // temporary: open events page when app starts
        val intent = Intent(this, EventActivity::class.java)
        startActivity(intent)
    }
}
