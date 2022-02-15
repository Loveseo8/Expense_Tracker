package com.akree.expensetracker.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.akree.expensetracker.User
import com.akree.expensetracker.databinding.FragmentProfileBinding
import io.getstream.avatarview.coil.loadImage
import java.lang.StringBuilder
import java.util.*

class ProfileFragment : Fragment() {
    private var binding: FragmentProfileBinding? = null
    private var viewModel: ProfileViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        viewModel!!.user.observe(viewLifecycleOwner) { user -> updateDataFromUser(user) }
        updateDataFromUser(viewModel!!.user.value)

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun updateDataFromUser(user: User?) {
        if (user != null) {
            binding!!.pfUserNameMsg.text = user.username
            binding!!.pfUserEmailMsg.text = user.email

            val nameParts = user.username.split("\"\\\\s+\"").toTypedArray()
            val nameInitials = StringBuilder()
            for (part in nameParts) {
                nameInitials.append(part[0])
            }
            binding!!.pfAvatarView.avatarInitials = nameInitials.toString().uppercase(Locale.getDefault())

            if (user.profilePicture.isNotEmpty()) {
                binding!!.pfAvatarView.loadImage(user.profilePicture)
            }
        }
    }
}