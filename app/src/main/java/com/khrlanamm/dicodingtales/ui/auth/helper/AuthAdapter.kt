package com.khrlanamm.dicodingtales.ui.auth.helper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.khrlanamm.dicodingtales.ui.auth.login.LoginFragment
import com.khrlanamm.dicodingtales.ui.auth.register.RegisterFragment

class AuthAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment.newInstance()
            1 -> RegisterFragment.newInstance()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
