package com.uwblueprint.dancefest

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import android.content.Intent
import com.uwblueprint.dancefest.firebase.FirestoreUtils
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Performance
import kotlinx.android.synthetic.main.activity_critique_form.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException

private const val REQUEST_RECORDING_PERMISSION = 200
private const val LOG_TAG = "CritiqueFormActivity"

class CritiqueFormActivity : AppCompatActivity() {

    private lateinit var performance: Performance
    private var adjudication: Adjudication? = null
    private lateinit var eventId: String
    private lateinit var eventTitle: String
    private var tabletId: Long = -1

    companion object {
        const val EMPTY_STRING = ""
    }


    // Recording vars
    private var isRecording = false
    private var permissions: Array<String> =
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var mRecorder: MediaRecorder? = null
    private val storage = FirebaseStorage.getInstance()
    private lateinit var localFilePath: String
    private var firebasePath: String? = null
    private var hasRecorded = false

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

        artisticScoreInput.setText(
            if (adjudication != null)
                adjudication!!.artisticMark.toString() else EMPTY_STRING
        )
        technicalScoreInput.setText(
            if (adjudication != null)
                adjudication!!.technicalMark.toString() else EMPTY_STRING
        )
        notesInput.setText(
            if (adjudication != null)
                adjudication!!.notes else EMPTY_STRING
        )

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
                "technicalMark" to technicalScore
            )

            if (adjudication == null) {
                FirestoreUtils().addData(ADJpath, data) {id ->
                    if (hasRecorded) {
                        saveRecording(localFilePath, id)
                    }
                    firebasePath = id
                }
            } else {
                FirestoreUtils().updateData(ADJpath, adjudication!!.adjudicationId, data)
            }

            val intent = Intent(this, SavedCritiqueActivity::class.java)
            startActivity(intent)
        }

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORDING_PERMISSION)

        // Each device only has one adjudication per performance so this is unique
        // for the context of saving locally
        var audioName = performance.performanceId
        if (adjudication != null) {
            audioName = adjudication!!.adjudicationId
            firebasePath = adjudication!!.adjudicationId
        }
        localFilePath = makeAudioFilePath(audioName)

        //TODO: Check if file already exists and if so populate modal...

        recordButton.setOnClickListener {
            if (isRecording) {
                pauseRecording()
                var alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setMessage(R.string.stop_record_prompt).setPositiveButton(
                    R.string.recording_yes_button
                ) { dialog, _ ->
                    recordButton.setImageResource(R.drawable.microphone)
                    recordButton.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.recordGreen
                        )
                    )
                    isRecording = !isRecording
                    stopRecording()
                    if (firebasePath != null) {
                        saveRecording(localFilePath, firebasePath!!)
                    }
                    hasRecorded = true
                    dialog.dismiss()
                }.setNegativeButton(
                    R.string.recording_cancel_button
                ) { dialog, _ ->
                    resumeRecording()
                    dialog.dismiss()
                }
                alertBuilder.create().show()
            } else {
                recordButton.setImageResource(R.drawable.ic_baseline_stop_24px)
                recordButton.setBackgroundColor(ContextCompat.getColor(this, R.color.recordStop))
                startRecording(localFilePath)
                isRecording = !isRecording
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissionToRecord = if (requestCode == REQUEST_RECORDING_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecord) finish()
    }

    private fun startRecording(fileName: String) {
        // If external storage isn't available recording is impossible
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            finish()
        }
        mRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, e.toString())
            }
            start()
        }
    }

    private fun stopRecording() {
        mRecorder?.apply {
            stop()
            reset()
            release()
        }
        mRecorder = null
    }

    private fun pauseRecording() {
        mRecorder?.apply {
            pause()
        }
    }

    private fun resumeRecording() {
        mRecorder?.apply {
            resume()
        }
    }

    private fun saveRecording(localPath: String, firebasePath: String) {
        val storeRef = storage.reference
        val audioFileRef = storeRef.child("Audio/$firebasePath.mp3")
        val localFileName = File(localPath)
        val source = Uri.fromFile(localFileName)
        audioFileRef.putFile(source).addOnSuccessListener {
            Log.i(LOG_TAG, "Successfully uploaded $source!")
        }.addOnFailureListener {
            Log.e(LOG_TAG, "Failed to upload $source properly")
        }
    }

    private fun makeAudioFilePath(fileName: String): String {
        return "${Environment.getExternalStorageDirectory().absolutePath}/$fileName.mp3"
    }

}
