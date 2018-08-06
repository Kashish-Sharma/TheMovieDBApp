package kashish.com.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kashish.com.R
import kashish.com.models.Cast
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import kashish.com.utils.Helpers.buildImageUrl
import kashish.com.utils.Helpers.buildProfileImageUrl
import kashish.com.viewholders.CastCrewViewHolder

/**
 * Created by Kashish on 03-08-2018.
 */
class CastCrewAdapter(private var castList: List<Cast>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View
        mContext = parent.context

        view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cast_single_item, parent, false)
        return CastCrewViewHolder(view,mContext)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val castViewHolder = holder as CastCrewViewHolder
        val cast: Cast = castList.get(holder.adapterPosition)

        castViewHolder.mCastName.setText(cast.name)
        castViewHolder.mCastCharacter.setText(cast.character)
        Glide.with(mContext).load(buildProfileImageUrl(cast.profilePath!!))
                .thumbnail(0.05f)
                .transition(withCrossFade())
                .into(castViewHolder.mCastImage)

    }

    override fun getItemCount(): Int = castList.size
}