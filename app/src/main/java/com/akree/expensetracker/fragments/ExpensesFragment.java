package com.akree.expensetracker.fragments;

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
import com.akree.expensetracker.databinding.FragmentExpensesBinding;
import com.akree.expensetracker.models.ExpensesViewModel;
import com.akree.expensetracker.serialization.Expense;

import java.util.List;
import java.util.Map;

public class ExpensesFragment extends Fragment {
    private FragmentExpensesBinding binding = null;
    private ExpensesViewModel viewModel = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpensesBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ExpensesViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel.getExpenses().observe(getViewLifecycleOwner(), new Observer<Map<String, Expense>>() {
            @Override
            public void onChanged(Map<String, Expense> stringExpenseMap) {
                updateDataFromViewModel();
            }
        });
        updateDataFromViewModel();

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateDataFromViewModel() {
        binding.efCurrentBalanceMsg.setText(viewModel.getBudget().getValue().toString());

        List<Expense> expenses = (List<Expense>) viewModel.getExpenses().getValue().values();

    }
}