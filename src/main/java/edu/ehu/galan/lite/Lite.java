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

import edu.ehu.galan.lite.model.Corpus;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.utils.AlgorithmRunner;
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
        Option listAlgs = new Option("listAlgs", false, " Algorithm list names with the supported languages:\n"
                + "===================================================\n"
                + "tfidf => processes the TFIDF algorithm, process terms of the input document using the Wikipedia corpus as IDF (en,es)\n"
                + "cvalue => processes the CValue altorithm for the inputdocument (en, es)\n"
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
                System.out.println("Processing.... (it may take a while...)");
                if (line.getOptionValue('l').equals("en") || line.getOptionValue("l").equals("es")) {
                    System.out.println("Supported languages \"en\" or \"es\", however you may use the statistical algorithms via the API");
                } else {
                    line.getOptionValue('l');
                    //TODO: Finish the command line parser
                    Corpus cor = new Corpus(line.getOptionValue('l'));
                    cor.loadCorpus(line.getOptionValue('c'), Document.SourceType.wikipedia);
                    String res = line.getOptionValue('r');

                }
            }
            if (line.hasOption('c') && line.hasOption('l') && line.hasOption('r')) {
                System.out.println("Processing with default algorithms.... (it may take a while...)");
                Corpus cor = new Corpus(line.getOptionValue('l'));
                String res = line.getOptionValue('r');
                cor.loadCorpus(line.getOptionValue('c'), Document.SourceType.wikipedia);
                AlgorithmRunner runner = new AlgorithmRunner();

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
}
