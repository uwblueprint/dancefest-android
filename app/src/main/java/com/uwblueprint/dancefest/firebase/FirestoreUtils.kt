package com.uwblueprint.dancefest.firebase

import android.util.Log
import com.google.firebase.firestore.*

class FirestoreUtils {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FirestoreUtils"

    companion object {
        /*
        Static helper function to convert an object from a DocumentSnapshot to a generic type.
        Takes a nullable object and default value as parameters, checks if the object is of type T,
        and if it is, returns the object casted as type T. Otherwise returns the default value.
        */
        inline fun <reified T> getVal(value: Any?, default: T): T =
            if (value is T) value else default
    }

    init {
        FirebaseFirestoreSettings
            .Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
    }

    fun addData(
        collectionName: String,
        data: HashMap<String, Any?>,
        idCallback: ((String) -> Unit)? = null
    ) {
        db.collection(collectionName)
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.id)
                if (idCallback != null) {
                    idCallback(documentReference.id)
                }
            }
            .addOnFailureListener { e -> Log.e(TAG, "Error adding document", e) }
    }

    fun getData(
        collectionName: String,
        listener: EventListener<QuerySnapshot>
    ) {
        db.collection(collectionName).addSnapshotListener(listener)
    }

    fun updateData(
        collectionName: String,
        docName: String,
        data: HashMap<String, Any?>,
        merge: Boolean = false,
        callback: (() -> Unit)? = null
    ) {
        var docRef = db.collection(collectionName).document(docName)
        var setTask = if (merge) {
            docRef.set(data, SetOptions.merge())
        } else {
            docRef.set(data)
        }
        setTask.addOnSuccessListener {
            Log.d(TAG, "DocumentSnapshot successfully written!")
            if (callback != null) {
                callback()
            }
        }
            .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }
    }
}