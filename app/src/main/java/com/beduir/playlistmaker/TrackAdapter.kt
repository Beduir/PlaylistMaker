package com.beduir.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val coverImage: ImageView = itemView.findViewById(R.id.cover_image)
    private val title: TextView = itemView.findViewById(R.id.title)
    private val author: TextView = itemView.findViewById(R.id.author)
    private val duration: TextView = itemView.findViewById(R.id.duration)
    private val cornerRadius: Int = itemView.resources.getDimensionPixelSize(
        R.dimen.search_view_cover_conver_radius)

    fun bind(track: Track) {
        title.text = track.trackName
        author.text = track.artistName
        duration.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.cover)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(coverImage)
    }
}

class TrackAdapter(private val tracks: List<Track>): RecyclerView.Adapter<TrackViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_list_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}