package com.uwblueprint.dancefest

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_saved_critique.*

class SavedCritiqueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_critique)

        backButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

    }
}
