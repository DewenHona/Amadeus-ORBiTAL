package com.amadeus.orbital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

       bottomNavigationView.setOnNavigationItemSelectedListener(navListerner);


        }
        private BottomNavigationView.OnNavigationItemSelectedListener  navListerner=new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFrag=new GlobeFragment();

                switch (item.getItemId()){

                    case R.id.nav_home:
                        selectedFrag=new HomeFragment();
                        break;
                    case R.id.nav_globe:
                        selectedFrag=new GlobeFragment();
                        break;
                    case R.id.nav_profile:
                        selectedFrag=new ProfileFragment();
                        break;
                    case R.id.nav_ar:
                        selectedFrag=new ARFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFrag).commit();
                return true;
            }
        };
}

