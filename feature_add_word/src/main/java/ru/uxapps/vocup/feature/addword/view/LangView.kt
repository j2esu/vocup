package ru.uxapps.vocup.feature.addword.view

import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.feature.addword.R

class LangView(
    private val toolbar: Toolbar,
    private val onLangClick: (Language) -> Unit
) {

    fun setLanguages(languages: List<Language>) {
        val langItem = toolbar.menu.findItem(R.id.menu_lang)
        langItem.subMenu.clear()
        languages.forEachIndexed { i, lang ->
            val item = langItem.subMenu.add(lang.nativeName).setOnMenuItemClickListener {
                onLangClick(lang)
                true
            }
            if (i == 0) {
                item.icon = toolbar.context.getDrawable(R.drawable.ic_done)?.apply {
                    setTintList(
                        ContextCompat.getColorStateList(toolbar.context, R.color.target_lang_tint)
                    )
                }
            }
        }
    }
}