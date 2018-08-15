package kashish.com.adapters

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kashish.com.R
import kashish.com.models.Crew
import kashish.com.viewholders.CastCrewViewHolder

/**
 * Created by Kashish on 13-08-2018.
 */
class CrewAdapter(private val mSharedPreferences: SharedPreferences) : ListAdapter<Crew,
        RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.cast_single_item, parent, false)
        this.context = parent.context
        return CastCrewViewHolder(view,context, mSharedPreferences)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val crew: Crew? = getItem(holder.adapterPosition)
        if (crew != null){
            val movieViewHolder = holder as CastCrewViewHolder
            movieViewHolder.bindCrewData(crew)
        }
    }


    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Crew>() {
            override fun areItemsTheSame(oldItem: Crew, newItem: Crew): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Crew, newItem: Crew): Boolean =
                    oldItem == newItem
        }
    }
}