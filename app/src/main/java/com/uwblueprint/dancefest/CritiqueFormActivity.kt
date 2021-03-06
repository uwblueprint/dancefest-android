package com.uwblueprint.dancefest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.storage.FirebaseStorage
import com.uwblueprint.dancefest.api.DancefestClient
import com.uwblueprint.dancefest.api.DancefestService
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.AdjudicationPost
import com.uwblueprint.dancefest.models.Performance
import kotlinx.android.synthetic.main.activity_critique_form.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

private const val REQUEST_RECORDING_PERMISSION = 200
private const val LOG_TAG = "CritiqueFormActivity"

class CritiqueFormActivity : AppCompatActivity() {

    companion object {
        const val EMPTY_STRING = ""
        const val LIST_SEPARATOR = ", "
    }

    private lateinit var performance: Performance
    private lateinit var dancefestClient: DancefestClient
    private lateinit var service: DancefestService
    private var adjudication: Adjudication? = null
    private var eventId: Int = -1
    private var eventTitle: String = EMPTY_STRING
    private var tabletId: Int = -1

    private lateinit var awards: ArrayList<String>
    private lateinit var awardsAdapter: AwardsAdapter

    // Recording vars
    private var isRecording = false
    private var permissions: Array<String> =
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var mRecorder: MediaRecorder? = null
    private val storage = FirebaseStorage.getInstance()
    private var localFileName: Int = -1
    private var firebasePath: Int? = null
    private var hasRecorded = false
    private var audioURL: String? = null
    private var audioLength: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_critique_form)

        dancefestClient = DancefestClient()
        service = dancefestClient.getInstance()

        if (intent != null) {
            performance =
                    intent.getParcelableExtra(PerformanceListFragment.TAG_PERFORMANCES) as Performance
            adjudication =
                    intent.getSerializableExtra(PerformanceListFragment.TAG_ADJUDICATIONS) as? Adjudication
            eventId = intent.getSerializableExtra(PerformanceActivity.TAG_EVENT_ID) as Int
            eventTitle = intent.getSerializableExtra(PerformanceActivity.TAG_TITLE) as String
            tabletId = intent.getIntExtra(PerformanceActivity.TAG_TABLET_ID, -1)
        }

        populateInfoCard()

        setupAwards()


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

            val adjudicationPost = AdjudicationPost(
                    artisticMark = artisticScore,
                    cumulativeMark = cumulativeMark,
                    notes = judgeNotes,
                    tabletId = tabletId,
                    technicalMark = technicalScore
            )

            val curAdj = adjudication
            if (curAdj == null) {
                dancefestClient.call(
                        service.createAdjudication(performance.performanceId, adjudicationPost)
                ) {
                    onResponse = {
                        if (it.body() != null) {
                            adjudication = it.body()

                            val adjudicationId = adjudication!!.adjudicationId
                            saveRecording(localFileName, adjudicationId) {
                                saveAudioData(
                                        makeFirebasePath(adjudicationId),
                                        getAudioLength(localFileName)
                                )
                            }
                        }
                    }
                    onFailure = { Log.e("Creating Adjudication", it?.toString()) }
                }
            } else {
                dancefestClient.call(
                        service.updateAdjudication(curAdj.adjudicationId, adjudicationPost)
                ) {
                    onResponse = {
                        if (it.body() != null) {
                            adjudication = it.body()
                        }
                    }
                    onFailure = { Log.e("Updating adjudication", it?.toString()) }
                }
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
                val alertBuilder = AlertDialog.Builder(this)
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
                            audioURL = makeFirebasePath(firebasePath!!)
                            saveAudioData(audioURL, getAudioLength(localFileName))
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
                    val file = File(makeAudioFilePath(localFileName))
                    if (file.exists()) {
                        file.delete()
                    }
                    // Set adjudication audio data to some defaults
                    saveAudioData(EMPTY_STRING, 0)
                    Log.i(LOG_TAG, "Successfully deleted file on Firebase")
                    showAudioInstructions()
                }
            } else {
                val file = File(makeAudioFilePath(localFileName))
                if (file.exists()) {
                    file.delete()
                }
                showAudioInstructions()
            }
        }
    }

    private fun isConnected(): Boolean {
        val cm =
                this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnected!!
    }

    private fun saveAudioData(audioURL: String?, audioLength: Int?) {
        val curAdj = adjudication
        if (curAdj != null) {
            val adjudicationPost = AdjudicationPost(
                    audioURL = audioURL,
                    audioLength = audioLength
            )

            // Update audio data here
            dancefestClient.call(
                    service.updateAdjudication(curAdj.adjudicationId, adjudicationPost)
            ) {
                onResponse = {
                    if (it.body() != null) {
                        adjudication = it.body()
                    }
                }
                onFailure = { Log.e("Updating adjudication audio", it?.toString()) }
            }
        }
    }

    private fun populateInfoCard() {
        artisticScoreInput.setText(adjudication?.artisticMark?.toString() ?: EMPTY_STRING)
        technicalScoreInput.setText(adjudication?.technicalMark?.toString() ?: EMPTY_STRING)
        notesInput.setText(adjudication?.notes ?: EMPTY_STRING)

        setTitle(R.string.adjudication)
        val navPath = "$eventTitle > ${performance.danceTitle}"

        navigationBar.text = navPath
        danceIDInput.text = performance.danceEntry.toString()
        danceTitleInput.text = performance.danceTitle
        performersInput.text = performance.performers?.joinToString(LIST_SEPARATOR)
        danceStyleInput.text = performance.danceStyle
        levelOfCompInput.text = performance.competitionLevel
        schoolInput.text = performance.school
        levelInput.text = performance.academicLevel
        groupSizeInput.text = performance.danceSize
    }

    private fun setupAwards() {
        awardsAdapter = AwardsAdapter(this)
        awards_recycler_view.layoutManager = LinearLayoutManager(this)
        awards_recycler_view.adapter = awardsAdapter
        fetchAwards()
    }

    private fun fetchAwards() {
        dancefestClient.call(service.getAwards(eventId)) {
            onResponse = { response ->
                response.body()?.let { it -> awardsAdapter.updateAwards(it) }
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionToRecord = if (requestCode == REQUEST_RECORDING_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecord) {
            finish()
        }
    }

    private fun startRecording(fileName: Int) {
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
            localPath: Int,
            firebasePath: Int,
            callback: (() -> Unit)? = null
    ) {
        if (hasRecorded && isConnected()) {
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
    }

    private fun getAndShowAudio(audioFileName: Int, displayName: Int = audioFileName): Int? {
        val mediaPlayer = MediaPlayer()
        return try {
            val mediaStream = FileInputStream(makeAudioFilePath(audioFileName))
            mediaPlayer.setDataSource(mediaStream.fd)
            mediaPlayer.prepare()
            val length = mediaPlayer.duration
            mediaPlayer.reset()
            mediaPlayer.release()
            mediaStream.close()
            showAudioModal(displayName, getDisplayTime(length))
            length
        } catch (e: FileNotFoundException) {
            showAudioInstructions()
            null
        }
    }

    private fun getAudioLength(audioFileName: Int): Int? {
        val mediaPlayer = MediaPlayer()
        return try {
            val mediaStream = FileInputStream(makeAudioFilePath(audioFileName))
            mediaPlayer.setDataSource(mediaStream.fd)
            mediaPlayer.prepare()
            val length = mediaPlayer.duration
            mediaPlayer.reset()
            mediaPlayer.release()
            mediaStream.close()
            length
        } catch (e: FileNotFoundException) {
            null
        }
    }

    private fun makeAudioFilePath(fileName: Int): String {
        return "${Environment.getExternalStorageDirectory().absolutePath}/$fileName.mp3"
    }

    private fun makeFirebasePath(fileName: Int): String {
        return "Audio/$fileName.mp3"
    }

    private fun showAudioInstructions() {
        audioCard.visibility = View.GONE
        audio_instructions.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun showAudioModal(fileName: Int, length: String) {
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
