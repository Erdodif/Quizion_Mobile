package hu.petrik.quizion.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import hu.petrik.quizion.R
import hu.petrik.quizion.activities.LoginActivity
import hu.petrik.quizion.database.SQLConnector
import hu.petrik.quizion.databinding.FragmentEmailVerificationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.ArrayList

class EmailVerificationFragment(
    var uID: String,
    var password: String,
    var rememberLogin: Boolean
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val bind = FragmentEmailVerificationBinding.inflate(layoutInflater)

        bind.buttonLogin.setOnClickListener {
            (activity as LoginActivity).login(uID, password, rememberLogin)
        }

        bind.buttonCheckMailbox.setOnClickListener {
            openMailbox()
        }

        bind.buttonResendEmail.setOnClickListener {
            runBlocking {
                withContext(Dispatchers.IO) {
                    resendEmail()
                }
            }
        }

        return bind.root
    }

    fun openMailbox() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_EMAIL)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            requireActivity().startActivity(intent)
            requireActivity().finish()
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), R.string.error, Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun resendEmail() {
        val params = JSONObject()
        if (rememberLogin) {
            params.put("remember_token", password)
        } else {
            params.put("userID", uID)
            params.put("password", password)
        }
        val result = SQLConnector.serverCall("POST", "users/resendemail", params)
        if (result[0].startsWith("2")){
            requireActivity().runOnUiThread {
                Toast.makeText(
                    activity,
                    requireActivity().getString(R.string.email_sent),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        else{
            requireActivity().runOnUiThread {
                Toast.makeText(
                    activity,
                    requireActivity().getString(R.string.error_email_send),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}