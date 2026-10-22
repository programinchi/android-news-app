package com.example.ch10.di

import com.example.ch10.data.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class FakeRepositoryModule {

    @Binds
    abstract fun bindFakeNewsRepository(
        impl: FakeNewsRepository
    ): NewsRepository
}