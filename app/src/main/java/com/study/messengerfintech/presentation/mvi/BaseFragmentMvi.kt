package com.study.messengerfintech.presentation.mvi

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragmentMvi<PartialState : MviPartialState,
        Intent : MviIntent,
        State : MviState,
        Effect : MviEffect>(@LayoutRes layoutId: Int) :
    Fragment(layoutId) {

    protected abstract val store: MviStore<PartialState, Intent, State, Effect>
    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable.add(
            store.uiState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::render)
        )

        compositeDisposable.add(
            store.effect
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::resolveEffect)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    protected abstract fun render(state: State)
    protected abstract fun resolveEffect(effect: Effect)
}