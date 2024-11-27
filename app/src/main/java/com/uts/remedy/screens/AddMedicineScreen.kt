package com.uts.remedy.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.uts.remedy.models.Medicine
import com.uts.remedy.models.MedicineViewModel

fun navigateToEditMedicine(navController: NavController, medicine: Medicine) {
    navController.navigate("add-medicine/${medicine.id}")
}

@Composable
fun AddMedicineScreen(
    viewModel: MedicineViewModel = viewModel(),
    medicineId: String? = null,
    navController: NavController? = null
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var perDay by remember { mutableIntStateOf(1) }
    var timingInstruction by remember { mutableStateOf("Before Meals") }
    var completion by remember { mutableStateOf("No Need To Finish") }
    var dateTimes by remember { mutableLongStateOf(0) }
    var image by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Twice") }

    Log.d("AddMedicineScreen", "Medicine ID: $medicineId")
    Log.d("AddMedicineScreen", "Medicine Name: $name")
    Log.d("AddMedicineScreen", "Medicine Per Day: $perDay")
    Log.d("AddMedicineScreen", "Medicine Timing Instruction: $timingInstruction")
    Log.d("AddMedicineScreen", "Medicine Completion: $completion")
    Log.d("AddMedicineScreen", "Medicine Date Times: $dateTimes")
    Log.d("AddMedicineScreen", "Medicine Image: $image")
    Log.d("AddMedicineScreen", "Medicine Description: $description")
    Log.d("AddMedicineScreen", "Medicine Type: $type")
    Log.d("AddMedicineScreen", "Medicine Frequency: $frequency")

    var frequencyDropdownExpanded by remember { mutableStateOf(false) }
    var timingInstructionDropdownExpanded by remember { mutableStateOf(false) }
    var completionDropdownExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    LaunchedEffect(medicineId) {
        medicineId?.let {
            val medicine = viewModel.getMedicineById(it)
            if (medicine != null) {
                name = medicine.name
                dosage = medicine.dosage
                perDay = medicine.perDay
                timingInstruction = medicine.timingInstruction
                completion = medicine.completion
                dateTimes = medicine.dateTimes
                image = medicine.image
                description = medicine.description
                type = medicine.type
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 200.dp, top = 24.dp) // Adjust for TabView height
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name
            Column {
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                )
                Text("Medicine name", color = Color.Gray)
            }

            // Dose Amount
            Column {
                BasicTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                )
                Text("Dose Amount", color = Color.Gray)
            }

            // Frequency
            Column {
                Text("Frequency", color = Color.Gray)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Amount")
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Text(
                            text = frequency,
                            modifier = Modifier
                                .clickable { frequencyDropdownExpanded = true }
                                .border(1.dp, Color.Gray)
                                .padding(8.dp)
                        )
                        DropdownMenu(
                            expanded = frequencyDropdownExpanded,
                            onDismissRequest = { frequencyDropdownExpanded = false },
                            content = {
                                DropdownMenuItem(
                                    onClick = {
                                        frequency = "Once"
                                        frequencyDropdownExpanded = false
                                    },
                                    text = { Text("Once") }
                                )
                                DropdownMenuItem(
                                    onClick = {
                                        frequency = "Twice"
                                        frequencyDropdownExpanded = false
                                    },
                                    text = { Text("Twice") }
                                )
                                DropdownMenuItem(
                                    onClick = {
                                        frequency = "Three Times"
                                        frequencyDropdownExpanded = false
                                    },
                                    text = { Text("Three Times") }
                                )
                            }
                        )
                    }
                }
            }

            // Timing Instructions
            Column {
                Text("Timing Instructions", color = Color.Gray)
                Box {
                    Text(
                        text = timingInstruction,
                        modifier = Modifier
                            .clickable { timingInstructionDropdownExpanded = true }
                            .border(1.dp, Color.Gray)
                            .padding(8.dp)
                    )
                    DropdownMenu(
                        expanded = timingInstructionDropdownExpanded,
                        onDismissRequest = { timingInstructionDropdownExpanded = false },
                        content = {
                            DropdownMenuItem(
                                text = { Text("Before Meals") },
                                onClick = {
                                    timingInstruction = "Before Meals"
                                    timingInstructionDropdownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("After Meals") },
                                onClick = {
                                    timingInstruction = "After Meals"
                                    timingInstructionDropdownExpanded = false
                                }
                            )
                        }
                    )
                }
            }

            // Completion
            Column {
                Text("Completion", color = Color.Gray)
                Box {
                    Text(
                        text = completion,
                        modifier = Modifier
                            .clickable { completionDropdownExpanded = true }
                            .border(1.dp, Color.Gray)
                            .padding(8.dp)
                    )
                    DropdownMenu(
                        expanded = completionDropdownExpanded,
                        onDismissRequest = { completionDropdownExpanded = false },
                        content = {
                            DropdownMenuItem(
                                text = { Text("No Need To Finish") },
                                onClick = {
                                    completion = "No Need To Finish"
                                    completionDropdownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Finish By...") },
                                onClick = {
                                    completion = "Finish By..."
                                    completionDropdownExpanded = false
                                }
                            )
                        }
                    )
                }
            }

            // Time Selection
            Text("Select Time", color = Color.Gray)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val times = listOf("06:00 AM", "07:00 AM", "08:00 AM", "09:00 PM", "10:00 PM", "11:00 PM", "12:00 PM", "01:00 PM", "02:00 PM")
                times.chunked(3).forEach { rowTimes ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowTimes.forEach { time ->
                            Button(onClick = { /* Handle time selection */ }) {
                                Text(time)
                            }
                        }
                    }
                }
            }

            Text("*Please enter the time before proceeding", color = Color.Red)

            // Image URL
            Column {
                BasicTextField(
                    value = image,
                    onValueChange = { image = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                )
                Text("Image URL", color = Color.Gray)
            }

            // Description
            Column {
                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                )
                Text("Description", color = Color.Gray)
            }

            // Type
            Column {
                BasicTextField(
                    value = type,
                    onValueChange = { type = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                )
                Text("Type", color = Color.Gray)
            }

            // Add Medicine Track Button
            val context = LocalContext.current
            Button(
                onClick = {
                    if (name.isBlank()) {
                        Toast.makeText(context, "Please enter medicine name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val newMedicine = Medicine(
                        id = medicineId ?: "", // ID will be set by Firestore
                        name = name,
                        dosage = dosage,
                        perDay = perDay,
                        timingInstruction = timingInstruction,
                        dateTimes = System.currentTimeMillis(), // Add current timestamp
                        completion = completion,
                        image = image,
                        description = description,
                        type = type
                    )
                    
                    viewModel.addMedicine(newMedicine)
                    Toast.makeText(context, "Medicine added successfully", Toast.LENGTH_SHORT).show()
                    navController?.navigate("home")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Medicine Track")
            }
        }
    }
}
