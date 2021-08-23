package io.stipop.model

data class SPSection<T>(
    val title: String = "",
    val itemList: List<T> = listOf(),
    val itemListOrientation: SectionItemListOrientation = SectionItemListOrientation.VERTICAL
)

enum class SectionItemListOrientation {
    VERTICAL,
    HORIZONTAL
}
