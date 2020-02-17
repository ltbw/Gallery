package com.oashwe.gallery


import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_photo_item.*
import kotlinx.android.synthetic.main.gallery_cell.*
import kotlinx.android.synthetic.main.gallery_cell.photoView
import kotlinx.android.synthetic.main.gallery_cell.view.*

/**
 * A simple [Fragment] subclass.
 */
class PhotoItemFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //闪退：问题出在没给shimmerLayout id，这里用了Gallery里的shimmerLayout
        //shimmerLayout.apply {
        shimmerLayoutPhoto.apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }
        Glide.with(requireContext())
            .load(arguments?.getParcelable<PhotoItem>("PHOTO")?.fullUrl)
            .placeholder(R.drawable.ic_photo_black_24dp)
            .listener(object:RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also { shimmerLayout?.stopShimmerAnimation() }
                }

            })
            .into(photoView)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_item, container, false)
    }


}
