package com.ynov.spookify.ui

import com.ynov.spookify.ui.util.Greeting
import com.ynov.spookify.ui.util.greetingForHour
import com.ynov.spookify.ui.util.toCompactCount
import com.ynov.spookify.ui.util.toDurationText
import kotlin.test.Test
import kotlin.test.assertEquals

class FormattingTest {

    @Test
    fun `duration is shown as minutes and zero padded seconds`() {
        assertEquals("0:00", 0.toDurationText())
        assertEquals("0:05", 5.toDurationText())
        assertEquals("3:20", 200.toDurationText())
        assertEquals("56:50", 3410.toDurationText())
    }

    @Test
    fun `duration in milliseconds drops the sub-second part`() {
        assertEquals("1:40", 100_999L.toDurationText())
    }

    @Test
    fun `counts are compacted`() {
        assertEquals("999", 999.toCompactCount())
        assertEquals("1.0k", 1_000.toCompactCount())
        assertEquals("6.3k", 6_252.toCompactCount())
        assertEquals("2.5M", 2_500_000.toCompactCount())
    }

    @Test
    fun `greeting depends on the hour`() {
        assertEquals(Greeting.Evening, greetingForHour(2))
        assertEquals(Greeting.Day, greetingForHour(5))
        assertEquals(Greeting.Day, greetingForHour(17))
        assertEquals(Greeting.Evening, greetingForHour(18))
    }
}
