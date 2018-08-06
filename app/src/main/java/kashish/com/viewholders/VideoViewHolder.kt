package kashish.com.viewholders

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kashish.com.R
import kashish.com.interfaces.OnVideoClickListener
import kashish.com.models.Video
import kashish.com.utils.Helpers

/**
 * Created by Kashish on 04-08-2018.
 */
class VideoViewHolder(itemView: View?,
                      val context: Context,
                      val videoList: List<Video>,
                      val listener: OnVideoClickListener): RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var mVideoImage: ImageView
    init{
        mVideoImage = itemView!!.findViewById(R.id.activity_detail_trailer_poster_image)
        itemView.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        val position:Int = adapterPosition
        if (position!=RecyclerView.NO_POSITION){
            listener.onVideoClickListener(videoList.get(position))
        }
    }

}