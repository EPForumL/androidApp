package com.github.ybecker.epforuml

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {


    private lateinit var btnToggleDark:Button;
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        btnToggleDark = view.findViewById(R.id.switchDark)
            btnToggleDark.setOnClickListener {
                if(btnToggleDark.isActivated)
                    AppCompatDelegate
                        .setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES)
                else
                    AppCompatDelegate
                        .setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO)
            }
        return view
    }
}