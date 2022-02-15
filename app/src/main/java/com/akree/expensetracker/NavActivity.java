package com.akree.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.akree.expensetracker.databinding.ActivityNavBinding;

public class NavActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //add expenses
        setSupportActionBar(binding.appBarNav.toolbar);
        binding.appBarNav.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddExpenseDialog();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_profile, R.id.nav_statistics, R.id.nav_expenses)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showAddExpenseDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.add_expense_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText amount = dialogView.findViewById(R.id.amount);
        final Spinner type_spinner = dialogView.findViewById(R.id.type_spinner);
        final ChipGroup chip_group_categories = dialogView.findViewById(R.id.chip_group_categories);
        LayoutInflater inflater = LayoutInflater.from(this);
        Chip newChip = (Chip) inflater.inflate(R.layout.layout_chip_entry, chip_group_categories, false);
        newChip.setText("aaaa");
        chip_group_categories.addView(newChip);

        String [] types = {"Income", "Outcome"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(arrayAdapter);

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        final AlertDialog b = dialogBuilder.create();
        b.show();

    }
}