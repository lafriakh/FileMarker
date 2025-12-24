package com.rachid.marker

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager

@Service(Service.Level.PROJECT)
@State(
    name = "MarkedFilesState",
    storages = [Storage("marked_files.xml")]
)
class MarkedFilesService(private val project: Project) : PersistentStateComponent<MarkedFilesService.State> {

    data class State(
        var markedFileUrls: MutableList<String> = mutableListOf()
    )

    private var store = State()

    override fun getState(): State {
        return store
    }

    override fun loadState(state: State) {
        store = state
    }

    private fun fireFilesChanged() {
        project.messageBus.syncPublisher(MarkedFilesListener.TOPIC).markedFilesChanged()
    }

    fun markFile(file: VirtualFile) {
        val url = file.url
        if (!store.markedFileUrls.contains(url)) {
            store.markedFileUrls.add(url)
        }
    }

    fun unmarkFile(file: VirtualFile) {
        if (store.markedFileUrls.remove(file.url)) {
            fireFilesChanged()
        }
    }

    fun getFiles(): List<VirtualFile> {
        val fileManager = VirtualFileManager.getInstance()
        val result = mutableListOf<VirtualFile>()

        val iterator = store.markedFileUrls.iterator()
        while (iterator.hasNext()) {
            val url = iterator.next()
            val file = fileManager.findFileByUrl(url)

            if (file != null && file.isValid) {
                result.add(file)
            } else {
                // If file no longer exists (deleted), remove it from the list?
                // iterator.remove()
            }
        }
        return result
    }

    fun isMarked(file: VirtualFile): Boolean {
        return store.markedFileUrls.contains(file.url)
    }
}