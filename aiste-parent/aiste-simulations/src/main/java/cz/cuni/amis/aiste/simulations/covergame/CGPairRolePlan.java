/*
 * Copyright (C) 1003 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
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
package cz.cuni.amis.aiste.simulations.covergame;

import cz.cuni.amis.aiste.environment.IReactivePlan;
import cz.cuni.amis.aiste.environment.ISimulableEnvironment;
import cz.cuni.amis.aiste.environment.ReactivePlanStatus;
import cz.cuni.amis.aiste.environment.impl.AbstractReactivePlan;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import org.apache.log4j.Logger;

/**
 *
 * @author Martin Cerny
 */
public class CGPairRolePlan extends AbstractReactivePlan<CGPairAction> {
    Queue<CGRolePlan> plansBody0;
    Queue<CGRolePlan> plansBody1;

    
    List<Queue<CGRolePlan>> allPlans;
    
    private final Logger logger = Logger.getLogger(CGPairRolePlan.class);
    
    public CGPairRolePlan(Collection<CGRolePlan> plansBody0, Collection<CGRolePlan> plansBody1) {
        if(plansBody0.isEmpty() || plansBody1.isEmpty()){
            throw new IllegalArgumentException("Plans must be non-empty");
        }
        this.plansBody0 = new ArrayDeque(plansBody0);
        this.plansBody1 = new ArrayDeque(plansBody1);
        allPlans = new ArrayList<Queue<CGRolePlan>>(1);
        allPlans.add(this.plansBody0);
        allPlans.add(this.plansBody1);
    }

    protected void clearCompletedPlans(){
        for(Queue<CGRolePlan> plans :allPlans){
            //I keep the last plan to generate any further actions after completion
            while(plans.size() > 1 && plans.peek().getStatus() == ReactivePlanStatus.COMPLETED){
                plans.poll();
            }
        }
    }
    
    @Override
    protected void updateStepForNextAction() {
        clearCompletedPlans();
        for(Queue<CGRolePlan> plans : allPlans){
            if(!plans.peek().hasActions()) {
                plans.peek().nextAction();
            } 
        }
    }

    @Override
    public CGPairAction peek() {
        clearCompletedPlans();
        CGAction action0, action1;
        if(!plansBody0.peek().hasActions()){
            action0 = CGAction.NO_OP_ACTION;
        } else {
            action0 = plansBody0.peek().peek();
        }
        if(!plansBody1.peek().hasActions()){
            action1 = CGAction.NO_OP_ACTION;
        } else {
            action1 = plansBody1.peek().peek();
        }
        return new CGPairAction(action0, action1);
    }

    @Override
    public ReactivePlanStatus getStatus() {
        ReactivePlanStatus status0 = null, status1 = null;
        if(!plansBody0.isEmpty()){
            status0 = plansBody0.peek().getStatus();
        }
        if(!plansBody1.isEmpty()){
            status1 = plansBody1.peek().getStatus();
        }
        //logger.trace("Plan statuses :" + plansBody0.peek().getStatus() + ", " + plansBody1.peek().getStatus());        
        if((status0 == null || (plansBody0.size() == 1 && status0 == ReactivePlanStatus.COMPLETED)) 
                && (status1 == null || (plansBody1.size() == 1 && status1 == ReactivePlanStatus.COMPLETED)) ){
            return ReactivePlanStatus.COMPLETED;
        }
        else if((status0 == ReactivePlanStatus.FAILED) 
                || (status1 == ReactivePlanStatus.FAILED)){
            return ReactivePlanStatus.FAILED;
        } else {
            return ReactivePlanStatus.EXECUTING;
        }
    }

    @Override
    public String toString() {
        return "0: " + plansBody0 + ", 1: " + plansBody1;
    }

    protected Queue<CGRolePlan> cloneSubplanForSimulation(Queue<CGRolePlan> originalPlan, CoverGame cg){
        Queue<CGRolePlan> plan = new ArrayDeque<CGRolePlan>(originalPlan.size());
        for(CGRolePlan subplan : originalPlan){
            plan.add(subplan.cloneForSimulation(cg));
        }
        return plan;
    }
    
    @Override
    public IReactivePlan<CGPairAction> cloneForSimulation(ISimulableEnvironment<CGPairAction> environmentCopy) {
        CoverGame cgCopy = (CoverGame)environmentCopy;
        return new CGPairRolePlan(cloneSubplanForSimulation(plansBody0, cgCopy), cloneSubplanForSimulation(plansBody1, cgCopy));
    }

    
    
    
    
}
