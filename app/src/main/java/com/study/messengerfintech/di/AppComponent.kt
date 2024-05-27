package com.study.messengerfintech.di

import android.content.Context
import com.study.messengerfintech.di.streams.StreamsComponent
import com.study.messengerfintech.di.chat.ChatComponent
import com.study.messengerfintech.di.main.MainComponent
import com.study.messengerfintech.di.profile.ProfileComponent
import com.study.messengerfintech.di.streamstopics.StreamsTopicsComponent
import com.study.messengerfintech.di.users.UserComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        RetrofitModule::class,
        DomainModule::class,
        RoomModule::class,
        ViewModelBuilderModule::class,
        SubcomponentsModule::class,
    ]
)
interface AppComponent {

    fun getApiUrlProvider(): UrlProvider

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    fun mainComponent(): MainComponent.Factory
    fun chatComponent(): ChatComponent.Factory
    fun userComponent(): UserComponent.Factory
    fun profileComponent(): ProfileComponent.Factory
    fun streamsComponent(): StreamsTopicsComponent.Factory
    fun channelsComponent(): StreamsComponent.Factory
}

@Module(
    subcomponents = [
        MainComponent::class,
        ChatComponent::class,
        UserComponent::class,
        ProfileComponent::class,
        StreamsTopicsComponent::class,
        StreamsComponent::class
    ]
)
object SubcomponentsModule