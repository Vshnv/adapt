package io.github.vshnv.adapt

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun test() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("io.github.vshnv.adapt.test", appContext.packageName)
    }
}