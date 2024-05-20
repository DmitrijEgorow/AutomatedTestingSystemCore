package ru.myitschool.lab23.core

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.jessecorbett.diskord.bot.bot
import com.jessecorbett.diskord.bot.events
import com.jessecorbett.diskord.util.sendMessage
import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@MediumTest
class AnalyticsManager {

    private val separator = "#"

    suspend fun sendDiscordMessage(test: BaseTest, testInfo: String) {
        bot(token) {
            events {
                onReady {
                    channel(channelId)
                        .sendMessage(
                            "${test.javaClass}$separator$testInfo" +
                                    "$separator${Build.MODEL}$separator${Build.VERSION.SDK_INT}"
                        )
                }
            }
        }
    }

    companion object {
        private const val token = "API TOKEN"
        private const val channelId = "CHANNEL ID"
    }
}
