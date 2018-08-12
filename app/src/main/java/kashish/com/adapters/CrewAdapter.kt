package kashish.com.adapters

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import kashish.com.R
import kashish.com.models.Cast
import kashish.com.models.Crew
import kashish.com.utils.Helpers.buildProfileImageUrl
import kashish.com.viewholders.CastCrewViewHolder

/**
 * Created by Kashish on 13-08-2018.
 */
class CrewAdapter(private var crewList: List<Crew>, private val mSharedPreferences: SharedPreferences) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View
        mContext = parent.context

        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cast_single_item, parent, false)
        return CastCrewViewHolder(view,mContext)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val crewViewHolder = holder as CastCrewViewHolder
        val crew: Crew = crewList.get(holder.adapterPosition)

        crewViewHolder.mCastName.setText(crew.name)
        crewViewHolder.mCastCharacter.setText(crew.department)

        if (mSharedPreferences.getBoolean(mContext.getString(R.string.pref_cache_data_key),true)){
            Glide.with(mContext).load(buildProfileImageUrl(crew.profilePath!!))
                    .apply(RequestOptions().placeholder(R.color.dark_gray))
                    .thumbnail(0.05f)
                    .transition(withCrossFade())
                    .into(crewViewHolder.mCastImage)
        } else {
            Glide.with(mContext).load(buildProfileImageUrl(crew.profilePath!!))
                    .apply(RequestOptions().placeholder(R.color.dark_gray)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true))
                    .thumbnail(0.05f)
                    .transition(withCrossFade())
                    .into(crewViewHolder.mCastImage)
        }

    }

    override fun getItemCount(): Int = crewList.size
}