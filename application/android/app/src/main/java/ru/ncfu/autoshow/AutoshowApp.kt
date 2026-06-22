package ru.ncfu.autoshow

import android.app.Application
import ru.ncfu.autoshow.di.AppContainer

/** Application: создаёт контейнер зависимостей на весь жизненный цикл процесса. */
class AutoshowApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
