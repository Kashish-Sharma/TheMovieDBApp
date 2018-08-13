package kashish.com.adapters

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kashish.com.R
import kashish.com.database.Entities.SearchEntry
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.utils.Helpers
import kashish.com.viewholders.MoreViewHolder
import kashish.com.viewholders.SearchViewHolder

/**
 * Created by Kashish on 13-08-2018.
 */
class SearchAdapter(private val listener: OnMovieClickListener,
                   private val mSharedPreferences: SharedPreferences) : ListAdapter<SearchEntry,
        RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.more_single_item, parent, false)
        this.context = parent.context
        return SearchViewHolder(view,context,listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = getItem(position)
        if (movie != null) {
            val searchViewHolder = holder as SearchViewHolder

            searchViewHolder.bindSearchData(movie,mSharedPreferences)

        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<SearchEntry>() {
            override fun areItemsTheSame(oldItem: SearchEntry, newItem: SearchEntry): Boolean =
                    oldItem.movieId == newItem.movieId

            override fun areContentsTheSame(oldItem: SearchEntry, newItem: SearchEntry): Boolean =
                    oldItem == newItem
        }
    }
}