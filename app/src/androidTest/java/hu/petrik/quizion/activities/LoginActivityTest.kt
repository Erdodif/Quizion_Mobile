package hu.petrik.quizion.activities

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import hu.petrik.quizion.R
import hu.petrik.quizion.fragments.LoginTokenFragment
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.isA
import java.util.regex.Matcher

class LoginActivityTest {
    @Test
    fun leaveAndResume() {
        val login = launch(LoginActivity::class.java)
        login.moveToState(Lifecycle.State.CREATED)
        login.onActivity {
        //TODO
        }
    }
}