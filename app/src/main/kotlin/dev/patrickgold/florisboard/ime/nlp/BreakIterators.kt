/*
 * Copyright (C) 2022 Patrick Goldinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.florisboard.ime.nlp

import android.icu.text.BreakIterator
import dev.patrickgold.florisboard.lib.FlorisLocale
import dev.patrickgold.florisboard.lib.kotlin.guardedByLock

object BreakIterators {
    private val wordInstances = guardedByLock { mutableMapOf<FlorisLocale, BreakIterator>() }

    suspend fun <R> withWordInstance(locale: FlorisLocale, action: (BreakIterator) -> R): R {
        return wordInstances.withLock { wordInstances ->
            val cachedInstance = wordInstances[locale]
            val instance = if (cachedInstance != null) {
                cachedInstance
            } else {
                val newInstance = BreakIterator.getWordInstance(locale.base)
                wordInstances[locale] = newInstance
                newInstance
            }
            action(instance)
        }
    }
}
