package com.uwblueprint.dancefest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.uwblueprint.dancefest.firebase.FirestoreUtils
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Performance
import kotlinx.android.synthetic.main.activity_critique_form.*

class CritiqueFormActivity : AppCompatActivity() {

    private lateinit var performance: Performance
    private var adjudication: Adjudication? = null
    private lateinit var eventId: String
    private lateinit var eventTitle: String
    private var tabletId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_critique_form)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Current placeholders for information passed from the previous activity.
        // TODO: Remove Placeholders.sxz

        if (intent != null) {
            performance =
                intent.getSerializableExtra(PerformanceFragment.TAG_PERFORMANCE) as Performance
            adjudication =
                intent.getSerializableExtra(PerformanceFragment.TAG_ADJUDICATION) as? Adjudication
            eventId = intent.getSerializableExtra(PerformanceActivity.TAG_EVENT_ID) as String
            eventTitle = intent.getSerializableExtra(PerformanceActivity.TAG_TITLE) as String
            tabletId = intent.getLongExtra(PerformanceActivity.TAG_TABLET_ID, -1)
        }

        if (adjudication != null) {
            val adjudication = adjudication
            artisticScoreInput.setText(adjudication?.artisticMark.toString())
            technicalScoreInput.setText(adjudication?.technicalMark.toString())
            notesInput.setText(adjudication?.notes)
        } else {
            technicalScoreInput.setText("")
            artisticScoreInput.setText("")
            notesInput.setText("")
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

            val ADJpath = "events/$eventId/performances/${performance.performanceId}/adjudications"
            val data: HashMap<String, Any?> = hashMapOf(
                    "artisticMark" to artisticScore,
                    "technicalMark" to technicalScore,
                    "cumulativeScore" to cumulativeScore,
                    "notes" to judgeNotes,
                    "tabletID" to tabletId)

            if (adjudication == null) {
                FirestoreUtils().addData(ADJpath, data)
            } else {
                FirestoreUtils().updateData(ADJpath, adjudication!!.adjudicationId, data)
            }

            Toast.makeText(this@CritiqueFormActivity, 
                    "CRITIQUE SAVED", 
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateInfoCard() {
        setTitle(R.string.adjudication)
        val navPath = "$eventTitle  > ${performance.danceTitle}"

        if (navPath.count() >= 60) {
            navPath.substring(IntRange(0, 60))
        }

        if (performance.performers.count() >= 30) {
            performance.performers.substring(IntRange(0, 30))
        }

        navigationBar.text = "$navPath..."
        danceIDInput.text = performance.danceEntry
        danceTitleInput.text = performance.danceTitle
        performersInput.text = "${performance.performers}..."
        danceStyleInput.text = performance.danceStyle
        levelOfCompInput.text = performance.competitionLevel
        schoolInput.text = performance.school
        levelInput.text = performance.competitionLevel
        groupSizeInput.text = performance.size

    }

    override fun onSupportNavigateUp(): Boolean {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        onBackPressed()
        return true
    }
}
