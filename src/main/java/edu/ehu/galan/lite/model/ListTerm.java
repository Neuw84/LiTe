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
package edu.ehu.galan.lite.model;

import java.util.List;

/**
 * A container for a list of terms that also contain the name of the algorithm that have
 * extracted those terms
 *
 * @author Angel Conde Manjon
 */
public class ListTerm {

    private final String name;
    private List<Term> termList;

    public ListTerm(String pName, List<Term> pTermList) {
        name = pName;
        termList = pTermList;

    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the termList
     */
    public List<Term> getTermList() {
        return termList;
    }

    public void setTermList(List<Term> pListTerm) {
        termList = pListTerm;
    }
}
