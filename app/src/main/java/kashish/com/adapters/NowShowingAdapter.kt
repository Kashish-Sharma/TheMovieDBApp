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
import kashish.com.database.Entities.UpcomingEntry
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.utils.Constants.Companion.NOWSHOWING
import kashish.com.utils.Constants.Companion.SEARCHES
import kashish.com.utils.Constants.Companion.UPCOMING
import kashish.com.viewholders.MovieViewHolder
import kashish.com.viewholders.NowShowingViewHolder
import kashish.com.viewholders.SearchViewHolder
import kashish.com.viewholders.UpcomingViewHolder

/**
 * Created by Kashish on 14-08-2018.
 */
class NowShowingAdapter(private val listener: OnMovieClickListener,
                        private val mSharedPreferences: SharedPreferences) : ListAdapter<Movie,
        RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){

            NOWSHOWING -> {
                val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.movie_single_item, parent, false)
                this.context = parent.context
                return NowShowingViewHolder(view,context,listener)
            }

            UPCOMING -> {
                val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.movie_single_item, parent, false)
                this.context = parent.context
                return UpcomingViewHolder(view,context,listener)
            }

            SEARCHES -> {
                val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.more_single_item, parent, false)
                this.context = parent.context
                return SearchViewHolder(view,context,listener)
            }

        }
        return SearchViewHolder(null,context,listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            NOWSHOWING -> {
                val movie: Movie = getItem(position)
                    val movieViewHolder = holder as NowShowingViewHolder
                    movieViewHolder.bindNowShowingData(movie,mSharedPreferences)
            }

            UPCOMING -> {
                val movie: Movie = getItem(position)
                    val movieViewHolder = holder as UpcomingViewHolder
                    movieViewHolder.bindNowShowingData(movie,mSharedPreferences)
            }

            SEARCHES -> {
                val movie: Movie = getItem(position)
                    val searchViewHolder = holder as SearchViewHolder
                    searchViewHolder.bindSearchData(movie,mSharedPreferences)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).tableName!!
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                    oldItem == newItem
        }
    }
}