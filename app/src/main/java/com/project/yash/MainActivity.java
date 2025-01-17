package com.project.yash;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;
import com.project.yash.routine.R;
import com.project.yash.ui.ScheduleMakerPage;
import com.project.yash.ui.SyncPage;
import com.project.yash.ui.TaskEnablerPage;

public class MainActivity extends AppCompatActivity {
    private SlideUp slideUp;
    LinearLayout linearLayout;
    Thread t;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager viewPager;
    public static final String[] PAGE_TITLE = {"Create Schedule","Set Profile","Sync"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start_page);
        TabLayout tabLayout=(TabLayout)findViewById(R.id.tabs);
        mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager=(ViewPager)findViewById(R.id.container1);
        viewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        linearLayout=(LinearLayout)findViewById(R.id.slide_up);
        slideUp=new SlideUpBuilder(linearLayout).withStartGravity(Gravity.BOTTOM)
                .withStartState(SlideUp.State.HIDDEN).withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onVisibilityChanged(int visibility) {

                    }

                    @Override
            public void onSlide(float percent) {
                linearLayout.setAlpha(1 - (percent / 200));

            }

        }).build();
        TextView textView=(TextView)findViewById(R.id.author);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               slideUp.show();
            }
        });
        ImageView imageView=(ImageView)findViewById(R.id.author_image);
        imageView.setClipToOutline(true);



    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position){
                case 0:
                    return ScheduleMakerPage.newInstance();
                case 1:
                    return TaskEnablerPage.newFragment();
                case 2:
                    return new SyncPage();
                default:
                    return TaskEnablerPage.newFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return PAGE_TITLE[position];
        }
    }
    private boolean dbl_click=false;
    @Override
    public void onBackPressed() {
        if(linearLayout.getVisibility()!=View.GONE)
        {
           slideUp.hide();
        }
        else {
            if (dbl_click)
                super.onBackPressed();
            dbl_click = true;
            Toast.makeText(getApplicationContext(), "Press back once more", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dbl_click = false;
                }
            }, 2000);
        }
    }
}
