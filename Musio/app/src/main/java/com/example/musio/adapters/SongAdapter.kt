package com.example.musio.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.musio.R
import com.example.musio.data.entity.Song
import com.example.musio.databinding.CellTrackBinding

class SongAdapter : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var onItemClickListener: ((Song) -> Unit)? = null

    fun setOnItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }

    // check same items to update recycler view list with new songs
    private val diffCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.cell_track,
                parent,
                false
        ))
    }

    // load data into items in recycler view
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = CellTrackBinding.bind(itemView)

        fun bind(position: Int) {
            with(binding) {
                val songs = songs[position]
                itemView.setOnClickListener {
                    val navController: NavController?
                    navController = Navigation.findNavController(itemView)
                    val bundle = Bundle()
                    with(bundle) {
                        putString("title", songs.title)
                        putString("subtitle", songs.subTitle)
                        putString("image", songs.imageUrl)
                        putInt("position", position)
                    }
                    navController.navigate(R.id.global_action_to_song_fragment, bundle)
                    onItemClickListener?.let { click ->
                        click(songs)
                    }
                }
                textTitle.text = songs.title
                textSubtitle.text = songs.subTitle
                imageItemSongs.load(songs.imageUrl) {
                    crossfade(true)
                    crossfade(700)
                    transformations(CircleCropTransformation())
                }
            }
        }
    }
}