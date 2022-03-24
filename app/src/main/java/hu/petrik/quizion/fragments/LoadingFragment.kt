package hu.petrik.quizion.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RawRes
import com.airbnb.lottie.LottieAnimationView
import hu.petrik.quizion.R
import hu.petrik.quizion.databinding.FragmentLoadingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 * Use the [LoadingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoadingFragment(@RawRes var res: Int? = null) : Fragment() {
    lateinit var binding: FragmentLoadingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadingBinding.inflate(layoutInflater)
        if (res !== null) {
            binding.lottieAnimationView.setAnimation(res!!)
        }
        return binding.root
    }

    fun setErrorMessage(errorMessage: String) {
        binding.lottieAnimationView.visibility = LottieAnimationView.GONE
        binding.messageError.visibility = TextView.VISIBLE
        binding.messageError.text = errorMessage
        Log.d("Hiba", errorMessage)
    }

    fun setAnimationSource(@RawRes res: Int) {
        binding.lottieAnimationView.setAnimation(res)
    }
}