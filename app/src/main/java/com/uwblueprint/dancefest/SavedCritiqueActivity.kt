package com.uwblueprint.dancefest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_saved_critique.*

class SavedCritiqueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_saved_critique)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, PerformanceActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}
