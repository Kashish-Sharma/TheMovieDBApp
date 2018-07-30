package kashish.com.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.ViewGroup
import kashish.com.R.layout.movie_single_item
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kashish.com.R
import kashish.com.models.Result
import kashish.com.utils.Constants.Companion.IMAGE_URL_BASE_PATH
import kashish.com.utils.Constants.Companion.getGenre
import kashish.com.viewholders.MovieViewHolder
import kotlinx.android.synthetic.main.movie_single_item.view.*


/**
 * Created by Kashish on 30-07-2018.
 */
class MovieAdapter(private var movieList: List<Result>) : Adapter<MovieViewHolder>() {

    private lateinit var mContext: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_single_item, parent, false)
        mContext = parent.context
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val result : Result = movieList.get(holder.adapterPosition)
        var movieType = "Genre: "
        val image_url = IMAGE_URL_BASE_PATH.plus(result.posterPath)

        holder.itemView.single_item_movie_title.setText(result.title)
        holder.itemView.single_item_movie_rating.setText("Rating: ".plus(result.voteAverage.toString()))
        holder.itemView.single_item_movie_popularity.setText("Popularity: ".plus(result.popularity.toString()))

        for (i in result.genreIds!!) {
            if (i == result.genreIds!!.size) movieType += getGenre(i)
            else movieType += getGenre(i) + ", "
        }

        holder.itemView.single_item_movie_type.setText(movieType)
        Glide.with(mContext).load(image_url).into(holder.itemView.single_item_movie_image)
    }


    override fun getItemCount(): Int = movieList.size
}