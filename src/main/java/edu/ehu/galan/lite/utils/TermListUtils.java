
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

package edu.ehu.galan.lite.utils;

import edu.ehu.galan.lite.model.Term;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.stream.Collectors.toList;
/**
 * Utils for Termlists, getting thresholded lists and applying stopwords lists
 *
 * @author Angel Conde
 */
public class TermListUtils {

    private static final Logger logger = LoggerFactory.getLogger(TermListUtils.class);

    /**
     * Returns a list of the terms where all the scores will be > of the passed threshold
     *
     * @param pTermlist
     * @param pThreshold
     * @return
     */
    public static List<Term> getThresholdedTermList(List<Term> pTermlist, float pThreshold) {
        if (pTermlist.size() > 0) {
            if (pTermlist.get(0).getScore() != -1) {
                return pTermlist.parallelStream().filter(term -> term.getScore() > pThreshold).collect(toList());
            }
        } else {
            logger.info("You can't get a thresholded list because this is not a scored algorithm");
            return pTermlist;
        }
        logger.debug("The list was empty");
        return pTermlist;
    }

    /**
     * Apply a stopWord list to the term list using equals function
     *
     * @param pTermList
     * @param pStopwordList
     * @return
     */
    public static List<Term> applyStopwordList(List<Term> pTermList, List<String> pStopwordList) {
        //TODO: previous problems with this method using streams
        List<Term> termL = new ArrayList<>();
        for (Term ter : pTermList) {
            boolean aux=false;
            for (String s : pStopwordList) {
                if (s.equals(ter.getTerm())) {
                aux=true;
                break;
            }
            }
            if(!aux){
            termL.add(ter);
            }
            
        }
        return termL;
    }

}
