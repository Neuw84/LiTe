/*
 * Copyright (C) 2015 Angel Conde Manjon neuw84 at gmail dot com
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

package edu.ehu.galan.lite;

import edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.TFIDFAlgorithm;
import edu.ehu.galan.lite.algorithms.ranked.unsupervised.cvalue.CValueAlgortithm;
import edu.ehu.galan.lite.algorithms.ranked.unsupervised.cvalue.filters.english.AdjPrepNounFilter;
import edu.ehu.galan.lite.algorithms.ranked.unsupervised.kpminer.KPMinerAlgorithm;
import edu.ehu.galan.lite.algorithms.ranked.unsupervised.rake.RakeAlgorithm;
import edu.ehu.galan.lite.algorithms.unranked.supervised.shallowParsingGrammar.cg3.ShallowParsingGrammarAlgortithm;
import edu.ehu.galan.lite.mixer.data.wikipedia.WikipediaData;
import edu.ehu.galan.lite.mixer.disambiguation.cValueWikiminerDisambiguation.CValueWikiDisambiguator;
import edu.ehu.galan.lite.mixer.mapping.wikipedia.wikiminer.WikiMinerMap;
import edu.ehu.galan.lite.mixer.relatedness.CValueWikiMinerRelationship.CValueWikiRelationship;
import edu.ehu.galan.lite.mixer.utils.DuplicateRemoval;
import edu.ehu.galan.lite.model.Corpus;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.parsers.english.PlainTextDocumentReaderLBJEn;
import edu.ehu.galan.lite.stemmers.CaseStemmer;
import edu.ehu.galan.lite.utils.AlgorithmRunner;
import edu.ehu.galan.lite.utils.wikiminer.WikiminnerHelper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.io.FileUtils;

/**
 * Example usage of LiTet API
 *
 * @author Angel Conde Manjon
 */
public class Example {

    private static Cache cache;

    public static void main(String[] args) {
        //initizalize ehcache system
        System.setProperty("net.sf.ehcache.enableShutdownHook", "true");
        if (CacheManager.getCacheManager("ehcacheLitet.xml") == null) {
            CacheManager.create("ehcacheLitet.xml");
        }
        cache = CacheManager.getInstance().getCache("LiteCache");
        //load the corpus to process
        Corpus corpus = new Corpus("en");
        //we spedify the directory and the database mapping (wikipedia in this case)
        corpus.loadCorpus("testCorpus", Document.SourceType.wikipedia);
        //will read the document using Illinois NLP utilities
        PlainTextDocumentReaderLBJEn parser = new PlainTextDocumentReaderLBJEn();
        AlgorithmRunner runner = new AlgorithmRunner();
        String resources = System.getProperty("user.dir") + "/resources/";
        //algorithms initializacion
        CValueAlgortithm cvalue = new CValueAlgortithm();
        cvalue.addNewProcessingFilter(new AdjPrepNounFilter());
        TFIDFAlgorithm tf = new TFIDFAlgorithm(new CaseStemmer(CaseStemmer.CaseType.lowercase), "en");
        ShallowParsingGrammarAlgortithm sha = new ShallowParsingGrammarAlgortithm(System.getProperty("user.dir") + "/resources/lite/" + "grammars/Cg2EnGrammar.grammar", "cg3/");
        KPMinerAlgorithm kp = new KPMinerAlgorithm();
        RakeAlgorithm ex = new RakeAlgorithm();
        ex.loadStopWordsList("resources/lite/stopWordLists/RakeStopLists/SmartStopListEn");
        ex.loadPunctStopWord("resources/lite/stopWordLists/RakeStopLists/RakePunctDefaultStopList");
        //algorithm submitting to execute them in parallel
        runner.submitAlgorithm(kp);
        runner.submitAlgorithm(cvalue);
        runner.submitAlgorithm(tf);
        runner.submitAlgorithm(ex);
        runner.submitAlgorithm(sha);
        //load stop list
        List<String> standardStop = null;
        try {
            standardStop = Files.readAllLines(Paths.get(resources + "lite/stopWordLists/standardStopList"), StandardCharsets.UTF_8);

        } catch (IOException e1x) {
            Logger.getLogger(WikiTerm.class.getName()).log(Level.SEVERE, null, e1x);
        }        
        //initialize Wikiminer helper (class that interacts with Wikiminer services)
        WikiminnerHelper helper = WikiminnerHelper.getInstance(resources);
        helper.setLanguage("en");
        //we may operate in local mode (using Wikiminer as API instead of interacting via REST api
        // helper.setLocalMode(false,"/home/angel/nfs/wikiminer/configs/wikipedia");
        WikiMinerMap wikimapping = new WikiMinerMap(resources, helper);
        CValueWikiDisambiguator disambiguator = new CValueWikiDisambiguator(resources, helper);
        CValueWikiRelationship relate = new CValueWikiRelationship(resources, helper);
        WikipediaData data = new WikipediaData(resources, helper);
        helper.openConnection();
        //process all the documents in the corpus
        while (!corpus.getDocQueue().isEmpty()) {
            Document doc = corpus.getDocQueue().poll();
            parser.readSource(doc.getPath());
            doc.setSentenceList(parser.getSentenceList());
            doc.setTokenList(parser.getTokenizedSentenceList());
            System.out.println(doc.getName());
            runner.runAlgorihms(doc, resources);
            doc.applyGlobalStopWordList(standardStop);
            doc.mapThreshold(1.9f, new String[]{"CValue"});
            doc.mapThreshold(0.00034554f, new String[]{"TFIDF"});
            doc.removeAndMixTerms();
            //map document
            wikimapping.mapCorpus(doc);
            disambiguator.disambiguateTopics(doc);
            //we may disambiguate topics that do not disambiguated correctly
            DuplicateRemoval.disambiguationRemoval(doc);
            DuplicateRemoval.topicDuplicateRemoval(doc);
            //obtain the wiki links,labels, etc
            data.processDocument(doc);
            //measure domain relatedness
            relate.relate(doc);
            //save the results
            String toJson = Document.getDocumentJson(doc);
            try {
                FileUtils.writeStringToFile(new File(doc.getName()), toJson);
            } catch (IOException ex1) {
                Logger.getLogger(WikiTerm.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        //close wikiminer connection and caches
        helper.closeConnection();
        cache.dispose();
        CacheManager.getInstance().shutdown();
    }
}
