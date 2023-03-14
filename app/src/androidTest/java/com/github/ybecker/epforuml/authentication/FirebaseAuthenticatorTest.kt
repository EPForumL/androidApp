package com.github.ybecker.epforuml.authentication

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ybecker.epforuml.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FirebaseAuthenticatorTest {
    @Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test() {

    }
}