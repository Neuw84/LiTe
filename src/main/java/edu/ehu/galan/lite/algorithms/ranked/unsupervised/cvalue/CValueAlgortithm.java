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
import edu.ehu.galan.lite.algorithms.AbstractAlgorithm;
import edu.ehu.galan.lite.model.Term;
import edu.ehu.galan.lite.algorithms.ranked.unsupervised.cvalue.filters.ILinguisticFilter;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.ListTerm;
import static java.util.Comparator.*;
import java.util.List;
import static java.util.stream.Collectors.toList;

/**
 * CValue implementation based on Frantzi, K., Ananiadou, S. and Mima, H. (2000)
 * Automatic recognition of multi-word terms. International Journal of Digital
 * Libraries 3(2), pp.117-132.
 *
 *
 * @author Angel Conde Manjon
 */

public class CValueAlgortithm extends AbstractAlgorithm {

    private transient Document doc = null;
    private transient ProcessLinguisticFilters filters = null;
    private final transient List<Term> termList; //gson 

    /**
     *
     */
    public CValueAlgortithm() {
        super(true, "CValue");
        filters = new ProcessLinguisticFilters();
        termList = super.getTermList();
    }

    @Override
    public void init(Document pDoc, String pPropsDir) {
        setDoc(pDoc);
    }

    /**
     *
     * @param pFilter
     */
    public void addNewProcessingFilter(ILinguisticFilter pFilter) {
        filters.addFilter(pFilter);
    }

    @Override
    public void runAlgorithm() {
        if (filters.getNumberOfFilters() > 0) {
            List<Candidate> candList = filters.processText(doc.getTokenList());
            CValue cvalue = new CValue(candList);
            cvalue.processCValue();
            candList = cvalue.getCandList();
            List<Term> termLi = candList.stream().map(cand -> new Term(cand.getText().trim(), cand.getCValue())).sorted(comparing(Term::getScore)).collect(toList());
            doc.addListTerm(new ListTerm(this.getName(), termLi));
        } else {
            System.out.println("A lingustic filter is needed for CValue algorithm");
        }
    }

    /**
     *
     * @return the doc
     */
    public Document getDoc() {
        return doc;
    }

    /**
     * @param doc the doc to set
     */
    public void setDoc(Document doc) {
        this.doc = doc;
    }


}
