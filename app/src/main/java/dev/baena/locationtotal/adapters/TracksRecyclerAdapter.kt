package dev.baena.locationtotal.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import dev.baena.locationtotal.R
import dev.baena.locationtotal.models.Track

class TracksRecyclerAdapter(
        private val list: List<Track>,
        private var onTrackClickListener: OnTrackClickListener)
    : RecyclerView.Adapter<TracksRecyclerAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksRecyclerAdapter.TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = TrackViewHolder(inflater, parent, onTrackClickListener)
        return holder
    }

    override fun onBindViewHolder(holder: TracksRecyclerAdapter.TrackViewHolder, position: Int) {
        val track: Track = list[position]
        holder.bind(track)
    }

    override fun getItemCount(): Int = list.size

    inner class TrackViewHolder(inflater: LayoutInflater, parent: ViewGroup,
                                val onTrackClickListener: OnTrackClickListener) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.vh_track_item, parent, false)), View.OnClickListener {

        private var mNameView: TextView? = null
        private var mInfoView: TextView? = null

        init {
            mNameView = itemView.findViewById(R.id.track_name)
            mInfoView = itemView.findViewById(R.id.track_info)
            itemView.setOnClickListener(this)
        }

        fun bind(track: Track) {
            mNameView?.text = track.name
            mInfoView?.text = track.path
        }

        override fun onClick(v: View?) {
            onTrackClickListener.onTrackClick(adapterPosition)
        }
    }

    interface OnTrackClickListener {
        fun onTrackClick(notePosition: Int): Unit
    }
}