package de.exceptionflug.haunted.phases;

import com.google.inject.Inject;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.oal.Orchestrator;
import de.exceptionflug.projectvenom.game.party.PartyContext;
import de.exceptionflug.projectvenom.game.phases.IngamePhase;
import de.exceptionflug.projectvenom.game.phases.LobbyPhase;

/**
 * Date: 10.08.2021
 *
 * @author Exceptionflug
 */
public class HauntedLobbyPhase extends LobbyPhase {

    @Inject
    public HauntedLobbyPhase(GameContext context, IngamePhase ingamePhase, Orchestrator orchestrator, PartyContext partyContext) {
        super(context, ingamePhase, orchestrator, partyContext);
    }

}
