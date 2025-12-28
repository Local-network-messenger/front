package spbsu.jigokuraku01.front.session

import java.util.prefs.Preferences

object DataStore {
    val prefs = Preferences.userRoot().node("/messenger")

    fun save(user: User) {
        prefs.put("username", user.name)
    }

    fun load() = prefs.get("username", "")
}