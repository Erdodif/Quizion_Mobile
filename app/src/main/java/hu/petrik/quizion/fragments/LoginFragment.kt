package hu.petrik.quizion.fragments

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import hu.petrik.quizion.R
import hu.petrik.quizion.activities.LoginActivity
import hu.petrik.quizion.activities.RegisterActivity
import hu.petrik.quizion.components.ViewSwapper
import hu.petrik.quizion.databinding.FragmentLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginFragment : Fragment() {
    private var tokenLogin = false
    private lateinit var bind: FragmentLoginBinding

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentLoginBinding.inflate(inflater, container, false)
        init()
        return bind.root
    }

    fun clearErrorMessages() {
        bind.textInputPassword.error = null
        bind.textInputUID.error = null
    }

    private fun init() {
        clearErrorMessages()
        bind.textInputUID.setOnClickListener {
            bind.textInputUID.error = null
        }
        bind.textInputPassword.setOnClickListener {
            bind.textInputPassword.error = null
        }
        bind.buttonLogin.setOnClickListener {
            val activity = requireActivity()
            val uID =
                if (tokenLogin) bind.unameLocked.text as String else bind.textInputUID.editText!!.text.toString()
            val pass = bind.textInputPassword.editText!!.text.toString()
            val remember = if (tokenLogin) true else bind.checkboxRemember.isChecked
            var noError = true
            if (uID.isEmpty()) {
                bind.textInputUID.error = activity.getString(R.string.uid_field_missing)
                noError = false
            }
            if (pass.isEmpty()) {
                bind.textInputPassword.error = activity.getString(R.string.password_field_missing)
                noError = false
            }
            if (noError) {
                clearErrorMessages()
                (activity as LoginActivity).login(
                    uID,
                    pass,
                    remember
                )
            }
        }
        bind.buttonRegister.setOnClickListener {
            ViewSwapper.swapActivity(activity as Context, RegisterActivity())
        }
        bind.textInputPassword.editText!!.setOnLongClickListener {
            bind.textInputPassword.editText!!.text = "".toEditable()
            bind.textInputPassword.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            false
        }
        bind.labelRemember.setOnClickListener {
            bind.checkboxRemember.isChecked = !bind.checkboxRemember.isChecked
        }
        bind.buttonCancel.setOnClickListener {
            clearEditor()
        }
        try {
            val arguments = requireArguments()
            if (arguments.containsKey("userID")
                && arguments.containsKey("password")
            ) {
                val userID = arguments.getString("userID")!!
                val password = arguments.getString("password")!!
                fillFromRegister(userID, password)
            }
        } catch (e: IllegalStateException) {
        }
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

    fun fillFromExpiredToken(uName: String) {
        tokenLogin = true
        bind.textInputUID.editText!!.text = uName.toEditable()
        bind.textInputUID.isEnabled = false
        bind.checkboxRemember.isChecked = true
        bind.textInputUID.visibility = MaterialTextView.GONE
        bind.buttonRegister.visibility = MaterialButton.GONE
        bind.checkboxRemember.visibility = MaterialCheckBox.GONE
        bind.labelRemember.visibility = TextView.GONE
        bind.buttonCancel.visibility = MaterialButton.VISIBLE
        bind.tokenLayout.visibility = LinearLayoutCompat.VISIBLE
        bind.unameLocked.text = uName

    }

    fun clearEditor() {
        tokenLogin = false
        bind.textInputUID.editText!!.text = null
        bind.unameLocked.text = null
        bind.textInputUID.isEnabled = true
        bind.checkboxRemember.isChecked = false
        bind.textInputUID.visibility = MaterialTextView.VISIBLE
        bind.buttonRegister.visibility = MaterialButton.VISIBLE
        bind.checkboxRemember.visibility = MaterialCheckBox.VISIBLE
        bind.labelRemember.visibility = TextView.VISIBLE
        bind.buttonCancel.visibility = MaterialButton.GONE
        bind.tokenLayout.visibility = LinearLayoutCompat.GONE
    }
}