package com.rachid.marker

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JList

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.JBPopup
import java.awt.Dimension
import com.intellij.util.IconUtil

// Action: Mark the current file (Cmd + A)
class MarkFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        project.service<MarkedFilesService>().markFile(file)
    }
}

// Action: Unmark the current file (Cmd + D)
class UnmarkFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        project.service<MarkedFilesService>().unmarkFile(file)
    }
}

// Action: Show the list (Cmd + Shift + O)
class ShowMarkedFilesAction : AnAction() {

    private var popup: JBPopup? = null

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        showPopup(project)
    }

    private fun showPopup(project: Project) {
        val service = project.service<MarkedFilesService>()
        val virtualFiles = service.getFiles()

        if (virtualFiles.isEmpty()) {
            Messages.showInfoMessage(project, "No files marked yet.", "Marked Files")
            return
        }

        // 1. Create a Custom Renderer to mimic the "Go to File" look
        val renderer = object : ColoredListCellRenderer<VirtualFile>() {
            override fun customizeCellRenderer(
                list: JList<out VirtualFile>,
                file: VirtualFile,
                index: Int,
                selected: Boolean,
                hasFocus: Boolean
            ) {
                // A. Icon: Get the correct icon for the file type
                icon = IconUtil.getIcon(file, 0, project)

                // B. File Name: Bold text
                append(file.name, SimpleTextAttributes.REGULAR_ATTRIBUTES)

                // C. Path: Gray text aligned to the right (container text)
                // We add a small space before the path
                val parentPath = file.parent?.path ?: ""
                // Truncate path if it's too long (optional aesthetic choice)
                val displayPath = if (parentPath.length > 50) "...${parentPath.takeLast(45)}" else parentPath

                if (displayPath.isNotEmpty()) {
                    append("  ($displayPath)", SimpleTextAttributes.GRAYED_ATTRIBUTES)
                }
            }
        }

        // 2. Build the Popup using Safe VirtualFiles
        this.popup = JBPopupFactory.getInstance()
            .createPopupChooserBuilder(virtualFiles)
            .setTitle("Marked Files")
            .setRenderer(renderer)
            .setMovable(true)
            .setResizable(true)
            .setNamerForFiltering { it.name } // Enable typing to search
            .setMinSize(Dimension(600, -1))   // Set Width to 600px
            .setItemChosenCallback { file ->
                FileEditorManager.getInstance(project).openFile(file, true)
            }
            .createPopup()

        this.popup?.let { popup ->
            popup.showCenteredInCurrentWindow(project)
            filesChangedListener(project, popup)
        }
    }


    private fun filesChangedListener(project: Project, popup: JBPopup) {
        val connection = project.messageBus.connect(popup)
        connection.subscribe(MarkedFilesListener.TOPIC, object : MarkedFilesListener {
            override fun markedFilesChanged() {
                getApplication().invokeLater {
                    if (!popup.isDisposed) {
                        showPopup(project)
                    }
                }
            }
        })
    }
}
