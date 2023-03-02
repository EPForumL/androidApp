package com.github.lgodier.webapibootcamp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.room.Room
import com.github.lgodier.webapibootcamp.cache.AppDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button : Button = findViewById(R.id.GetActivityButton);
        button.setOnClickListener(listener)


    }

    private val listener = View.OnClickListener { view ->
        val intent = Intent(view.context, ActActivity::class.java)
        view.context.startActivity(intent);
    }

}