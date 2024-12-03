package com.khrlanamm.dicodingtales.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.khrlanamm.dicodingtales.HomeAdapter

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val storyItems = HomeAdapter.getCurrentStories()
        return StackRemoteViewsFactory(this.applicationContext, storyItems)
    }
}
