package com.kecsot.leitnerbox.base.detail

open class BaseDetailsFragmentEvent (
    val isSuccess: Boolean
)

class UpdateEntityFragmentEvent(isSuccess: Boolean) : BaseDetailsFragmentEvent(isSuccess)

class CreateEntityFragmentEvent(isSuccess: Boolean) : BaseDetailsFragmentEvent(isSuccess)

class DeleteEntityFragmentEvent(isSuccess: Boolean) : BaseDetailsFragmentEvent(isSuccess)

class LoadEntityFragmentEvent(isSuccess: Boolean) : BaseDetailsFragmentEvent(isSuccess)
