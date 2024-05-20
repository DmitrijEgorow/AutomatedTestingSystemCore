package ru.myitschool.lab23.core

import android.view.View
import android.widget.TextView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import junit.framework.TestCase.assertTrue

class DoubleComparison(
    private val text: String,
    private val subtext: String,
    private val instance: BaseTest,
) :
    ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) throw noViewFoundException
        assertTrue(view is TextView)
        val foundText = (view as TextView).text.contains(text)
        val foundSubtext = (view as TextView).text.contains(subtext)
        if (!instance.isTextFound) instance.isTextFound = foundText
        if (!instance.isSubTextFound) instance.isSubTextFound = foundSubtext
    }
}
