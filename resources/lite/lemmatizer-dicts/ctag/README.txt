
GALICIAN DICTIONARIES FOR LEMMATIZATION
---------------------------------------


The Galician dictionaries (galician.dict and gl-locutions.txt) contained in this distribution of the IXA pipes have been developed by the TALG Group of the University of Vigo (http://sli.uvigo.es) from several reference sources, including the Dicionario da Real Academia Galega  (http://academia.gal/dicionario/), the Vocabulario ortográfico da lingua galega (VOLGa) (http://www.realacademiagalega.org/recursos-volg), the Hunspell Spellchecker for Galician (https://github.com/meixome/hunspell-gl), the Galician dictionary distributed by Apertium (http://sourceforge.net/projects/apertium/), the Galician dictionary distributed by Freeling (http://nlp.lsi.upc.edu/freeling/), and textual and lexical resources developed by our research group (http://sli.uvigo.es). 

The format of the galician.dict dictionary consist of "word\tab\lemma\tabpos" and it can be used to perform dictionary-based lemmatization. It is distributed in a finite state automata format created using Morfologik (https://github.com/morfologik/).

Both dictionaries (galician.dict and gl-locutions.txt) are ready to use the IXA pipes (ixa2.si.ehu.es/ixa-pipes) POS-tagger (https://github.com/ixa-ehu/ixa-pipe-pos) to perform Galician dictionary-based lemmatization.  The collection of part-of speech tags used in the Galician dictionaries are based on the CTAG tagset (http://sli.uvigo.es/CTAG) from the TALG Group.

Word forms: 
    + galician.dict: 572025 word-lemma-pos combinations. 
    + gl-locutions.txt: 4447 word-lemma-pos combinations. 

Note that plain text dictionaries can also be used in their plain text form via the API (check SimpleLemmatizer class). 

Please read the LICENSE.txt and COPYING files in this tarball for terms about distribution and use of these dictionaries. 

Contact details: 
----------------
Xavier Gómez Guinovart <xgg@uvigo.es>
TALG Group
University of Vigo
