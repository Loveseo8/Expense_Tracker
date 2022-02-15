package com.akree.expensetracker.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.akree.expensetracker.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> userData = new MutableLiveData<>(null);

    public ProfileViewModel() {
        FirebaseDatabase.getInstance()
                .getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        userData.setValue(dataSnapshot.getValue(User.class));
                    }
                });
    }

    public LiveData<User> getUser() {
        return userData;
    }

    public void setUser(User user) {
        userData.setValue(user);
    }
}