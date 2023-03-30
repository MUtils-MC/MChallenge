package de.miraculixx.mutils.utils

import de.miraculixx.mutils.MChallenge

fun getAccountStatus() = MChallenge.bridgeAPI?.getAccountStatus() ?: false