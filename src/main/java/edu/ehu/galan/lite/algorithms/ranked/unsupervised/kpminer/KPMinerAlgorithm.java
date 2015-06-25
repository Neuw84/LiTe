
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

package edu.ehu.galan.lite.algorithms.ranked.unsupervised.kpminer;

import KPminer.Extractor;
import edu.ehu.galan.lite.algorithms.AbstractAlgorithm;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.ListTerm;
import edu.ehu.galan.lite.model.Term;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that uses the KP-Miner algorithm for keyphrase extraction, see the
 * following paper Samhaa R. El-Beltagy and Ahmed Rafea, KP-Miner: A keyphrase
 * extraction system for English and Arabic documents, Elsevier’s Information
 * Systems Journal, 34(1), pp.132-144, 2009.
 *
 * @author Angel Conde MAnjon
 */


public class KPMinerAlgorithm extends AbstractAlgorithm {

    private transient Document doc = null;
    private final transient List<Term> termList; //gson 
    private transient Extractor ex;
    private int n = 1000;

    /**
     * Default constructor, by default the algorithm will "try" to ouput 1000
     * terms and 800 as threshold
     */
    public KPMinerAlgorithm() {
        super(false, "KP-Miner");
        termList = super.getTermList();

        /*
         Stuff that allows the redirection of KP-miner initialization 
         Systems.out to another input/output streams (do not want to log to the standard
         output > null) and then, put back the originals
         into its place
         */
        final PrintStream originalOutStream = System.out;
        final PrintStream originalErrStream = System.err;

        try {
            final PipedInputStream outPipedInputStream = new PipedInputStream();
            final PrintStream outPrintStream = new PrintStream(new PipedOutputStream(
                    outPipedInputStream));
            final BufferedReader outReader = new BufferedReader(
                    new InputStreamReader(outPipedInputStream));
            final PipedInputStream errPipedInputStream = new PipedInputStream();
            final PrintStream errPrintStream = new PrintStream(new PipedOutputStream(
                    errPipedInputStream));
            final BufferedReader errReader = new BufferedReader(
                    new InputStreamReader(errPipedInputStream));
            final Thread writingThread = new Thread(() -> {
                System.setOut(outPrintStream);
                System.setErr(errPrintStream);
                // You could also set the System.in here using a
                // PipedInputStream
                ex = new Extractor();
                ex.init();
                outPrintStream.close();
                errPrintStream.close();
            });
            //not to clean but works!
            writingThread.start();
            writingThread.join();
        }
        catch (IOException | InterruptedException e) {
        }
        finally {
            // may also want to add a catch for exceptions but it is
            // essential to restore the original System output and error
            // streams since it can be very confusing to not be able to
            // find System.out output on your console
            System.setOut(originalOutStream);
            System.setErr(originalErrStream);
            //You must close the streams which will auto flush them

        }
//        System.out.println("me llaman paco");
    }

    /**
     * by default the algorithm will "try" to output 1000 terms and 800 as
     * threshold
     *
     * @param pMaxResults - the number of results that will be returned as max
     * @param pThreshold - the threshold that the algorithm will take into
     * account
     */
    public KPMinerAlgorithm(int pMaxResults, int pThreshold) {
        super(false, "KP-Miner");
        termList = super.getTermList();
        //TODO: fix this, custom threshold,max results support
        ex = new Extractor();
        ex.init();
    }

    /**
     * Sets the threshold for the algorithm, the default is 800
     *
     * @param pThreshold
     */
    public void setThreshold(int pThreshold) {
        ex.setCutOff(pThreshold);
    }

    @Override
    public void runAlgorithm() {
        StringBuilder sb = new StringBuilder();
        for (String s : doc.getSentenceList()) {
            sb.append(s);
        }
        String[] keys = ex.getTopN(n, sb.toString(), false);
        for (String string : keys) {
            if (string != null) {
                termList.add(new Term(string));
            }
        }
        List<Term> terms = new ArrayList<>();
        for (Term string : termList) {
            terms.add(new Term(string.getTerm()));
        }
        doc.addListTerm(new ListTerm(this.getName(), terms));
        termList.clear();
    }

    @Override
    public void init(Document pDoc, String pPropsDir) {
        setDoc(pDoc);
    }

    /**
     *
     * @param pDoc
     */
    public void setDoc(Document pDoc) {
        doc = pDoc;
    }

    /**
     *
     * @return
     */
    public Document getDoc() {
        return doc;

    }

    /**
     * Changes the number of max results returned. Default 1000
     *
     * @param pNum
     */
    public void setMaxResultsReturned(int pNum) {
        n = pNum;

    }

//  
//    public static void main(String[] args) {
//        //try {
//        KPMinerAlgorithm kp = new KPMinerAlgorithm();
//        kp.init(null, null);
//
//        //String pollas = IOUtils.toString(new FileInputStream(new File("testCorpus/textAstronomy")), "UTF-8");
//        //String[] keys = ex.getTopN(1000, pollas, false);
//        //for (String string : keys) {
////                if (string != null) {
////
//        //                  System.out.println(string);
//        //            }
//        //      }
//        System.out.println("cachete con cavchete");
//
//        //} catch (FileNotFoundException ex1) {
//        //} catch (IOException ex) {
//        //}
//    }
}
