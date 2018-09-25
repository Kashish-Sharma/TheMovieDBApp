package kashish.com.adapters

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kashish.com.R
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.interfaces.OnReviewReadMoreClickListener
import kashish.com.models.MovieReview
import kashish.com.utils.Constants.Companion.CONTENT_REVIEW
import kashish.com.viewholders.ProgressBarViewHolder
import kashish.com.viewholders.ReviewViewHolder

/**
 * Created by Kashish on 02-08-2018.
 */
class MovieReviewAdapter(private val listener: OnReviewReadMoreClickListener) : ListAdapter<MovieReview,
        RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.review_single_item, parent, false)
        this.context = parent.context
        return ReviewViewHolder(view,context,listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val movie: MovieReview? = getItem(position)
        if (movie != null){
            val movieViewHolder = holder as ReviewViewHolder
            movieViewHolder.bindReviewData(movie)
        }
    }


    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<MovieReview>() {
            override fun areItemsTheSame(oldItem: MovieReview, newItem: MovieReview): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MovieReview, newItem: MovieReview): Boolean =
                    oldItem == newItem
        }
    }
}