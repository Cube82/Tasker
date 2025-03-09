package pl.cube.pp_client

import androidx.compose.runtime.Composable
import di.appModule
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.KoinAppDeclaration
import view.TasksScreen

@Composable
fun App() {
    initKoin()

    TasksScreen()
}

private fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }
