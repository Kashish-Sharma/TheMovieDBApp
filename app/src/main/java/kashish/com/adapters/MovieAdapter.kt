package kashish.com.adapters

import android.content.Context
import android.content.SharedPreferences
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kashish.com.R
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.utils.Constants.Companion.CONTENT_MOVIE
import kashish.com.utils.Constants.Companion.CONTENT_PROGRESS
import kashish.com.utils.Constants.Companion.CONTENT_SIMILAR
import kashish.com.utils.DateUtils
import kashish.com.utils.Helpers.buildImageUrl
import kashish.com.viewholders.MoreViewHolder
import kashish.com.viewholders.MovieViewHolder
import kashish.com.viewholders.ProgressBarViewHolder
import kotlinx.android.synthetic.main.movie_single_item.view.*


/**
 * Created by Kashish on 30-07-2018.
 */
class MovieAdapter(movieList: List<Movie>,listener: OnMovieClickListener,private val mSharedPreferences: SharedPreferences) : Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context
    private var mListener: OnMovieClickListener
    private var movieList: List<Movie>

    init {
        this.movieList = movieList
        this.mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View
        mContext = parent.context
        when(viewType){
            CONTENT_MOVIE ->{
                view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.movie_single_item, parent, false)
                return MovieViewHolder(view,mContext, movieList,mListener)
            }

            CONTENT_SIMILAR ->{
                view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.more_single_item, parent, false)
                return MoreViewHolder(view,mContext, movieList,mListener)
            }

            else -> {
                view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_view_progress_loader, parent, false)
                return ProgressBarViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder.itemViewType){
            CONTENT_MOVIE -> {
                val movieViewHolder = holder as MovieViewHolder
                val movie: Movie = movieList.get(holder.adapterPosition)

                movieViewHolder.movieTitle.setText(movie.title)
                movieViewHolder.movieRating.rating = movie.voteAverage!!.div(2)
                movieViewHolder.movieReleaseDate.text = "Release date: ".plus(DateUtils.getStringDate(movie.releaseDate!!))
                movieViewHolder.movieOverview.text = movie.overview

                movieViewHolder.itemView.single_item_movie_type.setText("Genre: "+movie.genreString)


                    if (mSharedPreferences.getBoolean(mContext.getString(R.string.pref_cache_data_key),true)){
                        Glide.with(mContext).load(buildImageUrl(movie.posterPath!!)).thumbnail(0.05f)
                                .transition(withCrossFade()).into(movieViewHolder.moviePoster)
                    } else{
                        Glide.with(mContext).load(buildImageUrl(movie.posterPath!!)).thumbnail(0.05f)
                                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                                .transition(withCrossFade()).into(movieViewHolder.moviePoster)
                    }

            }


            CONTENT_SIMILAR ->{
                val moreViewHolder = holder as MoreViewHolder
                val movie: Movie = movieList.get(holder.adapterPosition)

                moreViewHolder.moreTitle.setText(movie.title)
                moreViewHolder.moreSubtitle.setText(movie.genreString)

                if (mSharedPreferences.getBoolean(mContext.getString(R.string.pref_cache_data_key),true)){
                    Glide.with(mContext).load(buildImageUrl(movie.posterPath!!)).thumbnail(0.05f)
                            .transition(withCrossFade()).into(moreViewHolder.morePoster)
                } else{
                    Glide.with(mContext).load(buildImageUrl(movie.posterPath!!)).thumbnail(0.05f)
                            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                            .transition(withCrossFade()).into(moreViewHolder.morePoster)
                }



            }

            CONTENT_PROGRESS ->{
                // Nothing to bind here
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return movieList.get(position).contentType
    }

    override fun getItemCount(): Int = movieList.size
}