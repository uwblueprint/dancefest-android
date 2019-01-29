package com.uwblueprint.dancefest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import com.uwblueprint.dancefest.firebase.FirestoreUtils
import kotlinx.android.synthetic.main.activity_critique_form.*

class CritiqueFormActivity : AppCompatActivity() {

    // TODO: remove keypad on tap
    // TODO: pull information from firebase for first view
    // TODO: figure out how to audio field will be displayed (make a view?)
    // TODO: pop ups for save critique and record new message
    // TODO: audio recording

    private lateinit var performance: Performance
    private lateinit var eventId: String
    private lateinit var eventTitle: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_critique_form)


        eventId = "G25liC0iKFYcZFG3l2d6"
        eventTitle = "OSSDF2018 - Dance to the Rhythm"

        performance = Performance("1MhtaBW3J0WtoHY3fr3R", "6.0",
                "Spanish Rose", "6.0_SpanishRose_Competitive_Duet",
                "Paige Docherty, Devyn Kummer", "Musical Theater", "Competitive",
                "BCI", "Secondary", "Duet")


        populateInfoCard()

        val btnSave = findViewById<Button>(R.id.saveButton)
        btnSave?.setOnClickListener {

            val artisticScore = artisticScoreInput.text.toString().toIntOrNull()
            val technicalScore = technicalScoreInput.text.toString().toIntOrNull()
            val judgeNotes = notesInput.text.toString()
            var cumulativeScore = 0


            if (artisticScore != null && technicalScore != null) {
                cumulativeScore = (artisticScore + technicalScore) / 2
            } else {
                Log.e("CRITIQUE_ACTIVITY_FORM", "artisticScore or technicalScore is invalid")

            }

            val collectionPath = "events/$eventId/performances/${performance.id}/adjudications"
            Log.d("critique_form_activity", "artistic score is $artisticScore")
            Log.d("critique_form_activity", "collection path is$collectionPath")


            val data = HashMap<String, Any?>()
            data.put("artisticMark", artisticScore)
            data.put("technicalMark", technicalScore)
            data.put("cumulativeScore", cumulativeScore)
            data.put("notes", judgeNotes)

            FirestoreUtils().addData(collectionPath, data)
        }
    }

    private fun populateInfoCard() {

        danceIDInput.text = performance.entry
        danceTitleInput.text = performance.title

        if (performance.performers.count() >= 30) {
            performance.performers.substring(IntRange(0, 30))
        }

        performersInput.text = performance.performers
        danceStyleInput.text = performance.style
        levelOfCompInput.text = performance.levelOfComp
        schoolInput.text = performance.school
        levelInput.text = performance.level
        groupSizeInput.text = performance.size

    }

}