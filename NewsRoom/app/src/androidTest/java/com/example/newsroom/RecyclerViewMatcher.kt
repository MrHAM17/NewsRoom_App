package com.example.newsroom

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewMatcher(private val recyclerViewId: Int) {

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("View at position $position in RecyclerView")
            }

            public override fun matchesSafely(view: View): Boolean {
                val recyclerView: RecyclerView =
                    view.rootView.findViewById(recyclerViewId)
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                    ?: return false
                val targetView = viewHolder.itemView.findViewById<View>(targetViewId)
                return view === targetView
            }
        }
    }
}