package com.akree.expensetracker.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.akree.expensetracker.R;
import com.akree.expensetracker.User;
import com.akree.expensetracker.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding = null;
    private ProfileViewModel viewModel = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                updateDataFromUser(user);
            }
        });
        updateDataFromUser(viewModel.getUser().getValue());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateDataFromUser(User user) {
        if (user != null) {
            binding.pfUserNameMsg.setText(user.getUsername());
            binding.pfUserEmailMsg.setText(user.getEmail());

            String [] nameParts = user.getUsername().split("\"\\\\s+\"");

            StringBuilder nameInitials = new StringBuilder();
            for (String part: nameParts ) { nameInitials.append(part.charAt(0)); }

            binding.pfAvatarView.setAvatarInitials(nameInitials.toString().toUpperCase());
        }
    }
}