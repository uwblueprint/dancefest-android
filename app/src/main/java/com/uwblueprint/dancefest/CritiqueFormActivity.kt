package com.uwblueprint.dancefest

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

    companion object {
        const val EMPTY_STRING = ""
        const val RETURN_TO_PERFORMANCES = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_critique_form)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent != null) {
            performance =
                intent.getSerializableExtra(PerformanceFragment.TAG_PERFORMANCE) as Performance
            adjudication =
                intent.getSerializableExtra(PerformanceFragment.TAG_ADJUDICATION) as? Adjudication
            eventId = intent.getSerializableExtra(PerformanceActivity.TAG_EVENT_ID) as String
            eventTitle = intent.getSerializableExtra(PerformanceActivity.TAG_TITLE) as String
            tabletId = intent.getLongExtra(PerformanceActivity.TAG_TABLET_ID, -1)
        }

        artisticScoreInput.setText(if (adjudication != null)
            adjudication!!.artisticMark.toString() else EMPTY_STRING)
        technicalScoreInput.setText(if (adjudication != null)
            adjudication!!.technicalMark.toString() else EMPTY_STRING)
        notesInput.setText(if (adjudication != null)
            adjudication!!.notes else EMPTY_STRING)

        populateInfoCard()

        saveButton.setOnClickListener {
            val artisticScore = artisticScoreInput.text.toString().toIntOrNull() ?: -1
            val technicalScore = technicalScoreInput.text.toString().toIntOrNull() ?: -1
            val judgeNotes = notesInput.text.toString()
            var cumulativeMark = -1

            if (artisticScore >= 0 && technicalScore >= 0) {
                cumulativeMark = (artisticScore + technicalScore) / 2
            }

            val ADJpath = "events/$eventId/performances/${performance.performanceId}/adjudications"
            val data: HashMap<String, Any?> = hashMapOf(
                "artisticMark" to artisticScore,
                "cumulativeMark" to cumulativeMark,
                "notes" to judgeNotes,
                "tabletID" to tabletId,
                "technicalMark" to technicalScore)

            if (adjudication == null) {
                FirestoreUtils().addData(ADJpath, data)
            } else {
                FirestoreUtils().updateData(ADJpath, adjudication!!.adjudicationId, data)
            }

            val intent = Intent(this, SavedCritiqueActivity::class.java)
            startActivityForResult(intent, RETURN_TO_PERFORMANCES)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RETURN_TO_PERFORMANCES) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
            }
        }
    }

    private fun populateInfoCard() {
        setTitle(R.string.adjudication)
        var navPath = "$eventTitle > ${performance.danceTitle}"

        navigationBar.text = navPath
        danceIDInput.text = performance.danceEntry
        danceTitleInput.text = performance.danceTitle
        performersInput.text = performance.performers
        danceStyleInput.text = performance.danceStyle
        levelOfCompInput.text = performance.competitionLevel
        schoolInput.text = performance.school
        levelInput.text = performance.academicLevel
        groupSizeInput.text = performance.size
    }

    override fun onSupportNavigateUp(): Boolean {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        onBackPressed()
        return true
    }
}
