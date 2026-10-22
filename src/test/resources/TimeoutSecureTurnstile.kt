package io.jumpco.open.kfsm.async

// tag::states-events[]
enum class SecureTurnstileEvents {
    CARD,
    PASS
}
// end::states-events[]

enum class SecureTurnstileStates {
    LOCKED,
    OVERRIDE,
    UNLOCKED
}

// tag::context[]
class TimerSecureTurnstile {
    var locked: Boolean = true
        private set
    var overrideActive: Boolean = false
        private set
    
    fun activateOverride() {
        overrideActive = true
        println("override activated")
    }
    
    fun cancelOverride() {
        overrideActive = false
        println("override canceled")
    }
    
    fun lock() {
        println("lock")
        locked = true
        overrideActive = false
    }
    
    fun unlock() {
        println("unlock")
        locked = false
        overrideActive = false
    }
    
    fun buzzer() {
        println("BUZZER")
    }
    
    fun invalidCard(cardId: Int) {
        println("Invalid card $cardId")
    }
    
    fun isOverrideCard(cardId: Int): Boolean {
        return cardId == 42
    }
    
    fun isValidCard(cardId: Int): Boolean {
        return cardId % 2 == 1
    }
}
// end::context[]

// tag::fsm[]
class TimerSecureTurnstileFSM(private val secureTurnstile: TimerSecureTurnstile) {
    companion object {
        val definition = stateMachine(
            SecureTurnstileStates.entries.toSet(),
            SecureTurnstileEvents.entries.toSet(),
            SecureTurnstile::class,
            Int::class
        ) {
            initialState { if (locked) SecureTurnstileStates.LOCKED else SecureTurnstileStates.UNLOCKED }
            default {
                action { _, _, _ ->
                    buzzer()
                }
            }
            onStateChange { oldState, newState ->
                println("onStateChange:$oldState -> $newState")
            }
            whenState(SecureTurnstileStates.OVERRIDE) {
                timeout(SecureTurnstileStates.LOCKED, { timeout }) {
                    println("Timeout")
                    cancelOverride()
                }
                onEvent(
                    SecureTurnstileEvents.CARD to SecureTurnstileStates.LOCKED,
                    guard = { cardId ->
                        requireNotNull(cardId)
                        isOverrideCard(cardId)
                    }
                ) {
                    cancelOverride()
                }
                onEvent(
                    SecureTurnstileEvents.CARD to SecureTurnstileStates.UNLOCKED,
                    guard = { cardId ->
                        requireNotNull(cardId)
                        !isValidCard(cardId)
                    }
                ) { cardId ->
                    println("Allowed invalid card:$cardId")
                    unlock()
                }
                onEvent(
                    SecureTurnstileEvents.CARD to SecureTurnstileStates.UNLOCKED,
                    guard = { cardId ->
                        requireNotNull(cardId)
                        isValidCard(cardId)
                    }
                ) { cardId ->
                    println("Allowing valid card:${cardId}")
                    unlock()
                }
            }
            whenState(SecureTurnstileStates.LOCKED) {
                onEvent(
                    SecureTurnstileEvents.CARD to SecureTurnstileStates.OVERRIDE,
                    guard = { cardId ->
                        requireNotNull(cardId)
                        isOverrideCard(cardId)
                    }
                ) {
                    activateOverride()
                }
                onEvent(
                    SecureTurnstileEvents.CARD to SecureTurnstileStates.UNLOCKED,
                    guard = { cardId ->
                        requireNotNull(cardId)
                        isValidCard(cardId)
                    }
                ) {
                    unlock()
                }
                onEvent(
                    SecureTurnstileEvents.CARD,
                    guard = { cardId ->
                        requireNotNull(cardId) { "cardId is required" }
                        !isValidCard(cardId)
                    }
                ) { cardId ->
                    requireNotNull(cardId)
                    invalidCard(cardId)
                }
            }
            whenState(SecureTurnstileStates.UNLOCKED) {
                timeout(SecureTurnstileStates.LOCKED, { timeout }) {
                    println("Timeout. Locking")
                    lock()
                }
                onEvent(
                    SecureTurnstileEvents.CARD to SecureTurnstileStates.LOCKED,
                    guard = { cardId ->
                        requireNotNull(cardId)
                        isOverrideCard(cardId)
                    }
                ) {
                    lock()
                }
                onEvent(SecureTurnstileEvents.PASS to SecureTurnstileStates.LOCKED) {
                    lock()
                }
            }
        }.build()
    }
    
    private val fsm = definition.create(secureTurnstile)
    fun card(cardId: Int) = fsm.sendEvent(SecureTurnstileEvents.CARD, cardId)
    fun pass() = fsm.sendEvent(SecureTurnstileEvents.PASS)
    fun allowEvent(): Set<String> = fsm.allowed().map { it.name.lowercase() }.toSet()
}
// end::fsm[]
