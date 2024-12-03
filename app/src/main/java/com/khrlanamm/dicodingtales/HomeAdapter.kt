package com.khrlanamm.dicodingtales

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khrlanamm.dicodingtales.data.remote.response.ListStoryItem
import com.khrlanamm.dicodingtales.databinding.ItemStoryBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class HomeAdapter(private val onItemClick: (ListStoryItem) -> Unit) : ListAdapter<ListStoryItem, HomeAdapter.HomeViewHolder>(DIFF_CALLBACK) {

    inner class HomeViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name
            binding.tvItemDesc.text = story.description
            binding.tvItemDate.text = formatDate(story.createdAt)

            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            binding.root.setOnClickListener {
                onItemClick(story)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = inputFormat.parse(dateString)

            val isIndonesian = Locale.getDefault().language == "in"

            if (date != null) {
                if (isIndonesian) {
                    val calendar = Calendar.getInstance()
                    calendar.time = date

                    val outputFormat = SimpleDateFormat("d MMM yyyy HH:mm", Locale("in", "ID"))
                    "${outputFormat.format(calendar.time)} WIB"
                } else {
                    val outputFormat = SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault())
                    outputFormat.timeZone = TimeZone.getTimeZone("UTC")
                    "${outputFormat.format(date)} UTC"
                }
            } else {
                dateString
            }
        } catch (e: Exception) {
            dateString
        }
    }
}