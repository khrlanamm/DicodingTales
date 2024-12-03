package com.khrlanamm.dicodingtales.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.khrlanamm.dicodingtales.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailTextView.setupFadeIn(1000, 0)
        binding.edLoginEmail.setupFadeIn(1000, 200)
        binding.emailEditTextLayout.setupFadeIn(1000, 200)
        binding.edLoginPassword.setupFadeIn(1000, 400)
        binding.passwordTextView.setupFadeIn(1000, 400)
        binding.PasswordEditTextLayout.setupFadeIn(1000, 600)
        binding.btnLogin.setupFadeIn(1000, 800)
    }

    private fun View.setupFadeIn(duration: Long, startDelay: Long = 0L) {
        this.alpha = 0f
        this.animate().alpha(1f).setDuration(duration).setStartDelay(startDelay).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
