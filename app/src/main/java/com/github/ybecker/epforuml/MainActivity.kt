package com.github.ybecker.epforuml

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.github.ybecker.epforuml.database.Database
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.Model
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NameAndButton()
        }
    }

    /**
     * Creates the input field for the name and the button to transition to the greeting activity
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun NameAndButton() {
        // We can't use "this" inside the onClick lambda as we would not get the same context
        val context = this

        // Used to align vertically the window components
        Column(content = {
            // This is a standard way to update the TextField when the contents are modified
            var name by remember { mutableStateOf("Your Name") }

            TextField(
                value = name,
                onValueChange = { str -> name = str }
            )

            Button(onClick = {
                val intent = Intent(context, GreetingActivity::class.java)
                // We pass the name entered by the user to the greeting activity
                intent.putExtra("name", name)
                startActivity(intent)
            }) {
                Text("Click me")
            }
        })
    }
}