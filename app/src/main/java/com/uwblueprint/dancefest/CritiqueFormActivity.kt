package com.uwblueprint.dancefest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.uwblueprint.dancefest.firebase.FirestoreUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_critique_form.*

class CritiqueFormActivity : AppCompatActivity() {

    private lateinit var performance: Performance
    private lateinit var eventId: String
    private lateinit var eventTitle: String
    private lateinit var tabletId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_critique_form)

        // Current placeholders for information passed from the previous activity.
        eventId = "G25liC0iKFYcZFG3l2d6"
        eventTitle = "OSSDF2018 - Dance to the Rhythm"
        tabletId = "3"

        performance = Performance("1MhtaBW3J0WtoHY3fr3R", "6.0",
                "Test2", "6.0_SpanishRose_Competitive_Duet",
                "Paige Docherty, Devyn Kummer", "Musical Theater", "Competitive",
                "BCI", "Secondary", "Duet", listOf("",""))

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

            Toast.makeText(this@CritiqueFormActivity, "CRITIQUE SAVED", Toast.LENGTH_SHORT).show()

            val collectionPath = "events/$eventId/performances/${performance.id}/adjudications"
            val data = HashMap<String, Any?>()
            data["artisticMark"] = artisticScore
            data["technicalMark"] = technicalScore
            data["cumulativeScore"] = cumulativeScore
            data["notes"] = judgeNotes
            data["tabletID"] = tabletId

            FirestoreUtils().addData(collectionPath, data)
        }
    }

    private fun populateInfoCard() {

        setTitle(R.string.adjudication)
        val navPath = "$eventTitle  > ${performance.name}"

        if (navPath.count() >= 60) {
            navPath.substring(IntRange(0, 60))
        }

        if (performance.performers.count() >= 30) {
            performance.performers.substring(IntRange(0, 30))
        }

        navigationBar.text = "$navPath..."
        danceIDInput.text = performance.entry
        danceTitleInput.text = performance.title
        performersInput.text = "${performance.performers}..."
        danceStyleInput.text = performance.style
        levelOfCompInput.text = performance.levelOfComp
        schoolInput.text = performance.school
        levelInput.text = performance.level
        groupSizeInput.text = performance.size

    }
}
