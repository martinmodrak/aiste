/*
 * Copyright (C) 2012 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
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

import cz.cuni.amis.aiste.environment.impl.AbstractPlanningController.ValidationMethod;
import cz.cuni.amis.aiste.environment.impl.DoNothingAgentController;
import cz.cuni.amis.aiste.environment.impl.JShop2Controller;
import cz.cuni.amis.aiste.environment.impl.Planning4JController;
import cz.cuni.amis.aiste.environment.impl.ReactivePlanController;
import cz.cuni.amis.aiste.execution.IAgentExecutionDescriptor;
import cz.cuni.amis.aiste.execution.impl.AgentExecutionDescriptor;
import cz.cuni.amis.aiste.execution.impl.DefaultEnvironmentExecutorFactory;
import cz.cuni.amis.aiste.execution.impl.ManualAdvanceEnvironmentExecutorFactory;
import cz.cuni.amis.aiste.experiments.AisteExperiment;
import cz.cuni.amis.aiste.experiments.AisteExperimentRunner;
import cz.cuni.amis.experiments.utils.ExperimentUtils;
import cz.cuni.amis.planning4j.IAsyncPlanner;
import cz.cuni.amis.planning4j.external.ExternalPlanner;
import cz.cuni.amis.planning4j.external.impl.itsimple.EPlannerPlatform;
import cz.cuni.amis.planning4j.external.impl.itsimple.ItSimplePlannerExecutor;
import cz.cuni.amis.planning4j.external.impl.itsimple.ItSimplePlannerInformation;
import cz.cuni.amis.planning4j.external.impl.itsimple.ItSimpleUtils;
import cz.cuni.amis.planning4j.external.impl.itsimple.PlannerListManager;
import cz.cuni.amis.planning4j.external.plannerspack.PlannersPackUtils;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Martin Cerny
 */
public class Test {

    public static void main(String args[]) throws IOException {

       
        PlannerListManager plannerManager = PlannersPackUtils.getPlannerListManager();

        ItSimplePlannerInformation info;
        if (ItSimpleUtils.getOperatingSystem() == EPlannerPlatform.LINUX) {
            info = PlannersPackUtils.getSGPlan6();
        } else {
            info = PlannersPackUtils.getMetricFF();
        }

        File plannersDirectory = new File(".");
        //The planner is extracted (only if it does not exist yet) and exec permissions are set under Linux
        plannerManager.extractAndPreparePlanner(plannersDirectory, info);

        IAsyncPlanner planner = new ExternalPlanner(new ItSimplePlannerExecutor(info, plannersDirectory));


        Planning4JController pddlController = new Planning4JController(planner, Planning4JController.ValidationMethod.ENVIRONMENT_SIMULATION_WHOLE_PLAN);
        Planning4JController pddlController2 = new Planning4JController(planner, Planning4JController.ValidationMethod.ENVIRONMENT_SIMULATION_WHOLE_PLAN);
        JShop2Controller jshopController = new JShop2Controller(ValidationMethod.ENVIRONMENT_SIMULATION_WHOLE_PLAN);
        JShop2Controller jshopController2 = new JShop2Controller(ValidationMethod.ENVIRONMENT_SIMULATION_WHOLE_PLAN);

        
        CoverGame.StaticDefs defs = CGMapReader.readMap(Test.class.getResourceAsStream("/cg_map_simple.txt"));

        CoverGame cgEnv = new CoverGame(defs);

//        ReactivePlanController<CGPairAction> reactiveController = new ReactivePlanController<CGPairAction>(new CGPairRolePlan(
//                Collections.<CGRolePlan>singletonList(new CGRoleMove(cgEnv, 2, new Loc(19,15), 0)), 
//                Collections.<CGRolePlan>singletonList(new CGRoleOverWatch(cgEnv, 3, true))));
        
        
        List<IAgentExecutionDescriptor> descriptors = Arrays.asList(new IAgentExecutionDescriptor[]{
//                    new AgentExecutionDescriptor(CGAgentType.getInstance(), new DoNothingAgentController(), cgEnv.getRepresentations().get(2)),
                    new AgentExecutionDescriptor(CGAgentType.getInstance(), jshopController, cgEnv.getRepresentations().get(3)),
                    new AgentExecutionDescriptor(CGAgentType.getInstance(), jshopController2, cgEnv.getRepresentations().get(3)),
//                    new AgentExecutionDescriptor(CGAgentType.getInstance(), reactiveController, cgEnv.getRepresentations().get(3)),
//                    new AgentExecutionDescriptor(CGAgentType.getInstance(), pddlController, cgEnv.getRepresentations().get(3)),
//                    new AgentExecutionDescriptor(CGAgentType.getInstance(), pddlController2, cgEnv.getRepresentations().get(1))
                });
        AisteExperiment experiment = new AisteExperiment(cgEnv, descriptors,200, 30000000);

//        AisteExperimentRunner experimentRunner = new AisteExperimentRunner(new ManualAdvanceEnvironmentExecutorFactory());
        AisteExperimentRunner experimentRunner = new AisteExperimentRunner(new DefaultEnvironmentExecutorFactory());
        experimentRunner.setRandomSeed(548742L);
        
        ExperimentUtils.runExperimentsSingleThreaded(Collections.singletonList(experiment), experimentRunner);
        
    }
}
