package com.github.ybecker.epforuml

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.ybecker.epforuml.authentication.FirebaseAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.ktx.Firebase


/**
 * A simple [Fragment] subclass.
 * Use the [NewQuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewQuestionFragment(val mainActivity: MainActivity) : Fragment() {

    private lateinit var takePictureButton: Button




    var IMAGE_URI = ""
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            IMAGE_URI = uri.toString()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // DataBase

        // Create an instance of the MockDatabase
        //val mockDatabase = DatabaseManager.useMockDatabase()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_question, container, false)

        //user
        val user = DatabaseManager.user


        //Spinner

        // Retrieve the Spinner view
        val spinner = view.findViewById<Spinner>(R.id.subject_spinner)
        //spinner.se = this.mainActivity.intent.getStringExtra("subject")

        // Get the set of available courses from the MockDatabase
        val coursesSet = DatabaseManager.db.availableCourses()

        // Convert the set to an ArrayList
        val coursesList = ArrayList(coursesSet)

        val courseNamesList = coursesList.map { course -> course.courseName }

        // Create an ArrayAdapter with the coursesList as the data source
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            courseNamesList
        )

        // Set the ArrayAdapter as the Spinner adapter
        spinner.adapter = adapter

        //set if not empty

        val questBody = view.findViewById<EditText>(R.id.question_details_edittext)
        val questTitle = view.findViewById<EditText>(R.id.question_title_edittext)
        val imageURI = view.findViewById<TextView>(R.id.image_uri)

        questBody.setText(this.mainActivity.intent.getStringExtra("questionDetails"))
        questTitle.setText(this.mainActivity.intent.getStringExtra("questionTitle"))
        imageURI.setText(this.mainActivity.intent.getStringExtra("uri"))

        // perform submit action
        //SubmitButton

        val submitButton = view?.findViewById<Button>(R.id.btn_submit)
        submitButton?.setOnClickListener {
            print("click submit btn")

            if (questBody.text.isBlank() || questTitle.text.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Question title or body cannot be empty",
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
                        val questionSubject= parent.getItemAtPosition(position) as String

                        //find course correponding to the selected name
                        val course =
                            coursesList.filter { course -> course.courseName == questionSubject }[0]
                        if (user != null) {
                            DatabaseManager.db.addQuestion(
                                user,
                                course,
                                questTitle.toString(),
                                imageURI.toString()
                            )
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Do nothing
                    }
                }

                mainActivity.replaceFragment(HomeFragment(mainActivity), "HomeFragment")
            }

        }

        //select image
        val uploadButton = view?.findViewById<Button>(R.id.uploadButton)
        uploadButton?.setOnClickListener {
            pickImage.launch("image/*")
        }


        takePictureButton = view.findViewById(R.id.takeImage)

        takePictureButton.setOnClickListener {
            val questionDetails = questBody.text.toString()
            val questionTitle = questTitle.text.toString()

            val intent = Intent(this.mainActivity, CameraActivity::class.java)
            intent.putExtra( "questionTitle",questionTitle)
            intent.putExtra( "questionDetails",questionDetails)

            startActivity(intent)
        }

        return view

    }



}



