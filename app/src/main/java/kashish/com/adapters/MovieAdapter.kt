package kashish.com.adapters

import android.content.Context
import android.graphics.BlurMaskFilter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kashish.com.R
import kashish.com.models.Movie
import kashish.com.utils.Constants.Companion.CONTENT_MOVIE
import kashish.com.utils.Constants.Companion.getGenre
import kashish.com.utils.DateUtils
import kashish.com.utils.Helpers.buildImageUrl
import kashish.com.utils.Urls.Companion.IMAGE_URL_BASE_PATH
import kashish.com.viewholders.MovieViewHolder
import kashish.com.viewholders.ProgressBarViewHolder
import kotlinx.android.synthetic.main.movie_single_item.view.*


/**
 * Created by Kashish on 30-07-2018.
 */
class MovieAdapter(private var movieList: List<Movie>) : Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View
        mContext = parent.context
        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_single_item, parent, false)
        return MovieViewHolder(view,mContext, movieList)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder.itemViewType){
            CONTENT_MOVIE -> {
                val movieViewHolder = holder as MovieViewHolder
                val movie: Movie = movieList.get(holder.adapterPosition)
                var movieType = "Genre: "

                movieViewHolder.movieTitle.setText(movie.title)
                movieViewHolder.movieRating.rating = movie.voteAverage!!.div(2)
                movieViewHolder.moviePopularity.setText("Popularity: ".plus(movie.popularity.toString()))
                movieViewHolder.movieReleaseDate.setText("Release date: ".plus(DateUtils.getStringDate(movie.releaseDate!!)))

                for (i in movie.genreIds!!) {
                    if (i == movie.genreIds!!.last()) movieType += getGenre(i)
                    else movieType += getGenre(i) + ", "
                }

                movieViewHolder.itemView.single_item_movie_type.setText(movieType)
                Glide.with(mContext).load(buildImageUrl(movie.posterPath!!))
                        .transition(withCrossFade()).into(movieViewHolder.moviePoster)
            }

        }

    }

    override fun getItemCount(): Int = movieList.size
}