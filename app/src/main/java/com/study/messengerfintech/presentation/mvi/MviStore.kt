package com.study.messengerfintech.presentation.mvi

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject


abstract class MviStore<
        PartialState : MviPartialState,
        Intent : MviIntent,
        State : MviState,
        Effect : MviEffect>(
    private val reducer: MviReducer<PartialState, State>,
    private val actor: MviActor<PartialState, Intent, State, Effect>
) {

    private val initialState: State by lazy { initialStateCreator() }
    private val compositeDisposable = CompositeDisposable()

    abstract fun initialStateCreator(): State

    private val _uiState: BehaviorSubject<State> = BehaviorSubject.createDefault(initialState)
    val uiState: Flowable<State> = _uiState.toFlowable(BackpressureStrategy.LATEST)

    private val _intent: PublishProcessor<Intent> = PublishProcessor.create()
    val intent: Flowable<Intent> = _intent

    private val _effect: PublishSubject<Effect> = PublishSubject.create()
    val effect: Flowable<Effect> = _effect.toFlowable(BackpressureStrategy.LATEST)

    init {
        subscribe()
    }

    private fun subscribe() {
        compositeDisposable.add(
            intent
                .flatMap { actor.resolve(it, _uiState.value ?: initialState) }
                .scan(initialState, reducer::reduce)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_uiState::onNext)
        )

        compositeDisposable.add(
            actor.effects.subscribe(_effect::onNext)
        )
    }

    fun postIntent(intent: Intent) {
        _intent.onNext(intent)
    }

    fun onCleared() {
        compositeDisposable.clear()
    }
}