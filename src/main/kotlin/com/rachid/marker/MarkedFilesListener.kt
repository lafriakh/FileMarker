package com.rachid.marker

import com.intellij.util.messages.Topic

interface MarkedFilesListener {
    // This is the "Channel" (Topic) we will broadcast on
    companion object {
        val TOPIC = Topic.create("Marked Files Changed", MarkedFilesListener::class.java)
    }

    fun markedFilesChanged()
}
