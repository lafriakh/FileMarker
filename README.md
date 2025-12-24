# File Marker

**Mark files for easy access.**

File Marker is a productivity plugin for IntelliJ IDEA that allows you to create a temporary, searchable "working set" of files. It helps you navigate between specific files quickly without cluttering your editor tabs or using heavy bookmarking tools.

Your marks are **persistent per project** (saved even after restart) and the popup list **remembers its position** on your screen.

## Features

*   **Quick Mark/Unmark:** Instantly toggle the marked status of the currently opened file.
*   **Visual Navigation:** View your marked files in a popup list that mimics the native IntelliJ "Go to File" appearance (icons + grayed-out paths).
*   **Searchable:** Type immediately after opening the list to filter files by name.
*   **Persistent State:** Marks are saved inside the project's `.idea` folder, so they are ready when you return to the project.

## How to build
* Run `./gradlew buildPlugin`.
* You will find a `.zip` file in `./build/distributions/` directory.

> \* *Note: You can install the plugin in `Settings -> Plugins`. Click on the gear icon and choose `Install Plugin from Disk`*

## Standard Usage

| Action | Windows / Linux | macOS |
| :--- | :--- | :--- |
| **Mark File** | `Ctrl` + `Alt` + `A` | `Cmd` + `Opt` + `A` * |
| **Unmark File** | `Ctrl` + `Alt` + `D` | `Cmd` + `Opt` + `D` * |
| **Show List** | `Ctrl` + `Shift` + `O` | `Ctrl` + `Shift` + `O` |

> \* *Note: Default shortcuts may vary based on your OS keymap settings. You can rebind them in `Settings -> Keymap -> Plug-ins -> Marker`.*

---

## Usage with IdeaVim

If you use **IdeaVim**, you can map the plugin actions to Vim keystrokes in your `~/.ideavimrc` file using the plugin's Action IDs.

Add the following to your `~/.ideavimrc`:

```vim
" --- File Marker Plugin Mappings ---

" Example: Leader + m to Mark
nmap <leader>m <Action>(com.rachid.marker.MarkFile)

" Example: Leader + u to Unmark
nmap <leader>u <Action>(com.rachid.marker.UnmarkFile)

" Example: Leader + l to Show List
nmap <leader>l <Action>(com.rachid.marker.ShowList)
```
