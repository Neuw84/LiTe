package edu.ehu.galan.lite.model;

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

import java.io.File;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Corpus contains a List of Documents in a language
 *
 * @author Angel Conde Manjon
 */
public class Corpus {

    private ArrayDeque<Document> docList;
    private String language;
    private String rootFolder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     *
     * @param pLanguage
     */
    public Corpus(String pLanguage) {
        language = pLanguage;
        docList = new ArrayDeque<>();

    }

    /**
     *
     * @param pDocument
     */
    public void addDocument(Document pDocument) {
        getDocQueue().add(pDocument);
    }

    /**
     *
     * @return
     */
    public Iterator<Document> getIterator() {
        return getDocQueue().iterator();
    }

    /**
     * @return the docList
     */
    public ArrayDeque<Document> getDocQueue() {
        return docList;
    }

    /**
     * @param docList the docList to set
     */
    public void setDocQueue(ArrayDeque<Document> docList) {
        this.docList = docList;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Loads a corpus from a given folder and scans recursively all the documents inside it,
     * then it reorder using the file name (uses FilesUtils from CommonsIO), you need to choose
     * the source of knowledge to be used (Wikipedia or Wordnet)
     * @param pRootFolder
     * @param pType 
     */
    
    public void loadCorpus(String pRootFolder,Document.SourceType pType) {
        File file = new File(pRootFolder);
        rootFolder = pRootFolder;
        Collection<File> documents = FileUtils.listFiles(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        documents.stream().sorted(( d1,  d2) ->  d1.getName().compareTo(d2.getName())).forEachOrdered((f) -> docList.add(new Document(f.getAbsolutePath(), f.getName())));
    }

    /**
     * @return the rootFolder
     */
    public String getRootFolder() {
        return rootFolder;
    }

    /**
     * @param rootFolder the rootFolder to set
     */
    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

}
