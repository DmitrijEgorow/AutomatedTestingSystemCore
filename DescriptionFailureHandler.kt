package ru.myitschool.lab23.core

import android.app.Instrumentation
import android.view.View
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.base.DefaultFailureHandler
import kotlin.math.min
import org.hamcrest.Matcher

class DescriptionFailureHandler(instrumentation: Instrumentation) : FailureHandler {
    private val maxErrorMessageLength = 1200
    var extraMessage = StringBuilder("")
    var delegate: DefaultFailureHandler = DefaultFailureHandler(instrumentation.targetContext)

    override fun handle(error: Throwable?, viewMatcher: Matcher<View>?) {
        if (error != null) {
            val newError = Throwable(
                "$extraMessage     " + error.message?.substring(
                    0,
                    min(
                        maxErrorMessageLength,
                        error.message?.length ?: 0,
                    ),
                ) + "...",
                error.cause,
            )
            // delegate the error handling to the default handler which will throw an exception
            delegate.handle(newError, viewMatcher)
        }
    }

    fun appendExtraMessage(text: String) {
        extraMessage = extraMessage.append(text)
    }
}
