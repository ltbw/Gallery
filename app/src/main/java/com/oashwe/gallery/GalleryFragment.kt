package com.oashwe.gallery


import android.app.Application
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.util.logging.Handler

/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment() {
private lateinit var galleryViewModel:GalleryViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    //加载menu资源
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.swiperindicate -> {
                swipeLayoutGallery.isRefreshing = true
                android.os.Handler().postDelayed(Runnable { galleryViewModel.fetchData() },700)

            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true) //设置显示menu
        super.onActivityCreated(savedInstanceState)
        val galleryAdapter = GalleryAdapter()
        recyclerView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }
        galleryViewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application   //用的Application（）这个方法
        )).get(GalleryViewModel::class.java)
        galleryViewModel.photoListLive.observe(this, Observer {
            galleryAdapter.submitList(it)
            swipeLayoutGallery.isRefreshing = false
        })
        galleryViewModel.photoListLive.value?:galleryViewModel.fetchData()
        swipeLayoutGallery.setOnRefreshListener {
            galleryViewModel.fetchData()
        }
    }


}
