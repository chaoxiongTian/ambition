package com.hero.ambition

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hero.base.ext.alogi
import com.hero.base.ext.alogw
import com.hero.base.manager.ActivityRecord
import com.hero.base.manager.recodeName

open class BaseActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BaseActivityLog"
        val call = object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                super.onFragmentCreated(fm, f, savedInstanceState)
                alogi(TAG, "onFragmentCreated ${recodeName(f?.activity)} and ${f?.javaClass}")
                if (f == null || f.activity == null) {
                    alogw(TAG, "when onFragmentCreated fragment or activity is null.")
                    return
                }
                ActivityRecord.fragmentRecord.addFirst(
                    recodeName(f?.activity)
                            to f.javaClass.toString()
                )
            }

            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)
                alogi(TAG, "onFragmentStarted ${recodeName(f?.activity)} and ${f?.javaClass}")
            }


            override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
                super.onFragmentStopped(fm, f)
                alogi(TAG, "onFragmentStopped ${recodeName(f?.activity)} and ${f?.javaClass}")
            }

            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentDestroyed(fm, f)
                alogi(TAG, "onFragmentDestroyed ${recodeName(f?.activity)} and ${f?.javaClass}")
                val first = ActivityRecord.fragmentRecord.first()
                if (first.first == recodeName(f?.requireActivity()) && first.second == f.javaClass.toString()) {
                    ActivityRecord.fragmentRecord.removeFirst()
                } else {
                    alogw(TAG, "onFragmentStopped, ture is : ${first.first} ${first.second}")
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.registerFragmentLifecycleCallbacks(call, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(call)
    }
}