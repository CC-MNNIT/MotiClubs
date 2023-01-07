package com.example.notificationapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notificationapp.R
import com.example.notificationapp.data.network.IntroSlide
import com.google.android.material.imageview.ShapeableImageView

/**
 * Initialize the dataset of the Adapter
 *
 * @param mIntroSlides String[] containing the data to populate views to be used
 * by RecyclerView
 */
class IntroSliderAdapter(private val mIntroSlides: List<IntroSlide>) : RecyclerView.Adapter<IntroSliderAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTitle: TextView
        val textDescription: TextView
        val imageIcon: ShapeableImageView

        init {
            // Define click listener for the ViewHolder's View
            //TextView textTitle = (TextView)findViewById(R.id.textTitle);
            textDescription = view.findViewById<View>(R.id.textDescription) as TextView
            imageIcon = view.findViewById<View>(R.id.imageSlideIcon) as ShapeableImageView
            textTitle = view.findViewById<View>(R.id.textTitle) as TextView
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_slide, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textTitle.text = mIntroSlides[position].title
        viewHolder.textDescription.text = mIntroSlides[position].description
        viewHolder.imageIcon.setImageResource(mIntroSlides[position].icon)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int = mIntroSlides.size
}