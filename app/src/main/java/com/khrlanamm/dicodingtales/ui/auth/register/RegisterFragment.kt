package com.khrlanamm.dicodingtales.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.khrlanamm.dicodingtales.R
import com.khrlanamm.dicodingtales.data.Result
import com.khrlanamm.dicodingtales.databinding.FragmentRegisterBinding
import com.khrlanamm.dicodingtales.ui.auth.AuthActivity

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels {
        RegisterFactory.getInstance()
    }

    private var isRegistered = false

    private fun View.setupFadeIn(startDelay: Long = 0L) {
        val duration = 1000L
        this.alpha = 0f
        this.animate().alpha(1f).setDuration(duration).setStartDelay(startDelay).start()
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

        with(binding) {
            nameTextView.setupFadeIn(0)
            nameEditTextLayout.setupFadeIn(200)
            edRegisterName.setupFadeIn(200)
            emailTextView.setupFadeIn(400)
            emailEditTextLayout.setupFadeIn(600)
            edRegisterEmail.setupFadeIn(600)
            progressBar.setupFadeIn(800)
            txtPassword.setupFadeIn(1000)
            passwordEditTextLayout.setupFadeIn(1200)
            edRegisterPassword.setupFadeIn(1200)
            btnRegister.setupFadeIn(1400)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            if (isInputValid(name, email, password)) {
                viewModel.register(name, email, password)
                isRegistered = false
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    if (!isRegistered) {
                        val name = binding.edRegisterName.text.toString().trim()
                        showSuccessDialog(name)
                        isRegistered = true
                        binding.edRegisterName.text?.clear()
                        binding.edRegisterEmail.text?.clear()
                        binding.edRegisterPassword.text?.clear()
                    }
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isInputValid(name: String, email: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.nameEditTextLayout.error = getString(R.string.empty_name)
            isValid = false
        } else {
            binding.nameEditTextLayout.error = null
        }

        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = getString(R.string.empty_email)
            isValid = false
        } else {
            binding.emailEditTextLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordEditTextLayout.error = getString(R.string.empty_password)
            isValid = false
        } else {
            binding.passwordEditTextLayout.error = null
        }

        return isValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSuccessDialog(name: String) {
        moveToLoginTab()
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.welcome_register, name))
            .setMessage(getString(R.string.register_success))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun moveToLoginTab() {
        (activity as? AuthActivity)?.getViewPager()?.currentItem = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = RegisterFragment()
    }
}
