package com.khrlanamm.dicodingtales.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.khrlanamm.dicodingtales.MainActivity
import com.khrlanamm.dicodingtales.R
import com.khrlanamm.dicodingtales.data.Result
import com.khrlanamm.dicodingtales.data.local.pref.SessionManager
import com.khrlanamm.dicodingtales.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        LoginFactory.getInstance(requireContext())
    }

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        setupFadeIn()

        observeViewModel()

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            if (isInputValid(email, password)) {
                viewModel.login(email, password)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val token = result.data.loginResult.token
                    lifecycleScope.launch {
                        sessionManager.saveAuthToken(token)
                    }
                    Toast.makeText(requireContext(), result.data.message, Toast.LENGTH_SHORT).show()

                    navigateToMainActivity()
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isInputValid(email: String, password: String): Boolean {
        var isValid = true
        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = getString(R.string.empty_email)
            isValid = false
        } else {
            binding.emailEditTextLayout.error = null
        }

        if (password.isEmpty()) {
            binding.PasswordEditTextLayout.error = getString(R.string.empty_password)
            isValid = false
        } else {
            binding.PasswordEditTextLayout.error = null
        }

        return isValid
    }

    private fun setupFadeIn() {
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
