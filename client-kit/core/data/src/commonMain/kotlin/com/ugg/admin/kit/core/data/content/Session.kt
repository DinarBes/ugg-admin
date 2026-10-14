package com.ugg.admin.kit.core.data.content

import com.ugg.admin.infrastructure.firebase.auth.PhoneAuthService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * A live Firestore read tied to the session: it starts again for every new sign-in and rests at
 * [whenSignedOut] in between. Without it a listener opened by one admin fails with
 * PermissionDenied on sign-out and never recovers for the next one — the ViewModels outlive it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> PhoneAuthService.whileSignedIn(whenSignedOut: T, block: () -> Flow<T>): Flow<T> =
    observeUser()
        .distinctUntilChangedBy { it?.uid }
        .flatMapLatest { user -> if (user == null) flowOf(whenSignedOut) else block() }
