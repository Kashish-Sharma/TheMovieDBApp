package kashish.com.viewholders

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kashish.com.R
import kashish.com.database.Entities.SearchEntry
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.utils.Constants
import kashish.com.utils.Helpers

/**
 * Created by Kashish on 13-08-2018.
 */
class SearchViewHolder(itemView: View?,
                       val context: Context,
                       private val listener: OnMovieClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {



    var moreTitle: TextView
    var moreSubtitle: TextView
    var morePoster: ImageView
    private var movie: Movie? = null

    init {
        moreTitle = itemView!!.findViewById(R.id.item_more_name)
        moreSubtitle = itemView.findViewById(R.id.item_more_subtitle)
        morePoster = itemView.findViewById(R.id.item_more_image)

        itemView.setOnClickListener(this)
    }

    fun bindSearchData(movie: Movie?, mSharedPreferences:SharedPreferences) {
        if (movie == null) {
            return
        } else {
            this.movie = movie
            moreTitle.setText(movie.title)
            moreSubtitle.setText(movie.genreString)

            if (mSharedPreferences.getBoolean(context.getString(R.string.pref_cache_data_key),true)){
                Glide.with(context).load(Helpers.buildImageUrl(movie.posterPath!!)).thumbnail(0.05f)
                        .transition(DrawableTransitionOptions.withCrossFade()).into(morePoster)
            } else{
                Glide.with(context).load(Helpers.buildImageUrl(movie.posterPath!!)).thumbnail(0.05f)
                        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .transition(DrawableTransitionOptions.withCrossFade()).into(morePoster)
            }
        }
    }



    override fun onClick(p0: View?) {
        val position: Int = adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            listener.onMovieClickListener(movie!!)
        }
    }
}