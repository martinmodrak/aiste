/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.cuni.amis.aiste.simulations.fps1;

import cz.cuni.amis.aiste.environment.AgentBody;
import cz.cuni.amis.aiste.environment.IReactivePlan;
import cz.cuni.amis.aiste.environment.ISimulablePlanningRepresentation;
import java.util.List;

/**
 *
 * @author Martin Cerny
 */
public abstract class AbstractFPS1PlanningRepresentation <DOMAIN, PROBLEM, PLANNER_ACTION>  implements ISimulablePlanningRepresentation<DOMAIN, PROBLEM, PLANNER_ACTION, FPS1Action, FPS1, FPS1PlanningGoal>{

    @Override
    public boolean isGoalState(FPS1 env, AgentBody body, FPS1PlanningGoal goal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean environmentChangedConsiderablySinceLastMarker(AgentBody body) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IReactivePlan<? extends FPS1Action> evaluateReactiveLayer(AgentBody body) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FPS1PlanningGoal> getRelevantGoals(AgentBody body) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMarker(AgentBody body) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
