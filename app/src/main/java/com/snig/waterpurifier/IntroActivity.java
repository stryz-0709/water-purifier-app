package com.snig.waterpurifier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;

import me.relex.circleindicator.CircleIndicator;

public class IntroActivity extends AppCompatActivity {
    /////USERINFO
    String jsonObj;
    UserInfo userInfo;
    public UserInfo getUserInfo() {return userInfo;}
    private TextView textViewSkip;
    private ViewPager viewPager;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            jsonObj = extras.getString("userInfo");
        userInfo = new Gson().fromJson(jsonObj, UserInfo.class);

        textViewSkip = findViewById(R.id.intro_skip);
        viewPager = findViewById(R.id.view_pager);
        RelativeLayout layoutBottom = findViewById(R.id.layout_bottom);
        CircleIndicator circleIndicator = findViewById(R.id.circleIndicator);
        btnNext = findViewById(R.id.btn_next);

        textViewSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(3);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() < 3)viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        IntroAdapter viewPagerAdapter = new IntroAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);
        circleIndicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3){
                    textViewSkip.setVisibility(View.GONE);
                    btnNext.setVisibility(View.GONE);
                }
                else{
                    textViewSkip.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}