package com.uwblueprint.dancefest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import android.content.Intent
import com.google.firebase.firestore.FieldValue
import com.uwblueprint.dancefest.firebase.FirestoreUtils
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Performance
import kotlinx.android.synthetic.main.activity_critique_form.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

private const val REQUEST_RECORDING_PERMISSION = 200
private const val LOG_TAG = "CritiqueFormActivity"

class CritiqueFormActivity : AppCompatActivity() {

    private lateinit var performance: Performance
    private var adjudication: Adjudication? = null
    private lateinit var eventId: String
    private lateinit var eventTitle: String
    private var tabletId: Long = -1
    private val firestore: FirestoreUtils = FirestoreUtils()

    companion object {
        const val EMPTY_STRING = ""
    }


    // Recording vars
    private var isRecording = false
    private var permissions: Array<String> =
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var mRecorder: MediaRecorder? = null
    private val storage = FirebaseStorage.getInstance()
    private lateinit var localFileName: String
    private var firebasePath: String? = null
    private var hasRecorded = false
    private var audioURL: String? = null
    private var audioLength: Int? = null

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
            adjudication?.artisticMark?.toString() ?: EMPTY_STRING
        )
        technicalScoreInput.setText(
            adjudication?.technicalMark?.toString() ?: EMPTY_STRING
        )
        notesInput.setText(
            adjudication?.notes ?: EMPTY_STRING
        )

        populateInfoCard()

        val ADJpath = "events/$eventId/performances/${performance.performanceId}/adjudications"
        saveButton.setOnClickListener {
            var artisticScore: Int = artisticScoreInput.text.toString().toIntOrNull() ?: -1
            if (artisticScore < 0) {
                artisticScore = -1
            }
            var technicalScore = technicalScoreInput.text.toString().toIntOrNull() ?: -1
            if (technicalScore < 0) {
                technicalScore = -1
            }
            val judgeNotes = notesInput.text.toString()
            var cumulativeMark: Double = -1.0

            if (artisticScore >= 0 && technicalScore >= 0) {
                cumulativeMark = (artisticScore + technicalScore) / 2.0
            } else if (artisticScore < 0 && technicalScore >= 0) {
                cumulativeMark = technicalScore.toDouble()
            } else if (artisticScore >= 0 && technicalScore < 0) {
                cumulativeMark = artisticScore.toDouble()
            }

            val data: HashMap<String, Any?> = hashMapOf(
                "artisticMark" to artisticScore,
                "cumulativeMark" to cumulativeMark,
                "notes" to judgeNotes,
                "tabletID" to tabletId,
                "technicalMark" to technicalScore
            )

            if (adjudication == null) {
                firestore.addData(ADJpath, data) { id ->
                    firebasePath = id
                    // Check for wifi connectivity
                    val cm =
                        this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
                    val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                    if (hasRecorded && isConnected) {
                        saveRecording(localFileName, id) {
                            val audioData: HashMap<String, Any?> = hashMapOf(
                                "audioURL" to makeFirebasePath(id),
                                "audioLength" to getAudioLength(localFileName)
                            )
                            firestore.updateData(
                                ADJpath,
                                id,
                                audioData,
                                true
                            )
                        }
                    }
                }
            } else {
                firestore.updateData(ADJpath, adjudication!!.adjudicationId, data, true)
            }

            val intent = Intent(this, SavedCritiqueActivity::class.java)
            startActivity(intent)
        }

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORDING_PERMISSION)

        // Each device only has one adjudication per performance so this is unique
        // for the context of saving locally.
        localFileName = performance.performanceId
        if (adjudication != null) {
            firebasePath = adjudication!!.adjudicationId
        }

        audioURL = adjudication?.audioURL
        audioLength = adjudication?.audioLength

        if (adjudication == null || audioURL == null || audioLength == null) {
            showAudioInstructions()
        } else {
            getAndShowAudio(localFileName, adjudication!!.adjudicationId)
        }

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
                        saveRecording(localFileName, firebasePath!!) {
                            // Check for wifi connectivity
                            val cm =
                                this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
                            val isConnected: Boolean =
                                activeNetwork?.isConnectedOrConnecting == true
                            if (hasRecorded && isConnected) {
                                saveRecording(localFileName, firebasePath!!) {
                                    val audioData: HashMap<String, Any?> = hashMapOf(
                                        "audioURL" to makeFirebasePath(firebasePath!!),
                                        "audioLength" to getAudioLength(localFileName)
                                    )
                                    firestore.updateData(
                                        ADJpath,
                                        firebasePath!!,
                                        audioData,
                                        true
                                    )
                                    audioURL = makeFirebasePath(firebasePath!!)
                                }
                            }
                        }
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
                startRecording(localFileName)
                isRecording = !isRecording
            }
        }

        deleteButton.setOnClickListener {
            if (firebasePath != null && audioURL != null) {
                val storeRef = storage.reference
                val audioFileRef = storeRef.child(audioURL!!)
                audioFileRef.delete().addOnSuccessListener {
                    var file = File(makeAudioFilePath(localFileName))
                    if (file.exists()) {
                        file.delete()
                    }
                    val audioDeletes: HashMap<String, Any?> = hashMapOf(
                        "audioURL" to FieldValue.delete(),
                        "audioLength" to FieldValue.delete()
                    )
                    firestore.updateData(
                        ADJpath,
                        firebasePath!!,
                        audioDeletes,
                        true
                    ) {
                        audioURL = null
                        audioLength = null
                    }
                    Log.i(LOG_TAG, "Successfully deleted file on Firebase")
                    showAudioInstructions()
                }
            } else {
                var file = File(makeAudioFilePath(localFileName))
                if (file.exists()) {
                    file.delete()
                }
                showAudioInstructions()
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
        groupSizeInput.text = performance.danceSize
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
        if (!permissionToRecord) {
            finish()
        }
    }

    private fun startRecording(fileName: String) {
        // If external storage isn't available, recording is impossible
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            finish()
        }
        mRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(makeAudioFilePath(fileName))
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
        if (adjudication == null) {
            getAndShowAudio(localFileName)
        } else {
            getAndShowAudio(localFileName, adjudication!!.adjudicationId)
        }

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

    private fun saveRecording(
        localPath: String,
        firebasePath: String,
        callback: (() -> Unit)? = null
    ) {
        val storeRef = storage.reference
        val audioFileRef = storeRef.child(makeFirebasePath(firebasePath))
        val localFile = File(makeAudioFilePath(localPath))
        val source = Uri.fromFile(localFile)
        audioFileRef.putFile(source).addOnSuccessListener {
            Log.i(LOG_TAG, "Successfully uploaded $source to ${makeFirebasePath(firebasePath)}")
            if (callback != null) {
                callback()
            }
        }.addOnFailureListener {
            Log.e(LOG_TAG, "Failed to upload $source to ${makeFirebasePath(firebasePath)}")
        }
    }

    private fun getAndShowAudio(audioFileName: String, displayName: String = audioFileName): Int? {
        val mediaPlayer = MediaPlayer()
        try {
            val mediaStream = FileInputStream(makeAudioFilePath(audioFileName))
            mediaPlayer.setDataSource(mediaStream.fd)
            mediaPlayer.prepare()
            val length = mediaPlayer.duration
            mediaPlayer.reset()
            mediaPlayer.release()
            mediaStream.close()
            showAudioModal(displayName, getDisplayTime(length))
            return length
        } catch (e: FileNotFoundException) {
            showAudioInstructions()
            return null
        }
    }

    private fun getAudioLength(audioFileName: String): Int? {
        val mediaPlayer = MediaPlayer()
        try {
            val mediaStream = FileInputStream(makeAudioFilePath(audioFileName))
            mediaPlayer.setDataSource(mediaStream.fd)
            mediaPlayer.prepare()
            val length = mediaPlayer.duration
            mediaPlayer.reset()
            mediaPlayer.release()
            mediaStream.close()
            return length
        } catch (e: FileNotFoundException) {
            return null
        }
    }

    private fun makeAudioFilePath(fileName: String): String {
        return "${Environment.getExternalStorageDirectory().absolutePath}/$fileName.mp3"
    }

    private fun makeFirebasePath(fileName: String): String {
        return "Audio/$fileName.mp3"
    }

    private fun showAudioInstructions() {
        audioCard.visibility = View.GONE
        audio_instructions.visibility = View.VISIBLE
    }

    private fun showAudioModal(fileName: String, length: String) {
        audioCard.visibility = View.VISIBLE
        audio_instructions.visibility = View.GONE
        audioTitleLabel.text = "$fileName.mp3"
        audioTimeLabel.text = length
    }

    private fun getDisplayTime(length: Int): String {
        val totalSeconds = length / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val formattedSeconds = String.format("%02d", seconds)
        return "$minutes:$formattedSeconds"
    }

}
