package com.example.paamchssma.schedule

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paamchssma.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ScheduleActivity : AppCompatActivity() {

    private lateinit var timeEditText: EditText
    private lateinit var courseEditText: EditText
    private lateinit var facultyEditText: EditText
    private lateinit var roomEditText: EditText
    private lateinit var floorEditText: EditText
    private lateinit var addScheduleButton: Button
    private lateinit var emptyStateTextView: TextView
    private lateinit var scheduleRecyclerView: RecyclerView

    private val scheduleViewModel: ScheduleViewModel by viewModels {
        val database = ScheduleDatabase.getInstance(applicationContext)
        val repository = ScheduleRepository(database.scheduleDao())
        val userId = Firebase.auth.currentUser?.email ?: "guest" // Email-ul user-ului curent
        ScheduleViewModelFactory(repository, userId)
    }

    private val scheduleAdapter = ScheduleAdapter { entry ->
        scheduleViewModel.deleteEntry(entry)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        // Inițializare views
        timeEditText = findViewById(R.id.timeEditText)
        courseEditText = findViewById(R.id.courseEditText)
        facultyEditText = findViewById(R.id.facultyEditText)
        roomEditText = findViewById(R.id.roomEditText)
        floorEditText = findViewById(R.id.floorEditText)
        addScheduleButton = findViewById(R.id.addScheduleButton)
        emptyStateTextView = findViewById(R.id.emptyStateTextView)
        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView)

        // Setup RecyclerView
        scheduleRecyclerView.layoutManager = LinearLayoutManager(this)
        scheduleRecyclerView.adapter = scheduleAdapter

        // Observă lista de cursuri
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                scheduleViewModel.entries.collect { entries ->
                    scheduleAdapter.submitList(entries)
                    emptyStateTextView.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        // Buton adăugare curs
        addScheduleButton.setOnClickListener {
            addScheduleEntry()
        }
    }

    private fun addScheduleEntry() {
        val time = timeEditText.text.toString().trim()
        val course = courseEditText.text.toString().trim()
        val faculty = facultyEditText.text.toString().trim()
        val room = roomEditText.text.toString().trim()
        val floor = floorEditText.text.toString().trim()

        if (time.isEmpty() || course.isEmpty() || faculty.isEmpty() || room.isEmpty() || floor.isEmpty()) {
            Toast.makeText(this, getString(R.string.schedule_error_fields), Toast.LENGTH_SHORT).show()
            return
        }

        scheduleViewModel.addEntry(time, course, faculty, room, floor)
        
        // Curăță câmpurile
        timeEditText.text.clear()
        courseEditText.text.clear()
        facultyEditText.text.clear()
        roomEditText.text.clear()
        floorEditText.text.clear()

        Toast.makeText(this, getString(R.string.schedule_saved_message), Toast.LENGTH_SHORT).show()
    }
}

