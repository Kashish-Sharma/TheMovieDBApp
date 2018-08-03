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
import kashish.com.models.Video
import kashish.com.utils.Helpers

/**
 * Created by Kashish on 04-08-2018.
 */
class VideoViewHolder(itemView: View?, context: Context, videoList: List<Video>): RecyclerView.ViewHolder(itemView) {
    var mVideoImage: ImageView
    init{
        mVideoImage = itemView!!.findViewById(R.id.activity_detail_trailer_poster_image)

        itemView.setOnClickListener(View.OnClickListener {
            val video = videoList.get(adapterPosition)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(Helpers.buildYoutubeURL(video.key!!))
            context.startActivity(Intent.createChooser(intent, "View Trailer:"))
        })

    }
}