package edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.corpus.wikipedia;

/*
 *    WikiCorpusStatistics.java
 *    Copyright (C) 2013 Angel Conde, neuw84 at gmail dot com
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * Class that given a Wikipedia processed with WikiExtractor python script, stracts some statistics
 * about the extracted Wikipedia articles
 *
 * @author Angel Conde Manjon
 */
public class WikiCorpusStatistics {

    private long numWords;
    private static int numDocs;

    /**
     * Default Constructor
     */
    public WikiCorpusStatistics() {
        numWords = 0;
        numWords = 0;
    }

    /**
     * Extracts statistics from a Wikipedia dump processed with wikiExtractor
     *
     * @param args - the directory to process
     */
    public static void main(String[] args) {
        WikiCorpusStatistics doc = new WikiCorpusStatistics();
        if (args.length == 1) {
            if (new File(args[0]).isDirectory()) {
                doc.extractDirectory(new File(args[0]));
            } else {
                System.out.println("The specified arg is not a directory");
            }
            //doc.extractDirectory(new File("/home/angel/wikiextractor"));
        } else {
            System.out.println("The args must contain the root directory of a processed \n"
                    + "Wikipedia dump with WikiExtractor");
        }

    }

    /**
     * Extracts statistics from a Wikipedia dump processed with wikiExtractor
     *
     * @param file - the root dir of the extracted files with WikiExtractor
     */
    public void extractDirectory(File file) {
        // do not try to index files that cannot be read
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                // an IO error could occur
                if (files != null) {
                    File[] listFiles = file.listFiles();
                    for (int i = 0; i < file.listFiles().length; i++) {
                        if (listFiles[i] != null) {
                            extractDirectory2(listFiles[i]);
                        }
                    }
                }
            }

        }
    }

    private void extractDirectory2(File file) {

        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (int i = 0; i < file.listFiles().length; i++) {
                File fil = listFiles[i];
                if (fil != null) {
                    boolean start = true;
                    String sb = new String();
                    String line;
                    List<String> readLines = null;
                    try {
                        readLines = FileUtils.readLines(fil, StandardCharsets.UTF_8.name());
                    } catch (IOException ex) {
                        Logger.getLogger(WikiCorpusStatistics.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    boolean star = false;

                    for (int o = 0; o < readLines.size(); o++) {
                        line = readLines.get(o);
                        if (line.matches("<doc .*?>.*?")) {
                            String[] split = line.split("<.*>");
                            // Add the path of the file as a field named "path".  Use a
                            // field that is indexed (i.e. searchable), but don't tokenize 
                            // the field into separate words and don't index term frequency
                            // or positional information:
                            if (split.length == 2) {
                                sb = sb + split[1];
                            }

                        } else if (line.matches("</doc>")) {
                            numDocs++;
                            int l = sb.toString().split("\\s+").length;
                            numWords = l;
                            sb = new String();
                            System.out.println(l);
                        } else {
                            sb = sb + (line);
                        }
                    }
                }
            }
        }
    }

    private int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }
}
