package com.khrlanamm.dicodingtales.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
                    val name = result.data.loginResult.name
                    lifecycleScope.launch {
                        sessionManager.saveAuthToken(token)
                    }
                    showWelcomeDialog(name)
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showWelcomeDialog(name: String) {
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.welcome, name))
            .setMessage(getString(R.string.login_success))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.setOnDismissListener {
            navigateToMainActivity()

            Handler(Looper.getMainLooper()).postDelayed({
                navigateToMainActivity()
            }, 100)
        }
        dialog.show()
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
        with(binding) {
            emailTextView.setupFadeIn(0)
            edLoginEmail.setupFadeIn(200)
            emailEditTextLayout.setupFadeIn(200)
            edLoginPassword.setupFadeIn(400)
            passwordTextView.setupFadeIn(400)
            PasswordEditTextLayout.setupFadeIn(600)
            btnLogin.setupFadeIn(800)
        }
    }

    private fun View.setupFadeIn(startDelay: Long = 0L) {
        val duration = 1000L
        this.alpha = 0f
        this.animate().alpha(1f).setDuration(duration).setStartDelay(startDelay).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}
