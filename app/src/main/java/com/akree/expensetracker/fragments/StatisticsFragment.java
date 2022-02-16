package com.akree.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.akree.expensetracker.databinding.FragmentStatisticsBinding;
import com.akree.expensetracker.models.ExpensesViewModel;
import com.akree.expensetracker.serialization.Expense;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsFragment extends Fragment {
    private FragmentStatisticsBinding binding = null;
    private ExpensesViewModel viewModel = null;

    private int currentMonth = 0, currentYear = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);

        currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        updateTimePeriod();

        viewModel = new ViewModelProvider(this).get(ExpensesViewModel.class);
        viewModel.getExpenses().observe(getViewLifecycleOwner(), new Observer<Map<String, Expense>>() {
            @Override
            public void onChanged(Map<String, Expense> stringExpenseMap) {
                updateStatisticFromViewModel();
            }
        });
        updateStatisticFromViewModel();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateTimePeriod() {
        binding.sfMonthMsg.setText(new DateFormatSymbols().getMonths()[currentMonth] + " " + Integer.toString(currentYear));
    }

    private void updateStatisticFromViewModel() {
        updateOutcomes();
    }

    private void updateOutcomes() {
        List<Expense> perMonth = viewModel.getExpenses().getValue().values()
                .stream().filter(expense -> {
                    int expenseMonth = Integer.parseInt(expense.getDate().split("\\.")[1]) - 1;
                    int expenseYear = Integer.parseInt(expense.getDate().split("\\.")[2]);
                    return expenseMonth == currentMonth &&
                            expenseYear == currentYear &&
                            expense.getType().equals("Outcome");
                }).collect(Collectors.toList());

        double totalOutcomes = 0.0;
        for (Expense expense : perMonth) {
            totalOutcomes += expense.getAmount();
        }
        binding.sfTotalOutcomesMsg.setText(Double.valueOf(totalOutcomes).toString());
    }
}