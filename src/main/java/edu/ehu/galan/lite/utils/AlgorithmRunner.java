package edu.ehu.galan.lite.utils;

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
import edu.ehu.galan.lite.model.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that run in differents threads each term extraction algorithm/method
 *
 * @author Angel Conde Manjon
 */
public class AlgorithmRunner {

    private final List<AbstractAlgorithm> algList;
    private final Logger logger = LoggerFactory.getLogger(AlgorithmRunner.class);
    private final ExecutorService exec;
    private final CompletionService<Integer> comp;

    public AlgorithmRunner() {
        algList = new ArrayList<>();
        //we put more threads than the available processors because some algorithms will be IO limited
        exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        comp = new ExecutorCompletionService<>(exec);
    }

    public void submitAlgorithm(AbstractAlgorithm alg) {
        algList.add(alg);
    }

    /**
     * Runs the submited algorithms in parallel given a corpus
     *
     * @param pDoc
     * @param pPropsDir
     */
    public void runAlgorihms(Document pDoc, String pPropsDir) {

        List<Future<Integer>> futList = new ArrayList<>();
        List<Integer> algorithmResults = new ArrayList<>();
        logger.info("Algorithms start");
        if (algList.size() > 0) {
            for (AbstractAlgorithm algorithm : algList) {
                algorithm.init(pDoc, pPropsDir);
                futList.add(comp.submit(algorithm));
            }            
            for (Future<Integer> future : futList) {
                try {
                    logger.info(algList.get(futList.indexOf(future)).getName());
                    algorithmResults.add(future.get());
                } catch (CancellationException | ExecutionException | InterruptedException ex) {
                    logger.error("Error running the algorithms", ex);
                }
            }
            logger.info("Algorithms processed");
            //TODO For methods that run slower, it will be good to use futureable to start the mapping process in the faster ones
        } else {
            logger.info("At least one algorithm must be submited");

        }

    }

    public List<AbstractAlgorithm> getAlgorithms() {
        return algList;
    }

    public void shutdown() {
        exec.shutdown();

    }
}
