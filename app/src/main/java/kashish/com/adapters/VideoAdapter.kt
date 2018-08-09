package kashish.com.adapters

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import kashish.com.R
import kashish.com.interfaces.OnVideoClickListener
import kashish.com.models.Video
import kashish.com.utils.Helpers.buildYouTubeThumbnailURL
import kashish.com.viewholders.VideoViewHolder

/**
 * Created by Kashish on 04-08-2018.
 */
class VideoAdapter(private var videoList: List<Video>,listener: OnVideoClickListener,private val mSharedPreferences: SharedPreferences) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context
    private var mListener: OnVideoClickListener

    init {
        this.mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View
        mContext = parent.context

        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.trailer_single_item, parent, false)
        return VideoViewHolder(view,mContext, videoList,mListener)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val videoViewHolder = holder as VideoViewHolder
        val video: Video = videoList.get(holder.adapterPosition)

        if (mSharedPreferences.getBoolean(mContext.getString(R.string.pref_cache_data_key),true)){
            Glide.with(mContext).load(buildYouTubeThumbnailURL(video.key!!)).thumbnail(0.05f)
                    .transition(withCrossFade()).into(videoViewHolder.mVideoImage)
        } else{
            Glide.with(mContext).load(buildYouTubeThumbnailURL(video.key!!))
                    .thumbnail(0.05f).apply(RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                    .transition(withCrossFade()).into(videoViewHolder.mVideoImage)
        }

    }

    override fun getItemCount(): Int = videoList.size
}