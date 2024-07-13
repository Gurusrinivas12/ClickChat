package com.example.clickchat.RecyclerViewStory

data class StoryObject(
    var email: String,
    var uid: String,
    var charOrStory: String?
) {
    constructor(email: String, uid: String) : this(email, uid, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other?.javaClass) return false

        other as StoryObject

        if (uid != other.uid) return false

        return true
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}
