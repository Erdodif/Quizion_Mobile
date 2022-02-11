package hu.petrik.quizion.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import androidx.core.view.children
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hu.petrik.quizion.R
import hu.petrik.quizion.activities.LoginActivity
import hu.petrik.quizion.databinding.FragmentLoginTokenBinding

class LoginTokenFragment(var users: List<Pair<String, String>>) : Fragment() {
    lateinit var binding: FragmentLoginTokenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginTokenBinding.inflate(inflater, container, false)
        for (user in users) {
            Log.d("Remember token", user.first + " : " + user.second)
            val spacer = Space(context)
            val layoutParams = LinearLayout.LayoutParams(0, 0,1F)
            spacer.layoutParams = layoutParams
            val profileButton =
                MaterialButton(ContextThemeWrapper(requireContext(), R.style.action_button))
            profileButton.text = user.first
            profileButton.textSize = 20F
            profileButton.width = LinearLayout.LayoutParams.WRAP_CONTENT
            profileButton.height = LinearLayout.LayoutParams.WRAP_CONTENT
            profileButton.setOnClickListener {
                (activity as LoginActivity).login(user.first, user.second, loginViaToken = true)
            }
            profileButton.setOnLongClickListener {
                val alertBuilder = MaterialAlertDialogBuilder(context as Context)
                alertBuilder.background!!.setTint(requireActivity().getColor(R.color.textColorPrimary))
                alertBuilder.setTitle(requireActivity().getString(R.string.forget_profile))
                alertBuilder.setMessage(requireActivity().getString(R.string.sure_forget))
                alertBuilder.setNegativeButton(requireActivity().getString(R.string.no), null)
                alertBuilder.setPositiveButton(requireActivity().getString(R.string.yes)) { _, _ ->
                    (activity as LoginActivity).forgetRememberToken(user.first)
                    (activity as LoginActivity).selectRelevantMethod()
                }
                alertBuilder.show()
                true
            }
            binding.layoutUsers.addView(profileButton)
            binding.layoutUsers.addView(spacer)
        }
        binding.buttonAlternativeLogin.setOnClickListener {
            (activity as LoginActivity).startFragment(LoginFragment(""), false)
        }
        return binding.root
    }

}