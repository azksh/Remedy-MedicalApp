package com.uts.remedy.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Data classes for the app
data class User(
    var id: String,
    val name: String,
    val email: String,
)
data class Medicine(
    var id: String,
    val name: String,
    val dosage: String,
    val perDay: Int,
    val timingInstruction: String,
    val dateTimes: Long,
    val completion: String,
    val image: String,
    val description: String,
    val type: String,
    val userId: String? = null
)
data class DosageRecord(
    var id: String,
    val medicineId: String,
    val dosageTaken: String,
    val timeTaken: Long,
)
data class Reminder(
    var id: String,
    val medicineId: String,
    val reminderTime: Long,
    val frequency: String,
)

class MedicineViewModel() : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // StateFlow to hold data
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> get() = _medicines

    private val _dosageRecords = MutableStateFlow<List<DosageRecord>>(emptyList())
    val dosageRecords: StateFlow<List<DosageRecord>> get() = _dosageRecords

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> get() = _reminders

    init {
        fetchUserData()
        fetchMedicines()
        fetchDosageRecords()
        fetchReminders()
    }

    private fun fetchUserData() {
        val currentUser = auth.currentUser
        currentUser?.let {
            _user.value = User(it.uid, it.displayName ?: "", it.email ?: "")
        }
    }

    fun getMedicineById(medicineId: String): Medicine? {
        return medicines.value.find { it.id == medicineId }
    }

    private fun fetchMedicines() {
        val userId = auth.currentUser?.uid ?: return
        Log.i("MedicineViewModel", "Fetching medicines for user ID: $userId")
        firestore.collection("medicines")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val medicineList = documents.map { document ->
                    val dateTimesList = document.get("dateTimes") as? List<*>
                    val dateTime = dateTimesList?.firstOrNull() as? Long ?: System.currentTimeMillis()
                    
                    Medicine(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        dosage = document.getString("dosage") ?: "",
                        perDay = document.getLong("perDay")?.toInt() ?: 0,
                        timingInstruction = document.getString("timingInstruction") ?: "",
                        dateTimes = dateTime,
                        completion = document.getString("completion") ?: "",
                        image = document.getString("image") ?: "",
                        description = document.getString("description") ?: "",
                        type = document.getString("type") ?: "",
                        userId = document.getString("userId") ?: ""
                    )
                }
                _medicines.value = medicineList
            }
    }

    private fun fetchDosageRecords() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("dosageRecords")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val dosageList = documents.map { document ->
                    DosageRecord(
                        id = document.id,
                        medicineId = document.getString("medicineId") ?: "",
                        dosageTaken = document.getString("dosageTaken") ?: "",
                        timeTaken = document.getDate("timeTaken")?.time ?: 0
                    )
                }
                _dosageRecords.value = dosageList
            }
    }

    private fun fetchReminders() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("reminders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val reminderList = documents.map { document ->
                    Reminder(
                        id = document.id,
                        medicineId = document.getString("medicineId") ?: "",
                        reminderTime = document.getDate("reminderTime")?.time ?: 0,
                        frequency = document.getString("frequency") ?: ""
                    )
                }
                _reminders.value = reminderList
            }
    }

    // Add methods to add/update/delete medicines, dosage records, and reminders
    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val medicineData = hashMapOf(
                "name" to medicine.name,
                "dosage" to medicine.dosage,
                "perDay" to medicine.perDay,
                "timingInstruction" to medicine.timingInstruction,
                "dateTimes" to listOf(medicine.dateTimes), // Wrap in a list
                "completion" to medicine.completion,
                "image" to medicine.image,
                "description" to medicine.description,
                "type" to medicine.type,
                "userId" to userId
            )
            firestore.collection("medicines")
                .add(medicineData)
                .addOnSuccessListener { documentReference ->
                    medicine.id = documentReference.id
                    fetchMedicines()
                }
                .addOnFailureListener { e ->
                    Log.e("MedicineViewModel", "Error adding medicine: $e")
                }
        }
    }

    fun updateMedicine(medicine: Medicine) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val medicineData = hashMapOf(
                "name" to medicine.name,
                "dosage" to medicine.dosage,
                "perDay" to medicine.perDay,
                "timingInstruction" to medicine.timingInstruction,
                "dateTimes" to medicine.dateTimes,
                "completion" to medicine.completion,
                "image" to medicine.image,
                "description" to medicine.description,
                "type" to medicine.type,
                "userId" to userId
            )
            firestore.collection("medicines").document(medicine.id)
                .set(medicineData)
                .addOnSuccessListener {
                    fetchMedicines()
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }
    }

    fun deleteMedicine(medicineId: String) {
        viewModelScope.launch {
            firestore.collection("medicines").document(medicineId)
                .delete()
                .addOnSuccessListener {
                    fetchMedicines()
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }
    }

    fun addDosageRecord(dosageRecord: DosageRecord) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val dosageRecordData = hashMapOf(
                "medicineId" to dosageRecord.medicineId,
                "dosageTaken" to dosageRecord.dosageTaken,
                "timeTaken" to dosageRecord.timeTaken,
                "userId" to userId
            )
            firestore.collection("dosageRecords")
                .add(dosageRecordData)
                .addOnSuccessListener { documentReference ->
                    dosageRecord.id = documentReference.id
                    fetchDosageRecords()
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }
    }

    fun updateDosageRecord(dosageRecord: DosageRecord) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val dosageRecordData = hashMapOf(
                "medicineId" to dosageRecord.medicineId,
                "dosageTaken" to dosageRecord.dosageTaken,
                "timeTaken" to dosageRecord.timeTaken,
                "userId" to userId
            )
            firestore.collection("dosageRecords").document(dosageRecord.id)
                .set(dosageRecordData)
                .addOnSuccessListener {
                    fetchDosageRecords()
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }
    }

    fun deleteDosageRecord(dosageRecordId: String) {
        viewModelScope.launch {
            firestore.collection("dosageRecords").document(dosageRecordId)
                .delete()
                .addOnSuccessListener {
                    fetchDosageRecords()
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }
    }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val reminderData = hashMapOf(
                "medicineId" to reminder.medicineId,
                "reminderTime" to reminder.reminderTime,
                "frequency" to reminder.frequency,
                "userId" to userId
            )
            firestore.collection("reminders")
                .add(reminderData)
                .addOnSuccessListener { documentReference ->
                    reminder.id = documentReference.id
                    fetchReminders()
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val reminderData = hashMapOf(
                "medicineId" to reminder.medicineId,
                "reminderTime" to reminder.reminderTime,
                "frequency" to reminder.frequency,
                "userId" to userId
            )
            firestore.collection("reminders").document(reminder.id)
                .set(reminderData)
                .addOnSuccessListener {
                    fetchReminders()
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            firestore.collection("reminders").document(reminderId)
                .delete()
                .addOnSuccessListener {
                    fetchReminders()
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }
    }
}
