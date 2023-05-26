package com.github.ybecker.epforuml.basicEntities.questions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.DatabaseManager.user
import com.github.ybecker.epforuml.features.latex.LatexDialog
import com.github.ybecker.epforuml.features.voiceMessages.AndroidAudioRecorder
import java.io.File
import java.util.*
import com.github.ybecker.epforuml.structure.HomeFragment
import com.github.ybecker.epforuml.util.MainActivity
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.features.voiceMessages.AndroidAudioPlayer
import com.github.ybecker.epforuml.database.Model
import com.github.ybecker.epforuml.features.camera.CameraActivity

/**
 * A simple [Fragment] subclass.
 * Use the [NewQuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewQuestionFragment : Fragment() {

    private var audioFile: File? = null
    private var isRecording = false

    private var audioRecorder: AndroidAudioRecorder? = null

    private var audioPlayer: AndroidAudioPlayer? = null

    private lateinit var questBody : EditText
    private lateinit var questTitle : EditText
    private lateinit var imageURI: TextView
    private lateinit var recordVoiceNote: Button
    private lateinit var takePictureButton: Button
    private lateinit var playVoiceNote: Button

    private lateinit var image_uri : String

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_question, container, false)

        mainActivity = activity as MainActivity
        image_uri = mainActivity.intent.getStringExtra("uri").toString()


        val spinner = view.findViewById<Spinner>(R.id.subject_spinner)
        // Get the set of available courses from the MockDatabase
        db.availableCourses().thenAccept {
            val coursesList = it
            val courseNamesList = coursesList.map { course -> course.courseName }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                courseNamesList
            )
            spinner.adapter = adapter
            setUpArgs(view, spinner, coursesList, user)

            // Shows the latex renderer dialog
            val latexButton = view.findViewById<ImageButton>(R.id.show_latex_button)
            latexButton.setOnClickListener { LatexDialog(requireContext(), questBody).show() }
        }

        return view
    }

    private fun setUpArgs(
        view: View,
        spinner: Spinner,
        coursesList: List<Model.Course>,
        user: Model.User?,
    ) {

        questBody = view.findViewById(R.id.question_details_edittext)
        questTitle = view.findViewById(R.id.question_title_edittext)
        imageURI = view.findViewById(R.id.image_uri)
        questBody.setText(this.mainActivity.intent.getStringExtra("questionDetails"))
        questTitle.setText(this.mainActivity.intent.getStringExtra("questionTitle"))
        imageURI.text = image_uri

        val submitButton = view.findViewById<Button>(R.id.btn_submit)
        recordVoiceNote = view.findViewById(R.id.voice_note_button)
        playVoiceNote = view.findViewById(R.id.play_note_button)



        //Set up the listeners

        val anonymousSwitch = view.findViewById<Switch>(R.id.anonymous_switch)
        submitButton?.setOnClickListener(submitButtonListener(spinner, anonymousSwitch, coursesList, user))
        setTakeImageListener(view, questBody, questTitle)
        setRecordButtonListener(view)
        setPlayButtonListener(view)
    }


    /**
     * Sets up the listener to open the camera.
     * @param view the current view
     * @param questBody The body of the question
     * @param questTitle The title of the question
     */
    private fun setTakeImageListener(
        view: View,
        questBody: EditText,
        questTitle: EditText
    ) {
        takePictureButton = view.findViewById(R.id.takeImage)
        takePictureButton.setOnClickListener {
            val questionDetails = questBody.text.toString()
            val questionTitle = questTitle.text.toString()
            val intent = Intent(this.mainActivity, CameraActivity::class.java)
            intent.putExtra("questionTitle", questionTitle)
            intent.putExtra("questionDetails", questionDetails)
            startActivity(intent)
        }
    }


    /**
     * Sets up the listener to play a voice record.
     * @param view the current view
     */
    private fun setPlayButtonListener(View: View){

        playVoiceNote.setOnClickListener {
            if (audioPlayer == null) {
                recordVoiceNote.isEnabled = false
                audioPlayer = context?.let { it1 -> AndroidAudioPlayer(it1) }
                recordVoiceNote.isEnabled = true
            }

            if (audioFile != null) {
                recordVoiceNote.isEnabled = false
                audioPlayer?.playFile(audioFile!!.toUri())
                recordVoiceNote.isEnabled = true
            }


        }
    }
    /**
     * Sets up the listener to record a voice message.
     * @param view the current view
     */
    private fun setRecordButtonListener(View: View){

        recordVoiceNote.setOnClickListener {
            if (hasRecordAudioPermission()) {

                startRecording()
            }
            else {
                requestRecordAudioPermission()
            }
        }
     }


    private fun startRecording(){
        if (audioRecorder == null) {
            audioRecorder = context?.let { it1 -> AndroidAudioRecorder(it1) }
        }
        if (audioRecorder?.recorder == null) {

            val fileName = "recording.3gp"
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            audioFile = File(storageDir, fileName)
            isRecording = true
            audioRecorder?.start(audioFile!!)
            updateRecordButtonText()

        } else {
            audioRecorder?.stop()
            isRecording = false
            updateRecordButtonText()
        }
    }


    private fun hasRecordAudioPermission(): Boolean {
        val permission = Manifest.permission.RECORD_AUDIO
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordAudioPermission() {
        val permission = Manifest.permission.RECORD_AUDIO
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), REQUEST_RECORD_AUDIO_PERMISSION)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform the action
                startRecording()
            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }


    /**
     * Modifies the button text depending on the app's state.
     */
    private fun updateRecordButtonText() {
        if (isRecording) {
            recordVoiceNote.text = "Stop Recording"
            playVoiceNote.isEnabled = false

        } else {
            recordVoiceNote.text = "Start New Recording"
            playVoiceNote.isEnabled = true

        }
    }


    /**
     * Submits a question
     * @param spinner the dropdown menu containing the chose title
     * @param anonymousSwitch the users choiceconcerning anonymity
     * @param coursesList the list of available courses
     * @param user the logged in user
     */
    private fun submitButtonListener(
        spinner: Spinner,
        anonymousSwitch: Switch,
        coursesList: List<Model.Course>,
        user: Model.User?
    ): (v: View) -> Unit = {

        // If the user is not logged in, show a message and don't submit the question
        if (user == null) {
            Toast.makeText(
                requireContext(),
                "You must be logged in to post a question",
                Toast.LENGTH_SHORT
            ).show()
        }
        // If the question title or body is empty, show a message and don't submit the question
        else if (questBody.text.isBlank() || questTitle.text.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Question title or body cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Get the selected course from the spinner
            val selectedItemPosition = spinner.selectedItemPosition
            if (selectedItemPosition != Spinner.INVALID_POSITION) {
                val questionSubject = spinner.getItemAtPosition(selectedItemPosition) as String
                // Find the course in the list of available courses
                val course =
                    coursesList.firstOrNull { course -> course.courseName == questionSubject }

                // If the course is found, add the question to the database and navigate to the home screen
                if (course != null) {
                    var audioFilePath= "null"
                    if(audioFile != null){
                        audioFilePath = audioFile!!.absolutePath
                    }
                    db.addQuestion(
                        user.userId,
                        course.courseId,
                        anonymousSwitch.isChecked,
                        questTitle.text.toString(),
                        questBody.text.toString(),
                        imageURI.text.toString(),
                        audioFilePath
                    ).thenAccept {

                        mainActivity.replaceFragment(HomeFragment())
                    }
                }
            }
        }
    }
}