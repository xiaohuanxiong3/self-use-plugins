package com.sqy.plugins.auto_switch_ime

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler

class CustomMoveCaretHandler : EditorActionHandler {

    private val myOriginalHandler : EditorActionHandler?

    constructor(originalHandler : EditorActionHandler) {
        myOriginalHandler = originalHandler
    }

    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        AutoSwitchIMEService.prepare(editor)
        myOriginalHandler?.execute(editor, caret,dataContext)
        AutoSwitchIMEService.handle(editor,CaretPositionChangeCause.ONE_CARET_MOVE)
    }

    override fun isEnabledForCaret(editor: Editor, caret: Caret, dataContext: DataContext?): Boolean {
        return true
    }

    private fun beforeExecute() {

    }

    private fun afterExecute() {

    }
}