package com.example.clickchat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.clickchat.fragment.CameraFragment
import com.example.clickchat.fragment.ChatFragment
import com.example.clickchat.fragment.StoryFragment
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {

    private lateinit var adapterViewPager: FragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager>(R.id.viewPager)

        adapterViewPager = MyPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapterViewPager
        viewPager.currentItem = 1
        FirebaseApp.initializeApp(this)
    }

    class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> ChatFragment.newInstance()
                1 -> CameraFragment.newInstance()
                2 -> StoryFragment.newInstance()
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
