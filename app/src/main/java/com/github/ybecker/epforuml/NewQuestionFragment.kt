package com.github.ybecker.epforuml

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.github.ybecker.epforuml.sensor.CameraActivity
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CompletableFuture

/**
 * A simple [Fragment] subclass.
 * Use the [NewQuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewQuestionFragment(val mainActivity: MainActivity) : Fragment() {

    private lateinit var questBody : EditText
    private lateinit var questTitle : EditText
    private lateinit var imageURI: TextView
    private lateinit var takePictureButton: Button
    private lateinit var image_uri : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val user = DatabaseManager.user
        val view = inflater.inflate(R.layout.fragment_new_question, container, false)

        image_uri = this.mainActivity.intent.getStringExtra("uri").toString()
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
            setUpArgs(view,spinner,coursesList,user)
        }
        return view

        }

    private fun setUpArgs(
        view: View,
        spinner: Spinner,
        coursesList: List<Model.Course>,
        user: Model.User?,
    ) {
        setUpArgs(view)
        val submitButton = view.findViewById<Button>(R.id.btn_submit)
        submitButton?.setOnClickListener(submitButtonListener(spinner, coursesList, user))
        setTakeImage(view, questBody, questTitle)
    }

    private fun submitButtonListener(
        spinner: Spinner,
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
        }
        else {
            // Get the selected course from the spinner
            val selectedItemPosition = spinner.selectedItemPosition
            if (selectedItemPosition != Spinner.INVALID_POSITION) {
                val questionSubject = spinner.getItemAtPosition(selectedItemPosition) as String
                // Find the course in the list of available courses
                val course = coursesList.firstOrNull { course -> course.courseName == questionSubject }

                // If the course is found, add the question to the database and navigate to the home screen
                if (course != null) {
                    if(imageURI.text=="null"){
                        db.addQuestion(
                            user.userId,
                            course.courseId,
                            questTitle.text.toString(),
                            questBody.text.toString(),
                            imageURI.text.toString()
                        ).join()
                    }else{
                        db.addQuestionWithUri(
                            user.userId,
                            course.courseId,
                            questTitle.text.toString(),
                            questBody.text.toString(),
                            imageURI.text.toString(),
                            this.mainActivity
                        ).join()
                    }

                    //mainActivity.intent.extras.
                    mainActivity.replaceFragment(HomeFragment())
                }
            }
        }
    }
    private fun setTakeImage(
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

    private fun setUpArgs(view: View): Triple<EditText, EditText, TextView> {
         questBody = view.findViewById(R.id.question_details_edittext)
         questTitle = view.findViewById(R.id.question_title_edittext)
         imageURI = view.findViewById(R.id.image_uri)
        questBody.setText(this.mainActivity.intent.getStringExtra("questionDetails"))
        questTitle.setText(this.mainActivity.intent.getStringExtra("questionTitle"))
        imageURI.text = image_uri
        return Triple(questBody, questTitle, imageURI)
    }


}