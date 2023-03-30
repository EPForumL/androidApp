package com.github.ybecker.epforuml

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

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

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageURI.setText(uri.toString())
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val user = DatabaseManager.user ?: Model.User()
        val view = inflater.inflate(R.layout.fragment_new_question, container, false)

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
        seUploadImage(view)
        setTakeImage(view, questBody, questTitle)
    }

    private fun submitButtonListener(
        spinner: Spinner,
        coursesList: List<Model.Course>,
        user: Model.User?
    ): (v: View) -> Unit = {
        if (questBody.text.isBlank() || questTitle.text.isBlank()) {
            Toast.makeText(
                requireContext(), "Question title or body cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val questionSubject = parent.getItemAtPosition(position) as String
                    val course =
                        coursesList.filter { course -> course.courseName == questionSubject }[0]
                    if (user != null) {
                        db.addQuestion(
                            user.userId,
                            course.courseId,
                            questTitle.toString(),
                            questBody.toString(),
                            imageURI.toString()
                        )
                    }


                    mainActivity.replaceFragment(HomeFragment(mainActivity))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        mainActivity.replaceFragment(HomeFragment(this.mainActivity))
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

    private fun seUploadImage(view: View?) {
        val uploadButton = view?.findViewById<Button>(R.id.uploadButton)
        uploadButton?.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private fun setUpArgs(view: View): Triple<EditText, EditText, TextView> {
         questBody = view.findViewById(R.id.question_details_edittext)
         questTitle = view.findViewById(R.id.question_title_edittext)
         imageURI = view.findViewById(R.id.image_uri)

        questBody.setText(this.mainActivity.intent.getStringExtra("questionDetails"))
        questTitle.setText(this.mainActivity.intent.getStringExtra("questionTitle"))
        imageURI.text = this.mainActivity.intent.getStringExtra("uri")
        return Triple(questBody, questTitle, imageURI)
    }
}