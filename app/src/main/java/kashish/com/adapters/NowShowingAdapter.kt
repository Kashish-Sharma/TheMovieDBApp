package kashish.com.adapters

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kashish.com.R
import kashish.com.database.Entities.NowShowingEntry
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.viewholders.MovieViewHolder
import kashish.com.viewholders.NowShowingViewHolder

/**
 * Created by Kashish on 14-08-2018.
 */
class NowShowingAdapter(private val listener: OnMovieClickListener,
                        private val mSharedPreferences: SharedPreferences) : ListAdapter<NowShowingEntry,
        RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_single_item, parent, false)
        this.context = parent.context
        return NowShowingViewHolder(view,context,listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = getItem(position)
        if (movie != null) {
            val movieViewHolder = holder as NowShowingViewHolder

            movieViewHolder.bindNowShowingData(movie,mSharedPreferences)

        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<NowShowingEntry>() {
            override fun areItemsTheSame(oldItem: NowShowingEntry, newItem: NowShowingEntry): Boolean =
                    oldItem.movieId == newItem.movieId

            override fun areContentsTheSame(oldItem: NowShowingEntry, newItem: NowShowingEntry): Boolean =
                    oldItem == newItem
        }
    }
}