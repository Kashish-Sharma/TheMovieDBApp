package kashish.com.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kashish.com.R
import kashish.com.models.Cast
import kashish.com.models.Crew
import kashish.com.utils.Helpers
import kashish.com.viewholders.CastCrewViewHolder
import kashish.com.viewholders.CrewViewHolder

/**
 * Created by Kashish on 03-08-2018.
 */
class CrewAdapter(private var crewList: List<Crew>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View
        mContext = parent.context

        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.crew_single_item, parent, false)
        return CrewViewHolder(view,mContext)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val crewViewHolder = holder as CrewViewHolder
        val crew: Crew = crewList.get(holder.adapterPosition)

        crewViewHolder.mCrewName.setText(crew.name)
        crewViewHolder.mCrewJob.setText(crew.job)
        Glide.with(mContext).load(Helpers.buildProfileImageUrl(crew.profilePath!!))
                .transition(DrawableTransitionOptions.withCrossFade()).into(crewViewHolder.mCrewImage)

    }

    override fun getItemCount(): Int = crewList.size
}