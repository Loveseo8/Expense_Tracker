package com.akree.expensetracker.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.akree.expensetracker.R;
import com.akree.expensetracker.databinding.FragmentStatisticsBinding;
import com.akree.expensetracker.models.ExpensesViewModel;
import com.akree.expensetracker.serialization.Expense;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.common.util.CollectionUtils;

import java.text.DateFormatSymbols;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
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

        initChars();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initChars() {
        binding.sfOutcomesChart.getXAxis().setTextColor(getSecondaryColor());
        binding.sfOutcomesChart.getAxisLeft().setTextColor(getSecondaryColor());
        binding.sfOutcomesChart.getAxisRight().setTextColor(getSecondaryColor());
        binding.sfOutcomesChart.getDescription().setEnabled(false);
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

        int daysPerMonth = (new GregorianCalendar(currentYear, currentMonth, 1))
                .getActualMaximum(Calendar.DAY_OF_MONTH);

        ArrayList<Double> days = new ArrayList<>(daysPerMonth);
        for (int i = 0; i < daysPerMonth; i++) days.add(i, 0.0);

        double totalOutcomes = 0.0;
        for (Expense expense: perMonth) {
            totalOutcomes += expense.getAmount();

            int expenseDay = Integer.parseInt(expense.getDate().split("\\.")[0]);
            days.set(expenseDay, expense.getAmount() + days.get(expenseDay));
        }
        binding.sfTotalOutcomesMsg.setText(Double.valueOf(totalOutcomes).toString());

        ArrayList<Entry> outcomes = new ArrayList<>();
        for (int i = 0; i < daysPerMonth; i++) {
            outcomes.add(i, new Entry(i + 1, days.get(i).floatValue()));
        }

        LineDataSet outcomesDataSet = new LineDataSet(outcomes, "Real outcomes");
        outcomesDataSet.setMode(LineDataSet.Mode.LINEAR);
        outcomesDataSet.setDrawCircles(false);
        outcomesDataSet.setColor(getPrimaryColor());
        outcomesDataSet.setDrawValues(false);

        binding.sfOutcomesChart.setData(new LineData(outcomesDataSet));
        binding.sfOutcomesChart.invalidate();
    }

    private int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    private int getSecondaryColor() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
        return typedValue.data;
    }
}