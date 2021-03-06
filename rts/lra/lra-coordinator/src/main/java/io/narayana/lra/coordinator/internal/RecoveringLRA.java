/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package io.narayana.lra.coordinator.internal;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;
import com.arjuna.ats.arjuna.logging.tsLogger;
import io.narayana.lra.coordinator.domain.model.Transaction;
import io.narayana.lra.coordinator.domain.service.LRAService;

class RecoveringLRA extends Transaction {
    /**
     * Re-creates/activates an LRA for the specified transaction Uid.
     */
    RecoveringLRA(LRAService lraService, Uid rcvUid, int theStatus) {
        super(lraService, rcvUid );

        _theStatus = theStatus ;
        _activated = activate(); // this should initialize the state
    }

    public boolean isActivated() {
        return _activated;
    }

    /**
     * Replays phase 2 of the commit protocol.
     */
    public void replayPhase2()
    {
        if (tsLogger.logger.isDebugEnabled()) {
            tsLogger.logger.debug("RecoveringLRA.replayPhase2 recovering "+get_uid()+" ActionStatus is "+ActionStatus.stringForm(_theStatus));
        }

        if ( _activated )
        {
            if ( (_theStatus == ActionStatus.PREPARED) ||
                    (_theStatus == ActionStatus.COMMITTING) ||
                    (_theStatus == ActionStatus.COMMITTED) ||
                    (_theStatus == ActionStatus.H_COMMIT) ||
                    (_theStatus == ActionStatus.H_MIXED) ||
                    (_theStatus == ActionStatus.H_HAZARD) )
            {
                super.phase2Commit( _reportHeuristics ) ;
            }
            else if ( (_theStatus == ActionStatus.ABORTED) ||
                    (_theStatus == ActionStatus.H_ROLLBACK) ||
                    (_theStatus == ActionStatus.ABORTING) ||
                    (_theStatus == ActionStatus.ABORT_ONLY) )
            {
                super.phase2Abort( _reportHeuristics ) ;
            }
            else {
                if (tsLogger.logger.isInfoEnabled()) {
                    tsLogger.logger.info("RecoveringLRA.replayPhase2: Unexpected status: "
                            + ActionStatus.stringForm(_theStatus));
                }
            }
        }
        else {
            if (tsLogger.logger.isInfoEnabled()) {
                tsLogger.logger.infof(
                        "RecoveringLRA: LRA %s not activated, unable to replay phase 2 commit, will retry later",
                        get_uid());
            }

            // Failure to activate (NB other types such as AtomicActionExpiryScanner move the log)
        }
    }

    // Current transaction status
    // (retrieved from the TransactionStatusManager)
    private int _theStatus ;

    // Flag to indicate that this transaction has been re-activated
    // successfully.
    private boolean _activated = false ;

    // whether heuristic reporting on phase 2 commit is enabled.
    private boolean _reportHeuristics = true ;

}





