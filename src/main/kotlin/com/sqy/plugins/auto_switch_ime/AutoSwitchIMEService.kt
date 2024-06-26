package com.sqy.plugins.auto_switch_ime

import com.intellij.injected.editor.EditorWindow
import com.intellij.lang.Language
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.sqy.plugins.auto_switch_ime.areaDecide.AreaDeciderDelegate
import com.sqy.plugins.support.IMESwitchSupport
import org.jetbrains.kotlin.idea.KotlinLanguage
import java.util.concurrent.ConcurrentHashMap

object AutoSwitchIMEService {

    private val caretListenerMap : ConcurrentHashMap<Editor, SwitchIMECaretListener> = EditorMap.caretListenerMap
    private val psiFileMap : ConcurrentHashMap<Editor, PsiFile> = EditorMap.psiFileMap
    private var lastMoveTime : Long = 0
    private val moveAllowInterval : Long = 300

    private val logger : Logger = Logger.getInstance(AutoSwitchIMEService::class.java)

    fun prepare(editor: Editor) {
        try {
            val targetEditor = editor.let {
                it as? EditorWindow
            }?.delegate?:editor
            caretListenerMap[targetEditor]!!.caretPositionChange = 0
        } catch (error : Throwable) {
            logger.error("caught error in prepare method",error)
        }
    }

    fun handle(editor: Editor,cause: CaretPositionChangeCause) {
        try {
            val targetEditor = editor.let {
                it as? EditorWindow
            }?.delegate?:editor
            doHandle(targetEditor, cause)
        } catch (error : Throwable) {
            logger.error("caught error in handle method",error)
        }
    }

    private fun doHandle(editor: Editor, cause: CaretPositionChangeCause) {
        val caretListener = caretListenerMap[editor]!!
        if (caretListener.caretPositionChange <= 0) {
            return
        }
        val psiFile = psiFileMap.getOrDefault(editor,null)
        psiFile?.let {
            val language = psiFile.language
            val psiElement = psiFile.findElementAt(editor.caretModel.offset)
            // 如果 psiElement为null
            if (psiElement == null) {
                handleSwitchWhenNullPsiElement(editor,language)
                return
            }
            psiElement.let {
                it as? LeafPsiElement
            }?.let {
                val isLineEnd = isLineEnd(editor.caretModel.offset,psiElement,cause)
                when(cause) {
                    CaretPositionChangeCause.MOUSE_CLIKED -> handleMouseClick(language,it,isLineEnd)
                    CaretPositionChangeCause.ONE_CARET_MOVE -> handleOneCaretMove(language,it,isLineEnd)
                    CaretPositionChangeCause.ENTER -> handleEnter(language,caretListener.caretPositionChange,it,isLineEnd)
                    CaretPositionChangeCause.TYPED -> handleType(language,it,isLineEnd)
                    else -> {}
                }
            }
        }
    }

    /**
     * 处理鼠标点击引起的光标移动
     */
    private fun handleMouseClick(language: Language, psiElement: LeafPsiElement, isLineEnd : Boolean) {
        psiElement.let {
            if (AreaDeciderDelegate.isCommentArea(language, it, isLineEnd)) {
                IMESwitchSupport.switchToZh()
            } else if (AreaDeciderDelegate.isCodeArea(language, it, isLineEnd)){
                IMESwitchSupport.switchToEn()
            }
        }
    }

    /**
     * 处理按下方向键引起的光标移动
     */
    private fun handleOneCaretMove(language: Language, psiElement: LeafPsiElement, isLineEnd : Boolean) {
        psiElement.let {
            if (System.currentTimeMillis() - lastMoveTime > moveAllowInterval) {
                if (AreaDeciderDelegate.isCommentArea(language, it, isLineEnd)) {
                    IMESwitchSupport.switchToZh()
                } else if (AreaDeciderDelegate.isCodeArea(language, it, isLineEnd)){
                    IMESwitchSupport.switchToEn()
                }
                lastMoveTime = System.currentTimeMillis()
            }
        }
    }

    /**
     * 处理 按下回车键引起的光标移动
     */
    private fun handleEnter(language: Language, caretPositionChange : Int, psiElement: LeafPsiElement, isLineEnd : Boolean) {
        if (caretPositionChange == 1) {
            IMESwitchSupport.switchToEn()
        } else if(caretPositionChange > 1){
            psiElement.let {
                AreaDeciderDelegate.handleEnterWhenMultipleCaretPositionChange(language, psiElement, isLineEnd)
            }
        }
    }

    /**
     * 处理 输入字符引起的光标移动，主要是检测以//开头的注释
     */
    private fun handleType(language: Language, psiElement: LeafPsiElement, isLineEnd : Boolean) {
        psiElement.let {
            if (isLineEnd && psiElement.treePrev is PsiErrorElement) {
                IMESwitchSupport.switchToZh()
            }
        }
    }

    /**
     * 当psiElement为null时，处理输入法切换
     */
    private fun handleSwitchWhenNullPsiElement(editor: Editor,language: Language) {
        when(language) {
            JavaLanguage.INSTANCE -> {
                IMESwitchSupport.switchToEn()
            }
            KotlinLanguage.INSTANCE -> {
                IMESwitchSupport.switchToEn()
            }
            PlainTextLanguage.INSTANCE -> {
                val placeholder = "" + editor.let {
                    it as? EditorImpl
                }?.placeholder
                when(placeholder) {
                    // commit 输入git信息 区域，暂时不做处理
                    "Commit Message" -> {

                    }
                    // editor转EditorImpl失败或 editor不是EditorImpl的实例 或 EditorImpl的placeholder为null
                    "" -> {

                    }
                }
            }
        }

    }

    // 判断光标是否在行尾
    private fun isLineEnd(offset : Int, psiElement: PsiElement?, cause: CaretPositionChangeCause) : Boolean {
        return psiElement is PsiWhiteSpace
                && psiElement.text.contains("\n")
                && psiElement.textRange.endOffset - psiElement.text.length + (( if (cause == CaretPositionChangeCause.TYPED)  1 else 0 )) <= offset
    }
}