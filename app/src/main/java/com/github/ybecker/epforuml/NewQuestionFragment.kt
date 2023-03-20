package com.github.ybecker.epforuml

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.github.ybecker.epforuml.authentication.AuthenticatorManager
import com.github.ybecker.epforuml.database.DatabaseManager


/**
 * A simple [Fragment] subclass.
 * Use the [NewQuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewQuestionFragment(val mainActivity: MainActivity) : Fragment() {

    private var IMAGE_URI = ""
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        IMAGE_URI=uri.toString()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DataBase

        // Create an instance of the MockDatabase
        //val mockDatabase = DatabaseManager.useMockDatabase()

        //user
        val user = AuthenticatorManager.authenticator?.user

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_question, container, false)


        //Spinner

        // Retrieve the Spinner view
        val spinner = view.findViewById<Spinner>(R.id.subject_spinner)

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


        //SubmitButton

        val submitButton = view?.findViewById<Button>(R.id.btn_submit)
        submitButton?.setOnClickListener {
            print("click submit btn")
            val questBody = view.findViewById<EditText>(R.id.question_details_edittext)
            val questTitle = view.findViewById<EditText>(R.id.question_title_edittext)

            if (questBody.text.isBlank() || questTitle.text.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Question title or body cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // perform submit action


                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedCourse = parent.getItemAtPosition(position) as String

                        //find course correponding to the selected name
                        val course =
                            coursesList.filter { course -> course.courseName == selectedCourse }[0]
                        if (user != null) {
                            DatabaseManager.db.addQuestion(user, course, questTitle.toString(), IMAGE_URI)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Do nothing
                    }
                }

                mainActivity.replaceFragment(HomeFragment(mainActivity), "HomeFragment")
            }

        }

        val uploadButton = view?.findViewById<Button>(R.id.uploadButton)
        uploadButton?.setOnClickListener {
            pickImage.launch("image/*")
        }

        return view

    }

}