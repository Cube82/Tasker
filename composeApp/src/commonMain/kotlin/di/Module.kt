package di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import viewmodel.TasksViewModel

val appModule = module {
    singleOf(::TasksViewModel)
}