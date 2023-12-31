package com.sqy.plugins.auto_switch_ime.areaDecide

import com.intellij.lang.Language
import com.intellij.lang.java.JavaLanguage
import org.jetbrains.kotlin.idea.KotlinLanguage

enum class PsiFileLanguage(
    val language: Language,
    val areaDecider: AreaDecider
) {
    JAVA(JavaLanguage.INSTANCE,JavaAreaDecider),
    KOTLIN(KotlinLanguage.INSTANCE,KotlinAreaDecider);

    companion object {
        fun getAreaDecider(language: Language) : AreaDecider? {
            return values().filter {
                it.language == language
            }.takeIf {
                it.isNotEmpty()
            }?.get(0)?.areaDecider
        }
    }
}