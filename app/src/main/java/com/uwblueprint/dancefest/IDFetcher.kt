package com.uwblueprint.dancefest

import com.google.firebase.firestore.*
import android.os.Build
import android.util.Log

data class TabletID(val ID: Long)
data class IDCounter(val value: Long)

class IDFetcher {
    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val TAG = "IDFetcher"

        // Firestore collection names
        const val COLLECTION = "tablet_ids"
        const val COUNTER_DOC = "COUNTER"
        const val ID = "ID"
        const val COUNTER_VAL = "value"
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
    fun registerCallback(callback: (Long?) -> Unit) {
        // Calling Build.SERIAL is supposed to be "deprecated" but it should be fine in this case
        // (we aren't expecting people to be spoofing Serial #s)
        val idDocRef = db.collection(COLLECTION).document(Build.SERIAL)
        val counterDocRef = db.collection(COLLECTION).document(COUNTER_DOC)

        // Look up the ID
        idDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {
                callback(document.data?.get(ID) as Long)
            } else {
                // Atomically increment counter and add new tablet
                db.runTransaction { transaction ->
                    val counterSnapshot = transaction.get(counterDocRef)
                    val currentVal = counterSnapshot.getLong(COUNTER_VAL)
                    if (currentVal != null) {
                        val newVal = currentVal + 1
                        transaction.update(counterDocRef, COUNTER_VAL, newVal)
                        transaction.set(idDocRef, TabletID(newVal))
                        newVal
                    } else {
                        // If Counter doesn't exist in Firebase then initialize it
                        transaction.set(counterDocRef, IDCounter(0))
                        transaction.set(idDocRef, TabletID(0))
                        0
                    }
                }.addOnSuccessListener { result ->
                    callback(result)
                }.addOnFailureListener {
                    Log.i(TAG, "Failed to commit new ID for device ${Build.SERIAL}")
                    throw Exception("Couldn't find the ID counter")
                }
            }
        }
    }
}
