package kashish.com.viewholders

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kashish.com.R

/**
 * Created by Kashish on 04-08-2018.
 */
class VideoViewHolder(itemView: View?, context: Context): RecyclerView.ViewHolder(itemView) {
    var mVideoImage: ImageView
    init{
        mVideoImage = itemView!!.findViewById(R.id.activity_detail_trailer_poster_image)
    }
}