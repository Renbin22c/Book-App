package com.renbin.bookproject.core.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.core.service.StorageService
import com.renbin.bookproject.data.repo.BookRepo
import com.renbin.bookproject.data.repo.BookRepoRealtimeImpl
import com.renbin.bookproject.data.repo.CategoryRepo
import com.renbin.bookproject.data.repo.CategoryRepoRealtimeImpl
import com.renbin.bookproject.data.repo.RecycleBookRealtimeImpl
import com.renbin.bookproject.data.repo.RecycleBookRepo
import com.renbin.bookproject.data.repo.UserRepo
import com.renbin.bookproject.data.repo.UserRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAuth(): AuthService {
        return AuthService()
    }

    @Provides
    @Singleton
    fun provideStorageService(): StorageService {
        return StorageService()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideUserRepoFireStore(db: FirebaseFirestore): UserRepo {
        return UserRepoImpl(db.collection("users"))
    }

    @Provides
    @Singleton
    fun provideFirebaseRealtimeRef(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference()
    }

    @Provides
    @Singleton
    fun provideCategoryRepoRealTime(db: DatabaseReference): CategoryRepo {
        return CategoryRepoRealtimeImpl(db.child("Categories"))
    }

    @Provides
    @Singleton
    fun provideBooksRepoRealTime(db: DatabaseReference): BookRepo {
        return BookRepoRealtimeImpl(db.child("Books"))
    }

    @Provides
    @Singleton
    fun provideRecycleBooksRepoRealTime(db: DatabaseReference): RecycleBookRepo {
        return RecycleBookRealtimeImpl(db.child("Recycle Books"))
    }
}
