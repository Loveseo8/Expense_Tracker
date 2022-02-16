package com.akree.expensetracker.fragments;

import android.net.MacAddress;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akree.expensetracker.MAdapter;
import com.akree.expensetracker.R;
import com.akree.expensetracker.serialization.Expense;
import com.akree.expensetracker.serialization.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExpensesFragment extends Fragment {

    ArrayList<Expense> expenses = new ArrayList<>();
    double budget;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    MAdapter mAdapter;
    TextView bud;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bud = view.findViewById(R.id.ef_current_balance_msg);
        recyclerView = view.findViewById(R.id.ef_expenses_rv);
        getUserData();
        getExpensesData();


        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                getUserData();
                getExpensesData();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                getUserData();
                getExpensesData();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                getUserData();
                getExpensesData();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                getUserData();
                getExpensesData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                getUserData();
                getExpensesData();

            }
        });

    }

    private void getUserData(){

        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                budget = snapshot.getValue(User.class).getBudget();
                bud.setText(String.valueOf(budget) + " ₽");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getExpensesData(){

        expenses.clear();

        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()){

                    expenses.add(ds.getValue(Expense.class));

                }

                recyclerView.setHasFixedSize(false);
                mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new MAdapter(getContext(), expenses);
                recyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}