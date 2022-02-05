package hu.petrik.quizion.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import hu.petrik.quizion.R
import hu.petrik.quizion.activities.QuizzesActivity
import hu.petrik.quizion.activities.RegisterActivity
import hu.petrik.quizion.components.ViewSwapper
import hu.petrik.quizion.database.SQLConnector
import hu.petrik.quizion.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class LoginFragment : Fragment() {

    private lateinit var bind: FragmentLoginBinding
    private var loginInProgress = false

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentLoginBinding.inflate(inflater, container, false)
        init()
        return bind.root
    }

    private fun init() {
        bind.buttonLogin.setOnClickListener {
            login(
                activity as Context,
                bind.textInputUID.editText!!.text.toString(),
                bind.textInputPassword.editText!!.text.toString()
            )
        }
        bind.buttonRegister.setOnClickListener {
            ViewSwapper.swapActivity(activity as Context, RegisterActivity())
        }
        bind.textInputPassword.editText!!.setOnLongClickListener {
            bind.textInputPassword.editText!!.text = "".toEditable()
            bind.textInputPassword.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            false
        }
        try {
            val arguments = requireArguments()
            if (arguments!!.containsKey("userID")
                && arguments!!.containsKey("password")
            ) {
                val userID = arguments!!.getString("userID")!!
                val password = arguments!!.getString("password")!!
                fillFromRegister(userID, password)
            }
        }
        catch (e:IllegalStateException){}
    }

    private fun fillFromRegister(userID: String, password: String) {
        bind.textInputUID.editText!!.text = userID.toEditable()
        with(bind.textInputPassword) {
            bind.textInputPassword.endIconMode = TextInputLayout.END_ICON_NONE
            editText!!.text = password.toEditable()
            editText!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) {
                        this@with.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                        this@with.editText!!.removeTextChangedListener(this)
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                }
            })
        }
    }

    private fun login(context: Context, uID: String, password: String) {
        if (loginInProgress) {
            return
        }
        loginInProgress = true
        val params = JSONObject()
        params.put("userID", uID)
        params.put("password", password)
        var result: ArrayList<String> = ArrayList()
        runBlocking {
            try {
                val run = launch {
                    result = SQLConnector.serverCall("POST", "users/login", params)
                }
                run.join()
                if (result[0].startsWith("2")) {
                    val remember_token = JSONObject(result[1]).getString("remember_token")
                    val uName = JSONObject(result[1]).getString("userName")
                    val token = JSONObject(result[1]).getString("token")
                    ViewSwapper.swapActivity(
                        context,
                        QuizzesActivity(),
                        Pair("Token", token),
                        finish = false
                    )
                    Log.d(getString(R.string.state), getString(R.string.login_successful))
                } else {
                    Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_SHORT)
                        .show()
                    Log.d(getString(R.string.state), result[0])
                }
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                Log.d(getString(R.string.state), getString(R.string.login_failed))
            }
            return@runBlocking
        }
        loginInProgress = false
    }
}