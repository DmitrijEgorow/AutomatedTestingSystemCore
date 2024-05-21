package ru.myitschool.lab23.core

import android.app.Instrumentation
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultBaseUtils.matchesCheckNames
import java.security.SecureRandom
import java.util.Locale
import java.util.Random
import java.util.concurrent.TimeUnit
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.anyOf
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import ru.myitschool.lab23.MainActivity
import org.hamcrest.CoreMatchers.`is` as iz

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@MediumTest
open class BaseTest {

    val random = Random()
    val secureRandom = SecureRandom()

    private val limit = 10
    var randomDigit = 0
        private set

    private var activityScenario: ActivityScenario<MainActivity>? = null
    var handler: DescriptionFailureHandler? = null
    var uiDevice: UiDevice? = null

    var width = 0
        private set
    var height = 0
        private set

    var isTextFound = false
    var isSubTextFound = false

    private val clearAllNotificationsTextRu = "Очистить"
    private val clearAllNotificationsTextEn = "Clear"

    lateinit var appContext: Context
    lateinit var mInstrumentation: Instrumentation
    lateinit var mClipboardManager: ClipboardManager

    @Before
    fun setUp() {
        mInstrumentation = InstrumentationRegistry.getInstrumentation()
        handler = DescriptionFailureHandler(mInstrumentation)
        Espresso.setFailureHandler(handler)

        uiDevice = UiDevice.getInstance(mInstrumentation)
        uiDevice?.pressHome()

        width = uiDevice!!.displayWidth
        height = uiDevice!!.displayHeight

        val nonLocalizedContext = mInstrumentation.targetContext
        val configuration = nonLocalizedContext.resources.configuration
        configuration.setLocale(Locale.UK)
        appContext = nonLocalizedContext.createConfigurationContext(configuration)

        val intent = Intent(appContext, MainActivity::class.java)
        activityScenario = ActivityScenario.launch(intent)

        randomDigit = random.nextInt(limit)

        muteSound()
        beforeTest()
    }

    open fun beforeTest() {}

    /**
     * @param ids id of all required Views
     * @param message extra feedback message
     */
    protected fun checkInterface(ids: IntArray, message: String = "?") {
        var id = 1
        for (e in ids) {
            id *= e
        }
        if (message != "?") {
            Assert.assertNotEquals(message, 0, id.toLong())
        } else {
            Assert.assertNotEquals(0, id.toLong())
        }
    }

    @Test(timeout = MAX_TIMEOUT_MS)
    open fun launchTest() = runTest {
        addTestToStat(1)
        addTestToPass(1)
    }

    /**
     * @param text contained by View
     * @param performClick on View
     * @param shouldBeNull if true, then we require absence of this View
     */
    fun findView(
        text: String,
        performClick: Boolean = true,
        shouldBeNull: Boolean = false,
    ) = run {
        val tempView = uiDevice?.findObject(By.textContains(text))
        if ((tempView == null) == !shouldBeNull) {
            assertEquals(
                "problems with $text",
                1,
                0,
            )
            false
        } else {
            if ((tempView != null) && (performClick)) {
                tempView.click()
            }
            true
        }
    }

    @Throws(InterruptedException::class)
    fun rotateDevice(landscapeMode: Boolean) {
        if (landscapeMode) {
            activityScenario!!.onActivity { activity ->
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        } else {
            activityScenario!!.onActivity { activity ->
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    fun muteSound() {
        val audioManager =
            getApplicationContext<Context>().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND)

    }

    fun clearAllNotifications() {
        uiDevice?.openNotification()
        uiDevice?.findObject(By.textStartsWith(clearAllNotificationsTextRu))
            ?.click()
        uiDevice?.findObject(By.textStartsWith(clearAllNotificationsTextEn))
            ?.click()
    }

    fun addTestToStat(incMaxTotal: Int) {
        totalTests++
        maxGrade += incMaxTotal
    }

    fun addTestToPass(incGrade: Int) {
        passTests++
        grade += incGrade
    }

    protected companion object {
        const val APP_NAME = "Base Test"
        const val THREAD_DELAY: Long = 30_100
        const val MAX_TIMEOUT_MS: Long = 75_000
        const val INCREASED_MAX_TIMEOUT_MS: Long = 180_000
        private const val IDLING_TIMEOUT_SEC: Long = 30
        const val CHECK_INTERFACE_MESSAGE = "It seems that some UI elements are missed"

        private var grade = 0
        private var totalTests = 0
        private var maxGrade = 0
        private var passTests = 0

        private var customerNameId = 0
        private var customerBidId = 0
        private var submitBidId = 0

        private var winnerNameId = 0
        private var winnerBidId = 0

        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            AccessibilityChecks.enable()
                .setRunChecksFromRootView(true)
                .setThrowExceptionFor(AccessibilityCheckResult.AccessibilityCheckResultType.WARNING)
                .setThrowExceptionFor(AccessibilityCheckResult.AccessibilityCheckResultType.ERROR)
                .setThrowExceptionFor(AccessibilityCheckResult.AccessibilityCheckResultType.INFO)
                .setSuppressingResultMatcher(
                    matchesCheckNames(
                        anyOf(
                            iz("TouchTargetSizeCheck"),
                            iz("DuplicateSpeakableTextCheck"),
                        ),

                        ),
                )
            IdlingPolicies.setMasterPolicyTimeout(IDLING_TIMEOUT_SEC, TimeUnit.SECONDS)
            IdlingPolicies.setIdlingResourceTimeout(IDLING_TIMEOUT_SEC, TimeUnit.SECONDS)
        }

        @AfterClass
        @JvmStatic
        fun printResult() {
            tearDown()

            val results = Bundle()
            results.putInt("passTests", passTests)
            results.putInt("totalTests", totalTests)
            results.putInt("grade", grade)
            results.putInt("maxGrade", maxGrade)
            InstrumentationRegistry.getInstrumentation().addResults(results)
            Log.d("Tests", "$passTests из $totalTests тестов пройдено.")
            Log.d("Tests", "$grade из $maxGrade баллов получено.")
        }

        @JvmStatic
        fun tearDown() {
            val mInstrumentation = InstrumentationRegistry.getInstrumentation()
            val uiDevice = UiDevice.getInstance(mInstrumentation)
            uiDevice.pressHome()
        }
    }
}
