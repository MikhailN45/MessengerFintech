package com.study.messengerfintech.presentation.fragments

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.study.messengerfintech.presentation.state.State

abstract class FragmentMVI<T : State> : Fragment {
    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)
    abstract fun render(state: T)
}