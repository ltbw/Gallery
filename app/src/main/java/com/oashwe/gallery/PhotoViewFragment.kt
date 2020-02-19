package com.oashwe.gallery


import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_photo_view.*
import kotlinx.android.synthetic.main.photo_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.security.Permission

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
        viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photo_tag.text = "${position + 1}/${photo_list?.size}"
            }
        })
        viewpager2.setCurrentItem(arguments?.getInt("PHOTO_POSITION") ?: 0, false)
        //判断是否API，小于29判断是否请求了权限（清单里的权限和已经授予的权限）
        save_image.setOnClickListener {
            if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                //savePhoto()       报错，不能在主线程里执行了
                viewLifecycleOwner.lifecycleScope.launch { savePhoto() }
                //  放在这里执行，生命周期跟随主线程，主线程摧毁，IO线程也摧毁
            }
        }

    }
    //根据请求权限的结果执行操作
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //savePhoto()
                    viewLifecycleOwner.lifecycleScope.launch {
                        savePhoto()
                    }
                } else {
                    Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun savePhoto() {
        withContext(Dispatchers.IO){
            val holder =
                (viewpager2.get(0) as RecyclerView).findViewHolderForAdapterPosition(viewpager2.currentItem)
                        as PhotoViewHolder
            val bitmap = holder.itemView.pagerPhoto.drawable.toBitmap()
            val photoUri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            ) ?: kotlin.run {
                //Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()   不能在io线程执行 程序会崩溃
                MainScope().launch { Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show() }
                //return        不知道返回什么了
                return@withContext
            }
            requireContext().contentResolver.openOutputStream(photoUri).use {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)) {
                    //Toast.makeText(requireContext(), "存储成功", Toast.LENGTH_SHORT).show()   不能在io线程执行
                    MainScope().launch { Toast.makeText(requireContext(), "存储成功", Toast.LENGTH_SHORT).show() }
                } else {
                    //Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()   不能拿在io线程执行
                    MainScope().launch { Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()  }
                }
            }
        }

    }
}
