package com.akree.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.akree.expensetracker.MAdapter;
import com.akree.expensetracker.databinding.FragmentExpensesBinding;
import com.akree.expensetracker.models.ExpensesViewModel;
import com.akree.expensetracker.serialization.Expense;

import java.util.ArrayList;
import java.util.Map;

public class ExpensesFragment extends Fragment {
    private FragmentExpensesBinding binding = null;
    private ExpensesViewModel viewModel = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpensesBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(ExpensesViewModel.class);
        viewModel.getExpenses().observe(getViewLifecycleOwner(), new Observer<Map<String, Expense>>() {
            @Override
            public void onChanged(Map<String, Expense> stringExpenseMap) {
                updateDataFromViewModel();
            }
        });
        updateDataFromViewModel();

        return binding.getRoot();
    }

    private void updateDataFromViewModel() {
        binding.efCurrentBalanceMsg.setText(viewModel.getBudget().getValue().toString());

        ArrayList<Expense> expenses = new ArrayList<>(viewModel.getExpenses().getValue().values());

        binding.efExpensesRv.setHasFixedSize(false);

        binding.efExpensesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.efExpensesRv.setAdapter(new MAdapter(getContext(), expenses));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}