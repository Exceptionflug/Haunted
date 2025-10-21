package de.exceptionflug.haunted.phases;

import com.google.inject.Inject;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.i18n.InternationalizationContext;
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
    public HauntedLobbyPhase(GameContext context, InternationalizationContext i18nContext, IngamePhase ingamePhase, Orchestrator orchestrator, PartyContext partyContext) {
        super(context, i18nContext, ingamePhase, orchestrator, partyContext);
    }

}
