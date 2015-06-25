package edu.ehu.galan.lite.utils.wikiminer;

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


import java.io.Serializable;

/**
 * Class that holds the results to a call of the compare service of wikiminer
 * @author Angel Conde Majon
 */
public class Compare implements Serializable{
    private static final long serialVersionUID = 1L;
    private String termNormal1;
    private String termNormal2;
    private String term1;
    private String term2; 
    private int term1Id;
    private int term2Id;
    private float disambiguationConfidence;
    private float relatedness;

    /**
     * @return the term1
     */
    public String getTerm1() {
        return term1;
    }

    /**
     * @param term1 the term1 to set
     */
    public void setTerm1(String term1) {
        this.term1 = term1;
    }

    /**
     * @return the term2
     */
    public String getTerm2() {
        return term2;
    }

    /**
     * @param term2 the term2 to set
     */
    public void setTerm2(String term2) {
        this.term2 = term2;
    }

    /**
     * @return the term1Id
     */
    public int getTerm1Id() {
        return term1Id;
    }

    /**
     * @param term1Id the term1Id to set
     */
    public void setTerm1Id(int term1Id) {
        this.term1Id = term1Id;
    }

    /**
     * @return the term2Id
     */
    public int getTerm2Id() {
        return term2Id;
    }

    /**
     * @param term2Id the term2Id to set
     */
    public void setTerm2Id(int term2Id) {
        this.term2Id = term2Id;
    }

    /**
     * @return the disambiguationConfidence
     */
    public float getDisambiguationConfidence() {
        return disambiguationConfidence;
    }

    /**
     * @param disambiguationConfidence the disambiguationConfidence to set
     */
    public void setDisambiguationConfidence(float disambiguationConfidence) {
        this.disambiguationConfidence = disambiguationConfidence;
    }

    /**
     * @return the relatedness
     */
    public float getRelatedness() {
        return relatedness;
    }

    /**
     * @param relatedness the relatedness to set
     */
    public void setRelatedness(float relatedness) {
        this.relatedness = relatedness;
    }
    
    @Override
    public String toString(){
    
        return String.format("First term= %s id= %d \nSecond term= %s id= %d \nrelatedness= %f \ndisambiguationConfidence= %f", getTerm1(), getTerm1Id(), getTerm2(), getTerm2Id(), getRelatedness(), getDisambiguationConfidence());
    }

    /**
     * @return the termNormal2
     */
    public String getTermNormal2() {
        return termNormal2;
    }

    /**
     * @param termNormal2 the termNormal2 to set
     */
    public void setTermNormal2(String termNormal2) {
        this.termNormal2 = termNormal2;
    }

    /**
     * @return the termNormal1
     */
    public String getTermNormal1() {
        return termNormal1;
    }

    /**
     * @param termNormal1 the termNormal1 to set
     */
    public void setTermNormal1(String termNormal1) {
        this.termNormal1 = termNormal1;
    }
}
