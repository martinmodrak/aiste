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
package cz.cuni.amis.aiste.simulations.spyvsspy;

import JSHOP2.Calculate;
import JSHOP2.List;
import JSHOP2.Term;
import JSHOP2.TermConstant;
import JSHOP2.TermList;
import JSHOP2.TermNumber;
import cz.cuni.amis.aiste.environment.AgentBody;
import cz.cuni.amis.pathfinding.alg.astar.AStar;
import cz.cuni.amis.pathfinding.alg.astar.AStarResult;
import cz.cuni.amis.pathfinding.map.IPFGoal;
import cz.cuni.amis.utils.heap.IHeap;
import java.util.Set;
import org.apache.log4j.Logger;
import org.omg.CORBA.Environment;

/**
 *
 * @author Martin Cerny
 */
public class FindPathCalculate extends BodySpecificCalculate{
    private final Logger logger = Logger.getLogger(FindPathCalculate.class);

    AStar<Integer> astar;
    
    
    public FindPathCalculate(SpyVsSpyJShop2Representation representation, AgentBody body) {
        super(representation,body);
        astar =  new AStar<Integer>(representation.environment.defs.mapForPathFinding);
    }

    
    
    @Override
    public Term call(List l) {
        Term from = l.get(0);
        Term to = l.get(1);
        if(! (from instanceof TermConstant) || ! (to instanceof TermConstant)){
            logger.warn(body.getId() +  ": FindPath for non-constant params: " + from.toString(getContext()) + ", " + to.toString(getContext()));            
            return TermList.NIL;
        }
        
        int fromId = representation.constantsToLocationId.get(((TermConstant)from).getIndex());
        int toId = representation.constantsToLocationId.get(((TermConstant)to).getIndex());
        
        if(logger.isDebugEnabled()){
            logger.debug(body.getId() + ": Searching for path from " + fromId + " to " + toId);
        }
        AStarResult<Integer> result = astar.findPath(new AStarGoal(fromId, toId));
        
        if(!result.isSuccess()){
            if(logger.isDebugEnabled()){
                logger.debug(body.getId() + ": Path not found");
            }
            return TermList.NIL;
        } else {
            java.util.List<Integer> path = result.getPath();
            if(logger.isDebugEnabled()){
                logger.debug(body.getId() + ": Found path with cost " + result.getCostToNode(toId));
            }
            TermList pathTerm = TermList.NIL;
            
            //Construct the list term
            for(int i = path.size() - 1; i >= 1; i--){ //the first path element is ignored, as it is the agent's current location
                pathTerm = new TermList(new TermConstant(representation.locationIdToConstants[path.get(i)]), pathTerm);
            }

            return pathTerm;
        }
    }
    
    private class AStarGoal implements IPFGoal<Integer> {

        private int start;
        private int goal;
        SpyVsSpyMapNode goalNode;

        public AStarGoal(int start, int goal) {
            this.start = start;
            this.goal = goal;
            goalNode = representation.environment.nodes.get(goal);
        }
        

        @Override
        public Integer getStart() {
            return start;
        }

        @Override
        public boolean isGoalReached(Integer node) {
            return node == goal;
        }

        @Override
        public int getEstimatedCostToGoal(Integer node) {
            SpyVsSpyMapNode n = representation.environment.nodes.get(node);
            int xDist = Math.abs(n.posX - goalNode.posX);
            int yDist = Math.abs(n.posY - goalNode.posY);
            return xDist + yDist;
        }

        @Override
        public void setOpenList(IHeap<Integer> iheap) {
        }

        @Override
        public void setCloseList(Set<Integer> set) {
        }
        
    }
    
}
