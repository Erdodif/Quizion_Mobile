package hu.petrik.quizion.activities

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import hu.petrik.quizion.R
import hu.petrik.quizion.database.SQLConnector
import hu.petrik.quizion.databinding.ActivityRegisterBinding
import hu.petrik.quizion.components.ViewSwapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.lang.StringBuilder

class RegisterActivity : AppCompatActivity() {
    private var registerInProgress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.navigationBarColor = this.resources.getColor(R.color.secondary)
        super.onCreate(savedInstanceState)
        val bind = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.buttonBack.setOnClickListener {
            ViewSwapper.swapActivity(this, LoginActivity())
        }
        bind.buttonRegister.setOnClickListener {
            Log.d("RegisterButtonState", "Pressed")
            if (validate(bind)) {
                register(this, bind)
            }
        }
    }

    private fun validateName(binding: ActivityRegisterBinding): Boolean {
        val name = binding.textInputUserName.editText!!.text.toString()
        val constraintWordCharacter = Regex("""[^\p{javaLetterOrDigit}]""")
        val context = binding.root.context
        when {
            name.isEmpty() -> {
                binding.textInputUserName.error =
                    context.getString(R.string.uname_field_required)
            }
            name.length < 6 -> {
                binding.textInputUserName.error =
                    context.getString(R.string.uname_min_6)
            }
            name.length > 25 -> {
                binding.textInputUserName.error =
                    context.getString(R.string.uname_max_25)
            }
            constraintWordCharacter.containsMatchIn(name) -> {
                binding.textInputUserName.error =
                    context.getString(R.string.uname_alphanumeric)
            }
            else -> {
                binding.textInputUserName.error = null
                return true
            }
        }
        return false
    }

    private fun validateEmail(binding: ActivityRegisterBinding): Boolean {
        val email = binding.textInputEmail.editText!!.text.toString()
        val context = binding.root.context
        when {
            email.length < 6 -> {
                binding.textInputEmail.error =
                    context.getString(R.string.email_field_required)
            }
            email.length > 40 -> {
                binding.textInputEmail.error =
                    context.getString(R.string.email_too_long)
            }
            !email.contains('@') || !email.contains('.') -> {
                binding.textInputEmail.error =
                    context.getString(R.string.email_invalid)
            }
            else -> {
                binding.textInputEmail.error = null
                return true
            }
        }
        return false
    }

    private fun validatePassword(binding: ActivityRegisterBinding): Boolean {
        Log.d("ValidationState", "Started")
        var passwordValid = true
        val password = binding.textInputPassword.editText!!.text.toString()
        val context = binding.root.context
        val passwordErrors = ArrayList<String>()
        when {
            password.isEmpty() -> {
                binding.textInputPassword.error =
                    context.getString(R.string.password_field_required)
                passwordValid = false
            }
            password.length < 8 -> {
                binding.textInputPassword.error =
                    context.getString(R.string.password_field_min_8)
                passwordValid = false
            }
            password.length > 64 -> {
                binding.textInputPassword.error =
                    context.getString(R.string.password_field_max_64)
                passwordValid = false
            }
            else -> {
                val constraintLowerCase = Regex("""\p{javaLowerCase}+""")
                val constraintUpperCase = Regex("""\p{javaUpperCase}+""")
                val constraintNumberOrSpecial = Regex("""[\p{Digit}\W]+""")
                if (!constraintLowerCase.containsMatchIn(password)) {
                    passwordErrors.add(context.getString(R.string.validation_lowercase))
                    passwordValid = false
                }
                if (!constraintUpperCase.containsMatchIn(password)) {
                    passwordErrors.add(context.getString(R.string.validation_uppercase))
                    passwordValid = false
                }
                if (!constraintNumberOrSpecial.containsMatchIn(password)) {
                    passwordErrors.add(context.getString(R.string.validation_number_or_special))
                    passwordValid = false
                }
                if (!passwordValid) {
                    val passwordErrorMessage = StringBuilder()
                    for (i in 0 until passwordErrors.size) {
                        passwordErrorMessage.append(passwordErrors[i])
                        if (i < passwordErrors.size - 1) {
                            passwordErrorMessage.append(", ")
                            if (i == passwordErrors.size - 2) {
                                passwordErrorMessage.append(context.getString(R.string.validation_and))
                            }
                        }
                    }
                    binding.textInputPassword.error =
                        "${context.getString(R.string.validation_must)}$passwordErrorMessage${
                            context.getString(
                                R.string.validation_char
                            )
                        }!"
                }
            }
        }
        if (passwordValid) {
            binding.textInputPassword.error = null
        }
        return passwordValid
    }

    private fun validatePasswordMatch(binding: ActivityRegisterBinding): Boolean {
        val password = binding.textInputPassword.editText!!.text.toString()
        val passwordAgain = binding.textInputPasswordAgain.editText!!.text.toString()
        var passwordValid = true
        if (password != passwordAgain) {
            binding.textInputPasswordAgain.error =
                (binding.root.context as Activity).getString(R.string.passwords_not_same)
            passwordValid = false
        }
        if (passwordValid) {
            binding.textInputPasswordAgain.error = null
        }
        return passwordValid
    }

    private fun validate(
        binding: ActivityRegisterBinding
    ): Boolean {
        Log.d("ValidationState", "Started")
        val pass = validatePassword(binding) && validatePasswordMatch(binding)
        val name = validateName(binding)
        val email = validateEmail(binding)
        return pass && name && email
    }


    private fun register(context: Context, binding: ActivityRegisterBinding) {
        if (registerInProgress) {
            return
        }
        registerInProgress = true
        val params = JSONObject()
        params.put("name", binding.textInputUserName.editText!!.text.toString())
        params.put("email", binding.textInputEmail.editText!!.text.toString())
        params.put("password", binding.textInputPassword.editText!!.text.toString())
        runBlocking {
            launch {
                val result = SQLConnector.serverCall("POST", "users/register", params)
                if (result[0].startsWith('2')) {
                    Toast.makeText(
                        binding.root.context,
                        context.getString(R.string.register_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                    ViewSwapper.swapActivity(
                        this@RegisterActivity,
                        LoginActivity(),
                        Pair("userID", params.getString("name")),
                        Pair("password", params.getString("password"))
                    )
                }
            }.join()
            registerInProgress = false
        }
    }
}