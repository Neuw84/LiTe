package edu.ehu.galan.lite.algorithms.ranked.unsupervised.cvalue;
/*
 * Copyright (C) 2014 Angel Conde Manjon neuw84 at gmail.com
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
import java.util.List;

/**
 * Class that given a candidate list process the CValue
 *
 * @author Angel Conde Manjon
 */
public class CValue {

    private List<Candidate> candList;

    /**
     *
     * @param candList
     */
    public CValue(List<Candidate> candList) {
        this.candList = candList;
    }

    /**
     *
     */
    public void processCValue() {
        for (Candidate cand : getCandList()) {
            for (Candidate cand2 : getCandList()) {
                if (cand != cand2) {
                    if (cand2.getText().contains(cand.getText())) {
                        cand.observeNested();
                        cand.incrementFreqNested(cand2.getFrequency());
                    }
                }
            }
            cand.getCValue();
        }
    }

    /** 
     * Returns the list of ranked candidates
     * @return the candList
     */
    public List<Candidate> getCandList() {
        return candList;
    }

    /**
     * @param candList the candList to set
     */
    public void setCandList(List<Candidate> candList) {
        this.candList = candList;
    }

  
   
}
