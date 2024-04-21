package com.study.messengerfintech.presentation.mvi

interface MviReducer<PartialState : MviPartialState, State : MviState> {
    fun reduce(prevState: State, partialState: PartialState): State
}