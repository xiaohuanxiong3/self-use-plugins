package com.sqy.plugins.auto_switch_ime.cause

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

// TODO 目前只考虑了Java和Kotlin，有的语言单行注释不一定是 '//'，需要注意
@Deprecated("大概率要放弃此处理器，因为其功能和CustomPsiTreeChangeListener重叠，并且CustomPsiTreeChangeListener能实现地更加完善")
class CharTypedHandler : TypedHandlerDelegate() {

    override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
//        if (c == '/') {
//            AutoSwitchIMEService.prepare(editor)
//        }
        return Result.CONTINUE
    }

    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
//        if (c == '/') {
//            AutoSwitchIMEService.handle(editor, CaretPositionChangeCause.CHAR_TYPED)
//        }
        return Result.CONTINUE
    }

    override fun beforeClosingQuoteInserted(quote: CharSequence, project: Project, editor: Editor, file: PsiFile): Result {
        // println(quote)
        return TypedHandlerDelegate.Result.CONTINUE;
    }
}