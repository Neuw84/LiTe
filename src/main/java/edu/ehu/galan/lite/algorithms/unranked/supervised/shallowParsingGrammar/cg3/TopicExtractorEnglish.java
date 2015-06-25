package edu.ehu.galan.lite.algorithms.unranked.supervised.shallowParsingGrammar.cg3;

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
import edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.TFIDFAlgorithm;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.Token;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper Class for ShallowParsingGrammarAlgorithm
 *
 * @author Angel Conde Manjon
 */
class TopicExtractorEnglish {

    private List<String> candidateL;
    private final List<CandidateSentence> candidateList;
    private List<String> wordL;
    private int numTotalWords;
    private List<String> book;
    private List<Token> sentence;
    private List<String> stopWords;
    private List<String> topics;
    private final Properties props;
    private Document doc;

    /**
     *
     */
    public TopicExtractorEnglish() {
        topics = new ArrayList<>();
        candidateList = new ArrayList<>();
//        topics = new ArrayList<>();
//        Path path = Paths.get("candidatesAstronomy");
//        Path path1 = Paths.get("words");
        Path path2 = Paths.get("resources/lite/stopwordLists/ShallowCleaner/english");
        try {
            //        try {
            //            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            //            decoder.onMalformedInput(CodingErrorAction.IGNORE);
            //            decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
            //            book = Files.readAllLines(Paths.get("text"), StandardCharsets.UTF_8);
            //            candidateL = Files.readAllLines(path, StandardCharsets.UTF_8);
            //
            ////            FileChannel fc = new FileInputStream("candidates").getChannel();
            ////            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            ////            Charset charset = Charset.forName("ISO-8859-1");
            ////            CharBuffer charBuffer = decoder.decode(byteBuffer);
            ////            // Read file line by line
            ////            Scanner sc = new Scanner(charBuffer).useDelimiter("\n");
            ////            candidateL = new ArrayList<>();
            ////            while (sc.hasNext()) {
            ////                String line = sc.next();
            ////                candidateL.add(line);
            ////            }
            ////            fc.close();
            stopWords = Files.readAllLines(path2, StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            Logger.getLogger(TopicExtractorEnglish.class.getName()).log(Level.SEVERE, null, ex);
        }
////            wordL = Files.readAllLines(path1, StandardCharsets.UTF_8);
////            wordList = new ArrayList<>(wordL.size() - 1);
//            candidateList = new ArrayList<>(candidateL.size());
//            lbjSentence = new ArrayList<>();
//        } catch (IOException ex) {
//            Logger.getLogger(NER.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        int i = 0;
//
////        numTotalWords = Integer.parseInt(wordL.get(0));
////        for (int i = 1; i < wordL.size(); i++) {
////            if(wordL.get(i)!=null){
////            String[] num = ((String) wordL.get(i)).split(" ");
////            wordList.add(new Word(num[0], Integer.parseInt(num[1])));
////        }
////        }
//        System.out.println("Initial load finish");
        props = new Properties();
        try {
            props.load(new FileInputStream("resources/lite/configs/general.conf"));
        }
        catch (IOException ex) {
            Logger.getLogger(TFIDFAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TopicExtractorEnglish(Document pDoc, String pPropsDir) {
        doc = pDoc;
        topics = new ArrayList<>();
        candidateList = new ArrayList<>();
        props = new Properties();
        Path path2 = Paths.get(pPropsDir + "lite/stopWordLists/ShallowCleaner/english");
        try {

            stopWords = Files.readAllLines(path2, StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            Logger.getLogger(TopicExtractorEnglish.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            props.load(new FileInputStream(pPropsDir + "lite/configs/general.conf"));
        }
        catch (IOException ex) {
            Logger.getLogger(TFIDFAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadCandidates(List<String> pCandidates) {
        candidateL = pCandidates;
        List<LinkedList<Token>> list = doc.getTokenList();
        candidateL.stream().forEach(candidate -> {
            String[] candi = candidate.split("##");
            if (Integer.parseInt(candi[2]) < list.size()) {
                CandidateSentence cand = new CandidateSentence(candi[0], Integer.parseInt(candi[2].trim()), candi[1].trim());
                LinkedList<Token> sentenceToken = list.get(Integer.parseInt(candi[2].trim()));
                for (Token token : sentenceToken) {
                    cand.cSentence.add(new ChunkedWord(token.getWordForm(), token.getChunkerTag(), token.getPosTag()));
//                    System.out.print(token.getWordForm() + "[" + token.getChunkerTag() + "," + token.getPosTag() + "] ");
                }
//                System.out.println("");
                candidateList.add(cand);
            }
        });

    }

    public void loadCandidates(String pFile) {
//        candidateL = Files.readAllLines(Paths.get(null)., StandardCharsets.UTF_8);
    }

    /**
     *
     * @param args
     */
//    public static void main(String[] args) {
////        TopicExtractorEnglish ext = new TopicExtractorEnglish();
//////        ext.tokenizeCandidates();
////        ext.extractTopics();
////        ext.cleanTopics();
//
//    }
     void extractTopics() {
        candidateList.stream().forEach((CandidateSentence sent) -> {
            String rule = sent.getRule();
//            System.out.println(i + rule);
            switch (rule) {
                case "DEF-1": {
                    processRuleIsThatCalled(sent);
                    break;
                }
                case "DEF-2": {
                    processRuleIsADet(sent);
                    break;
                }
                case "DEF-3": {
                    processRuleRefersTo(sent);
                    break;
                }
                case "DEF-4": {
                    processRuleIsReferedTo(sent);
                    break;
                }
                case "DEF-5": {
                    processRuleBeingUsedTo(sent);
                    break;
                }
                case "DEF-6": {
                    processRuleComaIEComa(sent);
                    break;
                }
                case "DEF-7": {
                    processRuleWhatIs(sent);
                    break;
                }
                case "DEF-8": {

                    break;
                }
                case "DEF-9": {

                    break;
                }

            }
        });
    }

    /**
     * # Pattern:
     *
     * @ONT-TOPIC + is|are + determiner
     */
    private void processRuleIsADet(CandidateSentence sent) {
        List<NounPhrase> npList = getNP(sent);
//        System.out.println("tes " + sent.getSentence());
        int i = 0;
        for (i = 0; i < sent.cSentence.size(); i++) {
            ChunkedWord word = sent.cSentence.get(i);
//            System.out.println(word.getWord());
            if (word.getWord().equalsIgnoreCase("is") || word.getWord().equalsIgnoreCase("are")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("DT")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("is") || word.getWord().equalsIgnoreCase("are")) {
                break;
            }
        }
        NounPhrase phrase = null;
        for (int j = 0; j < npList.size(); j++) {
            NounPhrase topic = npList.get(j);
            if (topic.getLastIdx() < i) {
                phrase = topic;
            }
        }
        if (phrase != null) {
            cleanTopic(phrase);
            printTopic(phrase);
        }

    }

    /**
     * is|are (adverb) + called|known|as|defined as +
     *
     * @ONT-TOPIC
     */
    private void processRuleIsThatCalled(CandidateSentence sent) {
        List<NounPhrase> npList = getNP(sent);
//        System.out.println("tes " + sent.getSentence());
        int i = 0;
        for (i = 0; i < sent.cSentence.size(); i++) {
            ChunkedWord word = sent.cSentence.get(i);
//            System.out.println(word.getWord());
            if (word.getWord().equalsIgnoreCase("called") || word.getWord().equalsIgnoreCase("known") || word.getWord().equalsIgnoreCase("as")) {
                if (sent.cSentence.get(i + 1) != null) {
                    break;
                }
            } else if (word.getWord().equalsIgnoreCase("defined")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getWord().equalsIgnoreCase("as")) {
                        break;
                    }
                }
                break;
            }
        }
        NounPhrase phrase = null;
        for (int j = npList.size() - 1; j >= 0; j--) {
            NounPhrase topic = npList.get(j);
            if (topic.getLastIdx() > i) {
                phrase = topic;
            }
        }
        if (phrase != null) {
            cleanTopic(phrase);
            printTopic(phrase);
        }
    }

    /**
     * @ONT-TOPIC + refers to| refer to| satisfies| satisfy +...
     *
     *
     */
    private void processRuleRefersTo(CandidateSentence sent) {
        List<NounPhrase> npList = getNP(sent);
//        System.out.println("tes " + sent.getSentence());
        int i = 0;
        for (i = 0; i < sent.cSentence.size(); i++) {
            ChunkedWord word = sent.cSentence.get(i);
//            System.out.println(word.getWord());
            if (word.getWord().equalsIgnoreCase("refer") || word.getWord().equalsIgnoreCase("refers")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("to")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("satisfies") || word.getWord().equalsIgnoreCase("satisfy")) {
                break;
            }
        }
        NounPhrase phrase = null;
        for (int j = 0; j < npList.size(); j++) {
            NounPhrase topic = npList.get(j);
            if (topic.getLastIdx() < i) {
                phrase = topic;
            }
        }
        if (phrase != null) {
            cleanTopic(phrase);
            printTopic(phrase);
        }

    }

    /**
     * # Pattern:
     *
     * @ont-topic + is|are [adverb] + being used to| used to|referred
     * to|employed to|defined as|formalized as|formalised as|described
     * as|concerned with|called
     *
     * @param sent
     */
    private void processRuleIsReferedTo(CandidateSentence sent) {
        List<NounPhrase> npList = getNP(sent);
//        System.out.println("tes " + sent.getSentence());
        int i = 0;
        for (i = 0; i < sent.cSentence.size(); i++) {
            ChunkedWord word = sent.cSentence.get(i);
//            System.out.println(word.getWord());
            if (word.getWord().equalsIgnoreCase("being")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("used")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("used")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("to")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("referred")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("to")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("employed")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("to")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("defined")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("as")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("formalized")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("as")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("formalised")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("as")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("described")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("as")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("concerned")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("with")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("called")) {

                break;
            }

        }
        NounPhrase phrase = null;
        for (int j = 0; j < npList.size(); j++) {
            NounPhrase topic = npList.get(j);
            if (topic.getLastIdx() < i) {
                phrase = topic;
            }
        }
        if (phrase != null) {
            cleanTopic(phrase);
            printTopic(phrase);

        }

    }

    /**
     * # Patroia:
     *
     * @ONT-TOPIC {is|are} [adverb] {being used to| used to|referred to|employed
     * to|defined ##as|formalized as|formalised as|described as|concerned
     * with|called
     *
     *
     */
    private void processRuleBeingUsedTo(CandidateSentence sent) {
        List<NounPhrase> npList = getNP(sent);
//        System.out.println("tes " + sent.getSentence());
        int i = 0;
        for (i = 0; i < sent.cSentence.size(); i++) {
            ChunkedWord word = sent.cSentence.get(i);
//            System.out.println(word.getWord());
            if (word.getWord().equalsIgnoreCase("is") || word.getWord().equalsIgnoreCase("are")) {
                if (sent.cSentence.get(i + 1) != null) {
                    ChunkedWord wor = sent.cSentence.get(i + 1);
                    if (wor.getPos().equalsIgnoreCase("DT")) {
                        break;
                    }
                }
            } else if (word.getWord().equalsIgnoreCase("is") || word.getWord().equalsIgnoreCase("are")) {
                break;
            }
        }
        NounPhrase phrase = null;
        for (int j = 0; j < npList.size(); j++) {
            NounPhrase topic = npList.get(j);
            if (topic.getLastIdx() < i) {
                phrase = topic;
            }
        }
        if (phrase != null) {
            cleanTopic(phrase);
            printTopic(phrase);
        }
    }

    /**
     * # Patroia:
     *
     * @ONT-TOPIC + ,i.e. + defining text
     */
    private void processRuleComaIEComa(CandidateSentence sent) {
    }

    private void processRuleWhatIs(CandidateSentence sent) {
    }

    private List<NounPhrase> getNP(CandidateSentence sent) {
        List<NounPhrase> npList = new ArrayList<>();
        int pos = 0;
        getNPR(sent, npList, pos);
        return npList;
    }

    private void getNPR(CandidateSentence sent, List<NounPhrase> npList, int pos) {
        for (int i = 0; i < sent.cSentence.size(); i++) {
            if (sent.cSentence.get(i).getChunker().equalsIgnoreCase("B-NP")) {
                NounPhrase np = new NounPhrase();
                np.addWord(sent.cSentence.get(i));
                np.setIniIdx(i);
                pos = i + 1;
                getNPRR(sent, pos, np, npList);

            }
        }
    }

    private void getNPRR(CandidateSentence sent, int pos, NounPhrase np, List<NounPhrase> npList) {
        if (pos >= sent.cSentence.size()) {
            np.setLastIdx(pos - 1);
            npList.add(np);
        } else {
            ChunkedWord word = sent.cSentence.get(pos);
            if (sent.cSentence.get(pos).getChunker().equalsIgnoreCase("B-NP")) {
                np.setLastIdx(pos - 1);
                npList.add(np);
            } else if (sent.cSentence.get(pos).getChunker().equalsIgnoreCase("I-NP")) {
                np.addWord(sent.cSentence.get(pos));
                pos++;
                getNPRR(sent, pos, np, npList);

            } else {
                np.setLastIdx(pos - 1);
                npList.add(np);
            }
        }
    }

    private void cleanTopic(NounPhrase phrase) {
        if (phrase.getPhrase().size() == 1) {
            if (phrase.getPhrase().get(0).getPos().equalsIgnoreCase("DT") || phrase.getPhrase().get(0).getPos().equalsIgnoreCase("DT")) {
                phrase.getPhrase().get(0).setWord("#####");
            } else if (stopWords.contains(phrase.getPhrase().get(0).getWord())) {
                phrase.getPhrase().get(0).setWord("#####");
            } else if (phrase.getPhrase().get(0).getWord().matches("^-?\\d+(\\.\\d+)?$")) {
                phrase.getPhrase().get(0).setWord("#####");

            }
        }
        List<ChunkedWord> list = new ArrayList<>();
        for (ChunkedWord word : phrase.getPhrase()) {
            if (word.getPos().equalsIgnoreCase("DT")) {
                list.add(word);
            }
        }
        for (int j = 0; j < list.size(); j++) {
            phrase.getPhrase().remove(list.get(j));

        }
    }

    private void printTopic(NounPhrase phrase) {
        String top = "";
        for (ChunkedWord word : phrase.getPhrase()) {
            if (!phrase.getPhrase().get(0).getWord().equalsIgnoreCase("#####")) {
//                System.out.print(word.getWord()+" ");
                top = top + " " + word.getWord();
            }
        }
        topics.add(top);
//        System.out.print("\n");
    }

    public void cleanTopics() {
        List<String> list = new ArrayList<>();
        List<String> listopics = new ArrayList<>();
        for (String string : topics) {
            //clean duplicates and stemming 
            if (!list.contains(string) && !string.equals("")) {
                list.add(string.toLowerCase());
            }
        }
        topics = list;
//        Stemmer stem = new Stemmer();
//        list = new ArrayList<>();
//        for (String string : topics) {
//            stem.add(string.toLowerCase().toCharArray(), string.length());
//            stem.stem();
//            String stemmed = stem.toString();
////            System.out.println(stemmed);
//            if (!list.contains(stemmed)) {
//                list.add(stemmed);
//
//                listopics.add(string);
//            }
//        }
//        topics = listopics;
    }

    private void saveToTmp() {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter(props.getProperty("tmpDir") + "cg3/candidates.txt");
            pw = new PrintWriter(fichero);
            for (String string : candidateL) {
                pw.println(string.split("##")[0]);
            }
            pw.close();
        }
        catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while creating temporal text file for analysis", e);

        }
        finally {
            try {
                if (null != fichero) {
                    fichero.close();
                }
            }
            catch (Exception e2) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while closing text file", e2);
            }
        }
    }

    public List<String> getTopics() {
        return topics;
    }
}
