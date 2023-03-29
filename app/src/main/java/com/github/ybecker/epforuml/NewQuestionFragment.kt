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
import com.github.ybecker.epforuml.authentication.AuthenticatorManager
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model

/**
 * A simple [Fragment] subclass.
 * Use the [NewQuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewQuestionFragment(val mainActivity: MainActivity) : Fragment() {

    private lateinit var takePictureButton: Button
    private var IMAGE_URI = ""
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            IMAGE_URI = uri.toString()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val user = AuthenticatorManager.authenticator?.user
        val view = inflater.inflate(R.layout.fragment_new_question, container, false)

        //Spinner
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
            val (questBody, questTitle, imageURI) = setUp(view)

            setSubmitButton(view, questBody, questTitle, spinner, coursesList, user, imageURI)
            mainActivity.replaceFragment(HomeFragment(mainActivity), "HomeFragment")
            seUploadImage(view)
            setTakeImage(view, questBody, questTitle)
        }
        return view

        }

    private fun setSubmitButton(
        view: View?,
        questBody: EditText,
        questTitle: EditText,
        spinner: Spinner,
        coursesList: List<Model.Course>,
        user: Model.User?,
        imageURI: TextView
    ) {
        val submitButton = view?.findViewById<Button>(R.id.btn_submit)
        submitButton?.setOnClickListener {
            if (questBody.text.isBlank() || questTitle.text.isBlank()) {
                Toast.makeText(requireContext(), "Question title or body cannot be empty",
                    Toast.LENGTH_SHORT).show()
            } else {
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val questionSubject = parent.getItemAtPosition(position) as String
                        val course =coursesList.filter { course -> course.courseName == questionSubject }[0]
                        if (user != null) {
                            db.addQuestion(
                                user.userId,
                                course.courseId,
                                questTitle.toString(),
                                questBody.toString(),
                                imageURI.toString()
                            )
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
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

    private fun seUploadImage(view: View?) {
        val uploadButton = view?.findViewById<Button>(R.id.uploadButton)
        uploadButton?.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private fun setUp(view: View): Triple<EditText, EditText, TextView> {
        val questBody = view.findViewById<EditText>(R.id.question_details_edittext)
        val questTitle = view.findViewById<EditText>(R.id.question_title_edittext)
        val imageURI = view.findViewById<TextView>(R.id.image_uri)

        questBody.setText(this.mainActivity.intent.getStringExtra("questionDetails"))
        questTitle.setText(this.mainActivity.intent.getStringExtra("questionTitle"))
        imageURI.text = this.mainActivity.intent.getStringExtra("uri")
        return Triple(questBody, questTitle, imageURI)
    }
}