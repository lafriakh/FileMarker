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

    private var myState = State()

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State) {
        myState = state
    }

    fun markFile(file: VirtualFile) {
        val url = file.url
        if (!myState.markedFileUrls.contains(url)) {
            myState.markedFileUrls.add(url)
        }
    }

    fun unmarkFile(file: VirtualFile) {
        myState.markedFileUrls.remove(file.url)
    }

    fun getFiles(): List<VirtualFile> {
        val fileManager = VirtualFileManager.getInstance()
        val result = mutableListOf<VirtualFile>()

        val iterator = myState.markedFileUrls.iterator()
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
        return myState.markedFileUrls.contains(file.url)
    }
}