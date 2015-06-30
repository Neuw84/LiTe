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
package edu.ehu.galan.lite;

import edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.TFIDFAlgorithm;
import edu.ehu.galan.lite.algorithms.ranked.unsupervised.cvalue.CValueAlgortithm;
import edu.ehu.galan.lite.algorithms.ranked.unsupervised.cvalue.filters.english.AdjPrepNounFilter;
import edu.ehu.galan.lite.algorithms.ranked.unsupervised.kpminer.KPMinerAlgorithm;
import edu.ehu.galan.lite.algorithms.ranked.unsupervised.rake.RakeAlgorithm;
import edu.ehu.galan.lite.algorithms.unranked.supervised.freeLingNerEn.FreeLingNerAlgorithm;
import edu.ehu.galan.lite.algorithms.unranked.supervised.shallowParsingGrammar.cg3.ShallowParsingGrammarAlgortithm;
import edu.ehu.galan.lite.mixer.data.wikipedia.WikipediaData;
import edu.ehu.galan.lite.mixer.disambiguation.cValueWikiminerDisambiguation.CValueWikiDisambiguator;
import edu.ehu.galan.lite.mixer.mapping.wikipedia.wikiminer.WikiMinerMap;
import edu.ehu.galan.lite.mixer.relatedness.CValueWikiMinerRelationship.CValueWikiRelationship;
import edu.ehu.galan.lite.mixer.utils.DuplicateRemoval;
import edu.ehu.galan.lite.model.Corpus;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.parsers.AbstractDocumentReader;
import edu.ehu.galan.lite.parsers.english.PlainTextDocumentReaderIXAEn;
import edu.ehu.galan.lite.parsers.spanish.PlainTextDocumentReaderIXAEs;
import edu.ehu.galan.lite.stemmers.CaseStemmer;
import edu.ehu.galan.lite.utils.AlgorithmRunner;
import edu.ehu.galan.lite.utils.wikiminer.WikiminnerHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Class that implements a command line interface for LiTe
 *
 * @author Angel Conde Manjon
 */
public class Lite {

    private static Cache cache;

    /**
     * Main entry point for LiTe from the command Line use -h option for help
     *
     * @param args
     */
    public static void main(String[] args) {

        // create Options object
        Options options = new Options();
        OptionGroup required = new OptionGroup();
        required.setRequired(false);
        Option corpus = new Option("c", true, "the location (directory) of the corpus to process, containing only one document at the moment");
        Option language = new Option("l", true, "the language of the corpus (lowercase ISO format, for example 'en'");
        Option algorithms = new Option("a", true, "the algorithms you want to process separated by commas: \n"
                + "see the documentation for see the available algorithms for each language");
        Option help = new Option("h", false, "print this message");
        Option resources = new Option("r", true, "the location of the lite resources folder");
        Option listAlgs = new Option("listAlgs", false, " Algorithm list names with the supported languages (remember that the cvalue will be processed chosen or not):\n"
                + "===================================================\n"
                + "tfidf => processes the TFIDF algorithm, process terms of the input document using the Wikipedia corpus as IDF (en,es)\n"
                + "cvalue => processes the CValue altorithm for the inputdocument, CValue is processed whether is chosed or not! (en, es)\n"
                + "shallow => processes the shallow parsing grammar algorithm (en)\n"
                + "rake => processes the rake algorithm (language agnostig)\n"
                + "kpminer => processes the KPMiner algorithm (en)\n"
                + "chisquare => processes the ChiSquare using the NLTK toolkit (language agnostic)\n"
                + "pmi=> processes the Point Mutual Information using the NLTK toolkit (language agnostic)\n"
                + "likehood=> processes the Likehood Ratio using the NLTK toolkit (language agnostic)\n"
                + "tstudent=> processes the T-Student using the NLTK toolkit (language agnostic)\n"
                + "rawfreq=> processes the raw frequency algorithm using the NLTK toolkit (language agnostic)\n"
                + "freelingner=> processes the FreeLing ner algorithm via external call(es, en)\n");
        required.addOption(resources);
        required.addOption(corpus);
        required.addOption(language);
        required.addOption(algorithms);
        options.addOptionGroup(required);
        options.addOption(help);
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        String[] s = new String[]{};
        // create the parser
        CommandLineParser parser = new org.apache.commons.cli.GnuParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, s);
            if (line.hasOption('c') && line.hasOption('l') && line.hasOption('a') && line.hasOption('r')) {
                if (!line.getOptionValue('l').equals("en") || !line.getOptionValue("l").equals("es")) {
                    System.out.println("Supported languages \"en\" or \"es\", however you may use the statistical algorithms via the API");
                } else {
                    String lang = line.getOptionValue('l');
                    Corpus cor = new Corpus(line.getOptionValue('l'));
                    cor.loadCorpus(line.getOptionValue('c'), Document.SourceType.wikipedia);
                    String res = line.getOptionValue('r');
                    List<String> algs = Arrays.asList(line.getOptionValue('a').split(","));
                    System.out.println("Processing.... (it may take a while...)");
                    runner(lang, res, algs, cor);
                }
            }
            if (line.hasOption('c') && line.hasOption('l') && line.hasOption('r')) {
                if (!line.getOptionValue('l').equals("en") || !line.getOptionValue("l").equals("es")) {
                    System.out.println("Supported languages \"en\" or \"es\", however you may use the statistical algorithms via the API");
                } else {
                    System.out.println("Processing with default algorithms (TFIDF/CValue).... (it may take a while...)");
                    Corpus cor = new Corpus(line.getOptionValue('l'));
                    String res = line.getOptionValue('r');
                    cor.loadCorpus(line.getOptionValue('c'), Document.SourceType.wikipedia);
                    String lang = line.getOptionValue('l');
                    List<String> algos = null;
                    switch (lang) {
                        case "es": {
                            String[] algs = {"cvalue", "tfidf", "rake"};
                            algos = Arrays.asList(algs);
                            break;
                        }
                        case "en": {
                            String[] algs = {"cvalue", "tfidf", "rake"};
                            algos = Arrays.asList(algs);
                            break;
                        }
                    }
                    runner(lang, res, algos, cor);
                }
            } else if (line.hasOption("h")) {
                formatter.printHelp("LiTe: a language indepent term extractor", options);
            } else if (line.getOptions().length == 0) {
                formatter.printHelp("LiTe: a language indepent term extractor", options);
            } else {
                System.err.println("The 'c', 'l' and 'r' arguments are required \n");
                formatter.printHelp("LiTe: a language indepent term extractor", options);
            }
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage() + "\n");
            formatter.printHelp("LiTe: a language indepent term extractor", options);

        }

    }

    private static void runner(String lang, String resources, List<String> algs, Corpus corpus) {
        System.setProperty("net.sf.ehcache.enableShutdownHook", "true");
        if (CacheManager.getCacheManager("ehcacheLitet.xml") == null) {
            CacheManager.create("ehcacheLitet.xml");
        }
        cache = CacheManager.getInstance().getCache("LiteCache");
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(resources + "lite/configs/general.conf"));
        } catch (IOException ex) {
            System.err.println("Check the resources dir: " + ex.getMessage());
        }
        AbstractDocumentReader parser = null;
        AlgorithmRunner runner = new AlgorithmRunner();
        CValueAlgortithm cvalue = new CValueAlgortithm();
        switch (lang) {
            case "en":
                cvalue.addNewProcessingFilter(new AdjPrepNounFilter());
                parser = new PlainTextDocumentReaderIXAEn();
                break;
            case "es":
                cvalue.addNewProcessingFilter(new edu.ehu.galan.lite.algorithms.ranked.unsupervised.cvalue.filters.spanish.NounAdjOpenFilter());
                parser = new PlainTextDocumentReaderIXAEs();
                break;
        }
        runner.submitAlgorithm(cvalue);
        //TODO: do this via java reflection
        for (int i = 0; i < algs.size(); i++) {
            switch (algs.get(i)) {
                case "TFIDF": {
                    TFIDFAlgorithm tf = new TFIDFAlgorithm(new CaseStemmer(CaseStemmer.CaseType.lowercase), lang);
                    runner.submitAlgorithm(tf);
                    break;
                }
                case "FreeLing NER": {
                    FreeLingNerAlgorithm alg = null;
                    switch (lang) {
                        case "en":
                            alg = new FreeLingNerAlgorithm(resources + "lite" + File.separator + "configs" + File.separator + "freeling" + File.separator + "enPOSMW.cfg");
                            break;
                        case "es":
                            alg = new FreeLingNerAlgorithm(resources + "lite" + File.separator + "configs" + File.separator + "freeling" + File.separator + "esPOSMW.cfg");
                            break;
                    }
                    runner.submitAlgorithm(alg);
                    break;
                }
                case "KP-Miner": {
                    if (lang.equals("en")) {
                        KPMinerAlgorithm kp = new KPMinerAlgorithm();
                        runner.submitAlgorithm(kp);
                    }
                    break;
                }
                case "Shallow Parsing Grammar": {
                    if (lang.equals("en")) {
                        ShallowParsingGrammarAlgortithm a = new ShallowParsingGrammarAlgortithm(resources + "lite" + File.separator + "grammars" + File.separator + "Cg2EnGrammar.grammar", props.getProperty("tmpDir") + File.separator + "cg3");
                        runner.submitAlgorithm(a);
                    }
                    break;
                }
                case "RAKE": {
                    RakeAlgorithm ex = new RakeAlgorithm();
                    switch (lang) {
                        case "en":
                            ex.loadStopWordsList(resources + "lite/stopWordLists/RakeStopLists/SmartStopListEn");
                            break;
                        case "es":
                            ex.loadStopWordsList(resources + "lite/stopWordLists/RakeStopLists/SpanishCustomEs");
                            break;
                    }
                    ex.loadPunctStopWord(resources + "lite/stopWordLists/RakeStopLists/RakePunctDefaultStopList");
                    runner.submitAlgorithm(ex);
                    break;
                }
            }
        }
        //load stop list
        List<String> standardStop = null;
        try {
            standardStop = Files.readAllLines(Paths.get(resources + "lite/stopWordLists/standardStopList"), StandardCharsets.UTF_8);

        } catch (IOException e1x) {
            System.err.println("Check your resources dir: " + e1x.getMessage());
        }
        WikiminnerHelper helper = WikiminnerHelper.getInstance(resources);
        helper.setLanguage(lang);
        //we may operate in local mode (using Wikiminer as API instead of interacting via REST api        
        helper.setLocalMode(props.getProperty("localMode").equals("true"), "/home/angel/nfs/wikiminer/configs/wikipedia");
        WikiMinerMap wikimapping = new WikiMinerMap(resources, helper);
        CValueWikiDisambiguator disambiguator = new CValueWikiDisambiguator(resources, helper);
        CValueWikiRelationship relate = new CValueWikiRelationship(resources, helper);
        WikipediaData data = new WikipediaData(resources, helper);
        helper.openConnection();
        while (!corpus.getDocQueue().isEmpty()) {
            Document doc = corpus.getDocQueue().poll();
            doc.setSource(Document.SourceType.wikipedia);
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
            System.out.println(Document.getDocumentJson(doc));
        }
        if (props.getProperty("localMode").equals("true")) {
            helper.closeWikipedia();
        } else {
            helper.closeConnection();
        }

    }
}
