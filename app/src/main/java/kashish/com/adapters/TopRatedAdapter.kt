package kashish.com.adapters

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kashish.com.R
import kashish.com.database.Entities.TopRatedEntry
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.viewholders.TopRatedViewHolder

/**
 * Created by Kashish on 15-08-2018.
 */
class TopRatedAdapter(private val listener: OnMovieClickListener,
                      private val mSharedPreferences: SharedPreferences) : PagedListAdapter<TopRatedEntry,
        RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_single_item, parent, false)
        this.context = parent.context
        return TopRatedViewHolder(view,context,listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie: TopRatedEntry? = getItem(position)

        if (movie != null){
            val movieViewHolder = holder as TopRatedViewHolder
            movieViewHolder.bindPopularData(movie,mSharedPreferences)
        } else{
            notifyItemRemoved(position)
        }

    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<TopRatedEntry>() {
            override fun areItemsTheSame(oldItem: TopRatedEntry, newItem: TopRatedEntry): Boolean =
                    oldItem.movieId == newItem.movieId

            override fun areContentsTheSame(oldItem: TopRatedEntry, newItem: TopRatedEntry): Boolean =
                    oldItem == newItem
        }
    }


}