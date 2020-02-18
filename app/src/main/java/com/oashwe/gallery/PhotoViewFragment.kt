package com.oashwe.gallery


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_photo_view.*

/**
 * A simple [Fragment] subclass.
 */
class PhotoViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val photo_list = arguments?.getParcelableArrayList<PhotoItem>("PHOTO_LIST")
        PhotoViewAdapter().apply {
            viewpager2.adapter = this
            submitList(photo_list)
        }
        viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photo_tag.text = "${position +1 }/${photo_list?.size}"
            }
        })
        viewpager2.setCurrentItem(arguments?.getInt("PHOTO_POSITION")?:0,false)
    }

}
