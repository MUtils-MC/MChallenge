package de.miraculixx.mchallenge.utils

import de.miraculixx.mchallenge.MChallenge

fun getAccountStatus() = de.miraculixx.mchallenge.MChallenge.bridgeAPI?.getAccountStatus() ?: false