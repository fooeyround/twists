package twists.minigame.lobby

import net.casual.arcade.minigame.phase.Phase

enum class LobbyPhase(override val id: String): Phase<LobbyMinigame> {
    Waiting("waiting"),
    Readying("readying"),
    Countdown("countdown") {
        override fun start(minigame: LobbyMinigame, previous: Phase<LobbyMinigame>) {
//            minigame.startCountdown()
        }
    }
}