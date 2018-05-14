package com.home.nattapop.todo.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.home.nattapop.todo.R;
import com.home.nattapop.todo.databinding.ActivityMainBinding;
import com.home.nattapop.todo.fragment.MainFragment;

/**
 * Created by Nattapop 13 May 2018
 */
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, MainFragment.newInstance())
                .commit();
    }
}

