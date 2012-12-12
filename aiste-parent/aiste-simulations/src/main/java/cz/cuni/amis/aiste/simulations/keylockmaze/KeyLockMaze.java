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
package cz.cuni.amis.aiste.simulations.keylockmaze;

import cz.cuni.amis.aiste.simulations.simplefps.*;
import cz.cuni.amis.aiste.environment.AgentInstantiationException;
import cz.cuni.amis.aiste.environment.IAgentInstantiationDescriptor;
import cz.cuni.amis.aiste.environment.IAgentType;
import cz.cuni.amis.aiste.environment.IStateVariable;
import cz.cuni.amis.aiste.environment.impl.AbstractStateVariableRepresentableSynchronizedEnvironment;
import cz.cuni.amis.aiste.environment.impl.AgentInstantiationDescriptor;
import cz.cuni.amis.aiste.environment.impl.EnumStateVariable;
import cz.cuni.amis.aiste.environment.impl.IntegerStateVariable;
import cz.cuni.amis.aiste.environment.impl.SingletonAgentInstantiationDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 */
public class KeyLockMaze extends AbstractStateVariableRepresentableSynchronizedEnvironment<KeyLockAgentBody, KeyLockAction> {

    private IStateVariable playerPositionVariable;

    int numRooms = 10; //tohle klidne odstran
    
    public KeyLockMaze() {
        super(KeyLockAgentBody.class, KeyLockAction.class);

        //TODO: prostredi se musi umet reprezentovat pomoci sady state variables, to jsou vlastne pary jmeno-hodnota
        //protoze prostredi je obecne, budes je muset mit v nejakych kolekcich, asi listech nebo mapach, jak se ti to bude hodit

        playerPositionVariable = new IntegerStateVariable("playerPosition", 0, numRooms);//jsou tez EnumStateVariable, pripadne si muzes udelat i vlastni typy
        
        //promennou je potreba zaregistrovat u parent tridy
        addStateVariable(playerPositionVariable);
        //a nastavit ji (a v prubehu simulace udrzovat) hodnotu
        setStateVariableValue(playerPositionVariable, 10 /* initial position*/);
    }
    
    
    
    @Override
    protected Map<KeyLockAgentBody, Double> simulateOneStepInternal(Map<KeyLockAgentBody, KeyLockAction> actionsToPerform) {
        //krok simulace vraci reward, ktery dostali agenti za provedene akce
        //v nasem pripade je reward +100 za dojiti do cile oponenta, jinak -1 za kazdy krok (aby to motivovalo k rychlemu reseni)
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected KeyLockAgentBody createAgentBodyInternal(IAgentType type) {
        if(type != KeyLockAgentType.getInstance()){
            throw new AgentInstantiationException("Illegal agent type");
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<? extends IAgentType, ? extends IAgentInstantiationDescriptor> getInstantiationDescriptors() {
        return Collections.singletonMap(KeyLockAgentType.getInstance(), new SingletonAgentInstantiationDescriptor());
    }
    
    
}
