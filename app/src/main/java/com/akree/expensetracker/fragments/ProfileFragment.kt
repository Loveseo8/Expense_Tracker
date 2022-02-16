package com.akree.expensetracker.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.akree.expensetracker.AuthorizationActivity
import com.akree.expensetracker.R
import com.akree.expensetracker.serialization.User
import com.akree.expensetracker.databinding.FragmentProfileBinding
import com.akree.expensetracker.models.ProfileViewModel
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import io.getstream.avatarview.coil.loadImage
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

            binding!!.pfCategoriesGroup.removeAllViews()
            for (category in user.categories) {
                val chip = LayoutInflater.from(requireContext())
                    .inflate(
                        R.layout.layout_chip_entry,
                        binding!!.pfCategoriesGroup,
                        false
                    ) as Chip

                chip.text = category
                binding!!.pfCategoriesGroup?.addView(chip)
            }
        }
    }

    private fun connectActionHandlers() {
        binding?.pfLogoutBtn!!.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(activity, AuthorizationActivity::class.java))
            activity?.finish()
        }

        binding?.pfAvatarView!!.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK

            resultLauncher.launch(intent)
        }

        binding?.pfAddCategoryBtn!!.setOnClickListener {
            val dialogBuilder = android.app.AlertDialog.Builder(requireContext())
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_category_dialog, null)
            dialogBuilder.setView(dialogView)

            val dialog = dialogBuilder.create()

            val addBtn = dialogView.findViewById<Button>(R.id.button_add_c)
            addBtn.setOnClickListener {
                val categoryName = dialogView.findViewById<EditText>(R.id.category_name_et).text.toString()

                if (categoryName.isNotEmpty()) {
                    val categoryList = viewModel?.user?.value!!.categories
                    categoryList.add(categoryName)

                    FirebaseDatabase.getInstance()
                        .getReference("user/" + FirebaseAuth.getInstance().currentUser!!.uid + "/categories")
                        .setValue(categoryList)
                }

                dialog.dismiss()
            }

            dialog.show()
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