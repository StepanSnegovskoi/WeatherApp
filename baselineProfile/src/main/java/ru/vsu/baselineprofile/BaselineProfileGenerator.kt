package ru.vsu.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collect(
            packageName = "ru.vsu.weatherapp",
            includeInStartupProfile = true
        ) {
            pressHome()
            startActivityAndWait()

            /**
             * С главного экрана переходим на экран поиска с причиной "добавить в избранное"
             */
            val searchBtn = waitAndFind(By.textContains("Добавить в избранное"))
            searchBtn?.click()
            device.waitForIdle()

            val searchInput = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 5000)

            if (searchInput != null) {
                searchInput.click()
                device.waitForIdle()
                /**
                 * Вводим London
                 */
                Thread.sleep(500)
                searchInput.text = "London"

                Thread.sleep(1000)
                device.pressEnter()
            } else {
                /**
                 * Вводим London
                 */
                val placeholder = waitAndFind(By.textContains("Search"), By.textContains("Поиск"))
                placeholder?.click()
                placeholder?.text = "London"
            }

            /**
             * Ждём результат
             */
            Thread.sleep(3000)

            /**
             * Кликаем по результату, попадаем на главный экран
             */
            val cityResult = waitAndFind(By.textContains("United Kingdom"))
            cityResult?.click()

            device.waitForIdle()
            Thread.sleep(2000)

            /**
             * Кликаем на кнопку поиска на главном экране и переходим на
             * экран поиска с причиной "обычный просмотр",
             * после возвращаемся
             */
            val searchButton = waitAndFind(By.textContains("Search"), By.textContains("Поиск"))
            searchButton?.click()
            device.waitForIdle()
            Thread.sleep(2000)
            device.pressBack()
            device.pressBack()

            Thread.sleep(2000)

            /**
             * Скроллим список
             */
            val scrollable = device.findObject(By.scrollable(true))
            scrollable?.setGestureMargin(device.displayWidth / 4)
            scrollable?.scroll(Direction.DOWN, 0.5f)

            device.waitForIdle()

            /**
             * Кликаем на ранее добавленный London и попадае наэкран детальной информации,
             * после возвращаемся на главный экран
             */
            val savedCity = waitAndFind(By.textContains("London"))
            savedCity?.click()
            device.waitForIdle()
            Thread.sleep(2000)
            device.pressBack()
        }
    }

    private fun MacrobenchmarkScope.waitAndFind(vararg selectors: androidx.test.uiautomator.BySelector): UiObject2? {
        for (selector in selectors) {
            val obj = device.wait(Until.findObject(selector), 3000)
            if (obj != null) return obj
        }
        return null
    }
}