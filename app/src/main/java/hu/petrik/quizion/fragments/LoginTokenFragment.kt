package hu.petrik.quizion.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import com.google.android.material.button.MaterialButton
import hu.petrik.quizion.R
import hu.petrik.quizion.activities.LoginActivity
import hu.petrik.quizion.databinding.FragmentLoginBinding
import hu.petrik.quizion.databinding.FragmentLoginTokenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginTokenFragment(var users: List<Pair<String, String>>) : Fragment() {
    lateinit var binding :FragmentLoginTokenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginTokenBinding.inflate(inflater, container, false)
        for (user in users){
            val spacer = Space(context)
            val layoutParams = LinearLayout.LayoutParams(0,0)
            layoutParams.weight = 1F
            spacer.layoutParams = layoutParams
            val profileButton = MaterialButton(requireContext(),null, R.style.action_button)
            profileButton.text = user.first
            profileButton.setOnClickListener {
                CoroutineScope(context as CoroutineContext).launch {
                    (activity as LoginActivity).login(user.first,user.second, loginViaToken = true)
                }
            }
            binding.layoutUsers.addView(profileButton)
            binding.layoutUsers.addView(spacer)
        }
        return binding.root
    }

}