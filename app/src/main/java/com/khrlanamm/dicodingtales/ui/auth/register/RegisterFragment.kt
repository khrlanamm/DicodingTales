package com.khrlanamm.dicodingtales.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.khrlanamm.dicodingtales.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    private fun View.setupFadeIn(duration: Long, startDelay: Long = 0L) {
        this.alpha = 0f
        this.animate().alpha(1f).setDuration(duration).setStartDelay(startDelay).start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameTextView.setupFadeIn(1000, 0)
        binding.nameEditTextLayout.setupFadeIn(1000, 200)
        binding.edRegisterName.setupFadeIn(1000, 200)
        binding.emailTextView.setupFadeIn(1000, 400)
        binding.emailEditTextLayout.setupFadeIn(1000, 600)
        binding.edRegisterEmail.setupFadeIn(1000, 600)
        binding.progressBar.setupFadeIn(1000, 800)
        binding.txtPassword.setupFadeIn(1000, 1000)
        binding.passwordEditTextLayout.setupFadeIn(1000, 1200)
        binding.edRegisterPassword.setupFadeIn(1000, 1200)
        binding.btnRegister.setupFadeIn(1000, 1400)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
