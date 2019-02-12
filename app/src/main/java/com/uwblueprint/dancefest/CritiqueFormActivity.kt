package com.uwblueprint.dancefest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.uwblueprint.dancefest.firebase.FirestoreUtils
import com.uwblueprint.dancefest.models.Adjudication
import kotlinx.android.synthetic.main.activity_critique_form.*

class CritiqueFormActivity : AppCompatActivity() {

    private lateinit var performance: Performance
    private lateinit var adjudication: Adjudication
    private lateinit var eventId: String
    private lateinit var eventTitle: String
    private lateinit var tabletId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_critique_form)

        // Current placeholders for information passed from the previous activity.
        // TODO: Remove Placeholders.

        eventId = "G25liC0iKFYcZFG3l2d6"
        eventTitle = "OSSDF2018 - Dance to the Rhythm"
        tabletId = "3"

        if (intent != null) {
            performance = intent.getSerializableExtra("performances") as Performance
            adjudication = intent.getSerializableExtra("adjudication") as Adjudication
            eventId = intent.getSerializableExtra("eventId") as String
            eventTitle = intent.getSerializableExtra("eventTitle") as String

        }

        if (adjudication != null) {
            getAdjudicationInfo()
        }

        populateInfoCard()

        saveButton.setOnClickListener {

            var artisticScore = artisticScoreInput.text.toString().toIntOrNull()
            var technicalScore = technicalScoreInput.text.toString().toIntOrNull()
            val judgeNotes = notesInput.text.toString()
            var cumulativeScore = -1

            if (artisticScore == null) {
                artisticScore = -1
            }

            if (technicalScore == null) {
                technicalScore = -1
            }

            if (artisticScore >= 0 && technicalScore >= 0) {
                cumulativeScore = (artisticScore + technicalScore) / 2
            }

            Toast.makeText(this@CritiqueFormActivity, "CRITIQUE SAVED", Toast.LENGTH_SHORT).show()

            val collectionPath = "events/$eventId/performances/${performance.id}/adjudications"
            val data: HashMap<String, Any?> = hashMapOf("artisticMark" to artisticScore,
                    "technicalMark" to technicalScore, "cumulativeScore" to cumulativeScore,
                    "notes" to judgeNotes, "tabletID" to tabletId)

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

    private fun getAdjudicationInfo() {
        artisticScoreInput.setText(adjudication.artisticMark.toString())
        technicalScoreInput.setText(adjudication.technicalMark.toString())
        notesInput.setText(adjudication.notes)
    }
}
