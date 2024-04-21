package com.study.messengerfintech.presentation.mvi

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

abstract class MviActor<
        PartialState : MviPartialState,
        Intent : MviIntent,
        State : MviState,
        Effect : MviEffect> {

    private val _effects: PublishSubject<Effect> = PublishSubject.create()
    val effects: Flowable<Effect> = _effects.toFlowable(BackpressureStrategy.LATEST)

    abstract fun resolve(intent: Intent, state: State): Flowable<PartialState>
}