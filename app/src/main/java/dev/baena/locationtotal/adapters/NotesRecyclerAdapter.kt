package dev.baena.locationtotal.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import dev.baena.locationtotal.R
import dev.baena.locationtotal.models.Note

class NotesRecyclerAdapter(
        private val list: List<Note>,
        private val onNoteClickListener: OnNoteClickListener)
    : RecyclerView.Adapter<NotesRecyclerAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(inflater: LayoutInflater, parent: ViewGroup, val onNoteClickListener: OnNoteClickListener) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.vh_note_item, parent, false)),
        View.OnClickListener {

        private var mTextView: TextView? = null
        private var mGeopositionView: TextView? = null


        init {
            mTextView = itemView.findViewById(R.id.note_text)
            mGeopositionView = itemView.findViewById(R.id.note_geoposition)
            itemView.setOnClickListener(this)
        }

        fun bind(note: Note) {
            mTextView?.text = note.text
            mGeopositionView?.text = "Lat: ${String.format("%.2f", note.lat)} Lng: ${String.format("%.2f", note.lng)}"
        }

        override fun onClick(v: View?) {
            onNoteClickListener.onNoteClick(adapterPosition)
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesRecyclerAdapter.NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = NoteViewHolder(inflater, parent, onNoteClickListener)
        return holder
    }

    override fun onBindViewHolder(holder: NotesRecyclerAdapter.NoteViewHolder, position: Int) {
        val note: Note = list[position]
        holder.bind(note)
    }

    override fun getItemCount(): Int = list.size

    interface OnNoteClickListener {
        fun onNoteClick(notePosition: Int): Unit
    }
}