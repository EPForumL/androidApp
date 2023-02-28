package com.github.ybecker.bootcampKotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class GreetingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Greet()
        }
    }

    /**
     * Simply greets the user with the name specified in the main activity
     */
    @Preview
    @Composable
    fun Greet() {
        // We get back the arguments passed from the main activity
        val args = intent.extras as Bundle
        val name = args.getString("name")
        Text(text = "Welcome $name !")
    }
}