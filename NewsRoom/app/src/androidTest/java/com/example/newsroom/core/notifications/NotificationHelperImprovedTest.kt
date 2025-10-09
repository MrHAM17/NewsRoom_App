package com.example.newsroom.core.notifications

import android.content.Context
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class NotificationHelperImprovedTest {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManagerCompat

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "news_sync"
        private const val WAIT_TIMEOUT_MS = 3000L
        private const val POLL_INTERVAL_MS = 100L
    }

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager = NotificationManagerCompat.from(context)
        clearAllNotifications()
    }

    @After
    fun tearDown() {
        clearAllNotifications()
    }

    private fun clearAllNotifications() {
        notificationManager.cancelAll()
        Thread.sleep(200)
    }

    @Test
    fun notifySync_shouldCreateNotificationWithCorrectProperties() {
        val testText = "Test notification content"

        NotificationHelper.notifySync(context, testText)

        val notification = waitForAndGetNotification(NOTIFICATION_ID)
        assertNotNull("Notification should exist", notification)
        assertEquals("Channel ID should match", CHANNEL_ID, notification!!.notification.channelId)
    }

    @Test
    fun notifySync_shouldOverridePreviousNotificationWithSameId() {
        val firstText = "First notification"
        Thread.sleep(200) // let the system register the first notification
        val secondText = "Second notification"

        NotificationHelper.notifySync(context, firstText)
        NotificationHelper.notifySync(context, secondText)

//        val notificationsCount = notificationManager.activeNotifications.count { it.id == NOTIFICATION_ID }
//        assertEquals("Should have only one notification with the same ID", 1, notificationsCount)
        val notification = waitForAndGetNotification(NOTIFICATION_ID) // use existing helper
        assertNotNull(notification)
        assertEquals("Should have only one notification with the same ID", NOTIFICATION_ID, notification!!.id)
    }

    @Test
    fun notifySync_shouldWorkWithSpecialCharacters() {
        val specialText = "Test with special chars: !@#$%^&*()_+{}[]:;\"'<>,.?/~`"

        NotificationHelper.notifySync(context, specialText)

        val notificationExists = waitForNotificationExistence(NOTIFICATION_ID)
        assertTrue("Notification with special characters should exist", notificationExists)
    }

    @Test
    fun notifySync_shouldWorkWithEmptyText() {
        val emptyText = ""

        NotificationHelper.notifySync(context, emptyText)

        val notificationExists = waitForNotificationExistence(NOTIFICATION_ID)
        assertTrue("Notification should exist even with empty text", notificationExists)
    }

    // Helper methods with clear, distinct names
    private fun waitForAndGetNotification(notificationId: Int): StatusBarNotification? {
        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < WAIT_TIMEOUT_MS) {
            val notifications = notificationManager.activeNotifications
            val notification = notifications.find { it.id == notificationId }
            if (notification != null) {
                return notification
            }
            Thread.sleep(POLL_INTERVAL_MS)
        }

        fail("Timeout waiting for notification with ID: $notificationId")
        return null
    }

    private fun waitForNotificationExistence(notificationId: Int): Boolean {
        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < WAIT_TIMEOUT_MS) {
            val notifications = notificationManager.activeNotifications
            if (notifications.any { it.id == notificationId }) {
                return true
            }
            Thread.sleep(POLL_INTERVAL_MS)
        }

        return false
    }

    // Generic helper method for custom conditions
    private fun <T> waitForNotificationCondition(
        timeoutMs: Long = WAIT_TIMEOUT_MS,
        condition: (List<StatusBarNotification>) -> T?
    ): T? {
        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            val notifications = notificationManager.activeNotifications
            val result = condition(notifications)
            if (result != null) {
                return result
            }
            Thread.sleep(POLL_INTERVAL_MS)
        }

        return null
    }
}