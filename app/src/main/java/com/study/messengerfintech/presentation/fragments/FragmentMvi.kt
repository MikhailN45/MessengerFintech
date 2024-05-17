package com.study.messengerfintech.presentation.fragments

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.study.messengerfintech.presentation.state.State

abstract class FragmentMvi<T : State> : Fragment {
    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)
    abstract fun render(state: T)
}