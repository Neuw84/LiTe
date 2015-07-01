/*
 * Copyright 2014 Rodrigo Agerri

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package edu.ehu.galan.lite.utils.ixatools.pos.lemma;

/**
 * Interface of the lemmatizer based on Dictionary lookup.
 * 
 * @author ragerri
 * @version 2014-07-08
 * 
 */
public interface DictionaryLemmatizer {

  /**
   * Lemmatize by dictionary lookup.
   * 
   * @param word
   *          the surface form word
   * @param postag
   *          the postag assigned
   * @return the lemma
   */
  String lemmatize(String word, String postag);

}
