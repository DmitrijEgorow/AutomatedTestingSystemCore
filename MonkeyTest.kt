package ru.myitschool.lab23.core

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.uiautomator.By
import java.util.Random
import kotlin.test.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@MediumTest
open class MonkeyTest : BaseTest() {

    private var eventsLimit = 10

    @Before
    override fun beforeTest() {
        super.beforeTest()
        eventsLimit = random.nextInt(20)
    }

    @Test(timeout = MAX_TIMEOUT_MS)
    fun monkeyTest() = runTest{
        addTestToStat(1)

        val random = Random()
        val actions = listOf(
            { clickRandomView() },
            { swipeUp() },
            { swipeDown() },
            { pressBackButton() }
        )

        repeat(eventsLimit) {
            val action = actions[random.nextInt(actions.size)]
            action.invoke()
        }

        addTestToPass(1)
    }

    private fun clickRandomView() {
        val view =
            uiDevice?.findObjects(By.clickable(true))?.random()
        assertNotNull(view)
        view.click()
    }

    private fun pressBackButton() {
        onView(isRoot()).perform(pressBack())
    }

    private fun swipeUp() {
        uiDevice?.swipe(200, 500, 200, height - 300, 50)
    }

    private fun swipeDown() {
        uiDevice?.swipe(200, height - 400, 200, 500, 50)
    }

}
