package com.sqy.plugins.auto_switch_ime.cause

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiFile

@Deprecated("大概率要放弃此处理器，因为其功能和CustomPsiTreeChangeListener重叠，并且CustomPsiTreeChangeListener能实现地更加完善")
class EnterHandler : EnterHandlerDelegateAdapter() {

    override fun preprocessEnter(file: PsiFile, editor: Editor, caretOffset: Ref<Int>, caretAdvance: Ref<Int>, dataContext: DataContext, originalHandler: EditorActionHandler?): EnterHandlerDelegate.Result {
        // AutoSwitchIMEService.prepare(editor)
        return EnterHandlerDelegate.Result.Continue
    }

    override fun postProcessEnter(file: PsiFile, editor: Editor, dataContext: DataContext): EnterHandlerDelegate.Result {
        // AutoSwitchIMEService.handle(editor, CaretPositionChangeCause.ENTER)
        return EnterHandlerDelegate.Result.Continue
    }
}