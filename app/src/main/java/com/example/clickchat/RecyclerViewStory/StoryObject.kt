package com.example.clickchat.RecyclerViewStory

data class StoryObject(var email: String, var uid: String?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StoryObject) return false
        if (uid != other.uid) return false
        return true
    }

    override fun hashCode(): Int {
        return uid?.hashCode() ?: 0
    }
}
