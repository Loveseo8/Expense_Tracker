package com.akree.expensetracker.ui.profile

import android.R.attr.data
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.akree.expensetracker.Authorization
import com.akree.expensetracker.User
import com.akree.expensetracker.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import io.getstream.avatarview.coil.loadImage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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

        connectActionHandlers()

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
                FirebaseStorage.getInstance()
                    .reference.child(user.profilePicture)
                    .downloadUrl.addOnSuccessListener {
                        binding!!.pfAvatarView.avatarInitials = ""
                        binding!!.pfAvatarView.loadImage(it)
                    }
            }
        }
    }

    private fun connectActionHandlers() {
        binding?.pfLogoutBtn!!.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(activity, Authorization::class.java))
            activity?.finish()
        }

        binding?.pfAvatarView!!.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK

            resultLauncher.launch(intent)
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result?.data?.data
            val inputStream = context?.contentResolver?.openInputStream(uri!!)

            try {
                val bitmap = BitmapFactory.decodeStream(inputStream)

                binding?.pfAvatarView?.avatarInitials = ""
                binding!!.pfAvatarView.loadImage(bitmap)

                val link = "images/" + UUID.randomUUID()

                FirebaseStorage.getInstance()
                    .reference.child(link)
                    .putFile(uri!!).addOnSuccessListener { snapshot ->
                        snapshot.storage.downloadUrl.addOnSuccessListener {
                            FirebaseDatabase.getInstance()
                                .getReference("user/" + FirebaseAuth.getInstance().currentUser!!.uid + "/profilePicture")
                                .setValue(link).addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Uploading completed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Uploading failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Something went wrong",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } finally {
                inputStream?.close()
            }
        }
    }
}