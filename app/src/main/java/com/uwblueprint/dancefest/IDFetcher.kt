package com.uwblueprint.dancefest

import com.google.firebase.firestore.*
import android.os.Build
import android.util.Log

data class TabletID(val id: Long)
data class IDCounter(val value: Long)

class IDFetcher {
    private val db = FirebaseFirestore.getInstance()

    companion object {
        // Firestore collection names
        const val COLLECTION = "tablet_ids"
        const val COUNTER_DOC = "COUNTER"
        const val COUNTER_VAL = "value"
        const val ID = "id"

        // Log Identifier
        const val TAG = "IDFetcher"
    }

    init {
        FirebaseFirestoreSettings
            .Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
    }

    /* registerCallback retrieves a unique ID for the caller and invokes callback on it.

    This ID is uniquely matched to the caller's Serial #.

    If the tablet's serial # is not known in Firestore yet, then a new unique ID is generated for
    it. Otherwise, the corresponding ID to the tablet's serial # is retrieved from Firestore.

    The unique ID generated is simply a counter stored in Firestore, which is incremented for each
    new tablet available.
     */
    fun registerCallback(callback: (Long) -> Unit) {
        // Calling Build.SERIAL is supposed to be "deprecated" but it should be fine in this case
        // (we aren't expecting people to be spoofing Serial #s)
        val idDocRef = db.collection(COLLECTION).document(Build.SERIAL)
        val counterDocRef = db.collection(COLLECTION).document(COUNTER_DOC)

        // Look up the ID
        idDocRef.get().addOnSuccessListener { document ->
            val documentData = document?.data
            if (documentData != null && documentData[ID] != null) {
                callback(documentData[ID] as Long)
            } else {
                // Atomically increment counter and add new tablet
                db.runTransaction { transaction ->
                    val counterSnapshot = transaction.get(counterDocRef)
                    val currentVal = counterSnapshot.getLong(COUNTER_VAL)
                    var retVal = 0L;
                    if (currentVal != null) {
                        retVal = currentVal + 1
                        transaction.update(counterDocRef, COUNTER_VAL, retVal)
                        transaction.set(idDocRef, TabletID(retVal))
                    } else {
                        // If Counter doesn't exist in Firebase then initialize it
                        transaction.set(counterDocRef, IDCounter(0))
                        transaction.set(idDocRef, TabletID(0))
                    }
                    retVal
                }.addOnSuccessListener { result ->
                    callback(result)
                }.addOnFailureListener {
                    Log.e(TAG, "Failed to commit new ID for device ${Build.SERIAL}")
                    throw Exception("Couldn't find the ID counter")
                }
            }
        }
    }
}
