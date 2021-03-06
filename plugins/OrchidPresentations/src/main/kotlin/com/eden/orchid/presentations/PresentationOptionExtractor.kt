package com.eden.orchid.presentations

import com.caseyjbrooks.clog.Clog
import com.eden.orchid.api.converters.StringConverter
import com.eden.orchid.api.options.OptionExtractor
import com.eden.orchid.presentations.model.Presentation
import com.eden.orchid.presentations.model.PresentationsModel
import java.lang.reflect.Field
import javax.inject.Inject

class PresentationOptionExtractor
@Inject
constructor(
        private val converter: StringConverter,
        val model: PresentationsModel
) : OptionExtractor<Presentation>(1000) {

    override fun acceptsClass(clazz: Class<*>): Boolean {
        return clazz == Presentation::class.java
    }

    override fun getOption(field: Field, sourceObject: Any, key: String): Presentation? {
        var toReturn: Presentation? = null

        val value = converter.convert(String::class.java, sourceObject)
        if (value.first) {
            toReturn = model.presentations.getOrDefault(value.second, null)
        }

        if (toReturn == null) {
            toReturn = getDefaultValue(field)
        }

        if (toReturn == null) {
            Clog.w("Presentation with key '${value.second}' does not exist")
        }

        return toReturn
    }

    override fun getDefaultValue(field: Field): Presentation? {
        return null
    }

}
