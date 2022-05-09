package manual.app.repository

import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import manual.app.data.ChapterGroupIcon
import manual.core.resources.read

class ChapterGroupIconsRepository(
    private val assetManager: AssetManager,
    private val gson: Gson
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val stateFlow = MutableStateFlow<List<ChapterGroupIcon>?>(null)

    init {
        coroutineScope.launch {
            try {
                stateFlow.value = gson.fromJson(
                    assetManager.read("chapter-group-icons/map.json"),
                    JsonArray::class.java
                ).map {
                    val json = it.asJsonObject
                    ChapterGroupIcon(
                        json["chapterGroupId"].asInt,
                        json["source"].asString
                    )
                }
            } catch (t: Throwable) {
                stateFlow.value = emptyList()
            }
        }
    }

    fun chapterGroupIconFlow(chapterGroupId: Int) = stateFlow.filterNotNull().map {
        it.firstOrNull { it.chapterGroupId == chapterGroupId }
    }

}