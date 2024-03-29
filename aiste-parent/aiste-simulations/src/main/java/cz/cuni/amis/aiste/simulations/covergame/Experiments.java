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
package cz.cuni.amis.aiste.simulations.covergame;

import cz.cuni.amis.aiste.environment.IAgentController;
import cz.cuni.amis.aiste.environment.IEnvironment;
import cz.cuni.amis.aiste.environment.impl.AbstractPlanningController;
import cz.cuni.amis.aiste.environment.impl.JShop2Controller;
import cz.cuni.amis.aiste.environment.impl.Planning4JController;
import cz.cuni.amis.aiste.execution.IAgentExecutionDescriptor;
import cz.cuni.amis.aiste.execution.impl.AgentExecutionDescriptor;
import cz.cuni.amis.aiste.execution.impl.DefaultEnvironmentExecutorFactory;
import cz.cuni.amis.aiste.experiments.AisteExperiment;
import cz.cuni.amis.aiste.experiments.AisteExperimentRunner;
import cz.cuni.amis.aiste.experiments.AisteExperimentUtils;
import cz.cuni.amis.experiments.IExperimentSuite;
import cz.cuni.amis.experiments.utils.ExperimentUtils;
import cz.cuni.amis.planning4j.IAsyncPlanner;
import cz.cuni.amis.planning4j.IPlanner;
import cz.cuni.amis.planning4j.external.ExternalPlanner;
import cz.cuni.amis.planning4j.external.impl.itsimple.EPlannerPlatform;
import cz.cuni.amis.planning4j.external.impl.itsimple.ItSimplePlannerExecutor;
import cz.cuni.amis.planning4j.external.impl.itsimple.ItSimplePlannerInformation;
import cz.cuni.amis.planning4j.external.impl.itsimple.ItSimpleUtils;
import cz.cuni.amis.planning4j.external.impl.itsimple.PlannerListManager;
import cz.cuni.amis.planning4j.external.plannerspack.PlannersPackUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Martin Cerny
 */
public class Experiments {
   private final static Logger logger = Logger.getLogger(Experiments.class);
    
  public static void main(String args[]) throws IOException {

    PlannerListManager plannerManager = PlannersPackUtils.getPlannerListManager();

        ItSimplePlannerInformation infos[];
        if(ItSimpleUtils.getOperatingSystem() == EPlannerPlatform.LINUX){
             infos = new ItSimplePlannerInformation[] {
                    PlannersPackUtils.getSGPlan6(),
                    PlannersPackUtils.getProbe(),
                    PlannersPackUtils.getMetricFF(),
                };
        } else {
             infos = new ItSimplePlannerInformation[] {
                PlannersPackUtils.getMetricFF(),
                PlannersPackUtils.getMetricFF(),
                PlannersPackUtils.getMetricFF(),
             };
        }

        File plannersDirectory = new File(".");
        //The planner is extracted (only if it does not exist yet) and exec permissions are set under Linux
        for(ItSimplePlannerInformation info : infos){
            if(info.getName().toLowerCase().contains("probe")){
                plannerManager.extractAndPreparePlanner(new File("/home/martin_cerny/seq-sat-probe"), info);   
            } else {
                plannerManager.extractAndPreparePlanner(plannersDirectory, info);
            }
        }
        
        List<IAgentController> controllers = new ArrayList<IAgentController>();
        controllers.add(new JShop2Controller(AbstractPlanningController.ValidationMethod.ENVIRONMENT_SIMULATION_WHOLE_PLAN));

        for(ItSimplePlannerInformation plannerInfo : infos){
            IAsyncPlanner pl = new ExternalPlanner(new ItSimplePlannerExecutor(plannerInfo, plannersDirectory));
            controllers.add(new Planning4JController(pl, Planning4JController.ValidationMethod.ENVIRONMENT_SIMULATION_WHOLE_PLAN));
        }
        
        List<IEnvironment> environments = new ArrayList<IEnvironment>();

        environments.add(new CoverGame(CGMapReader.readMap(Test.class.getResourceAsStream("/cg_map_simple.txt"))));
        environments.add(new CoverGame(CGMapReader.readMap(Test.class.getResourceAsStream("/cg_map_irregular.txt"))));
        environments.add(new CoverGame(CGMapReader.readMap(Test.class.getResourceAsStream("/cg_map_security.txt"))));


        List<Long> stepDelays = Arrays.asList(new Long[]{100L, 500L, 1000L, 2000L});        
        int maxSteps = 200;
        
        
        int start;
        int count;
        String suiteName = "CoverGameComplexPreliminary_Fill";
        boolean startAndCountSet;
        if(args.length >= 2){
            start = Integer.parseInt(args[0]);
            count = Integer.parseInt(args[1]);
            logger.info("Starting at " + start + " running for " + count);            
            suiteName += "_" + start + "_for_" + count;
            startAndCountSet = true;                    
       } else {
            start = 0;
            count = 0;
            startAndCountSet = false;
        }
        
//        IExperimentSuite<AisteExperiment> suite = AisteExperimentUtils.createAllPossiblePairwiseCombinationsSuite(suiteName , environments, controllers, stepDelays, maxSteps, 2 /* Five repetitions */);
        IExperimentSuite<AisteExperiment> suite = AisteExperimentUtils.createAllPossiblePairwiseCombinationsSuiteHack(suiteName , environments, controllers, stepDelays, maxSteps, 2 /* Five repetitions */);

        AisteExperimentRunner experimentRunner = new AisteExperimentRunner(new DefaultEnvironmentExecutorFactory(), maxSteps);
        experimentRunner.setRandomSeed(554853636L);        
        
        if(startAndCountSet){
            ExperimentUtils.runSuiteSingleThreaded(suite, experimentRunner, start, count);                        
        } 
        else {
            ExperimentUtils.runSuiteSingleThreaded(suite, experimentRunner);            
        }
        
    }    
}
