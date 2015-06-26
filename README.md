# LiTe
**A Language Independent Term Extractor/Linker/Desambiguator to Wikipedia in Java8**

Reference paper: [LiTeWi: A combined term extraction and entity linking method for eliciting educational ontologies from textbooks](http://onlinelibrary.wiley.com/doi/10.1002/asi.23398/abstract)

As the installation procedure requires some efforts, first access to the Web demo and REST Services to see whether it fit your needs: [LIDOM](http://galan.ehu.es/lidom/)

For usage and installation see the [Wiki](https://github.com/Neuw84/LiTe/wiki)

LiTe main aim is to extract terms from texts and link/disambiguate them to Wikipedia using these algorithms:
* **TFIDF**: using as corpus the Wikipedia
* **CValue**: see the  [Github proyect](https://github.com/Neuw84/CValue-TermExtraction)
* **Shallow Parsing Grammar**: see the reference paper
* **RAKE**: see the [Github proyect](https://github.com/Neuw84/RAKE-Java)
* **KPMiner**: see [KP-Miner] (http://www.sciencedirect.com/science/article/pii/S0306437908000537)
* **ChiSquare**: via call to [NLTK](http://www.nltk.org/) python toolkit
* **Likehood Ratio**: via call to NLTK python toolkit
* **T-Student:** via call to NLTK python toolkit
* **RawFrequency**: via call to NLTK python toolkit
* **Point Mutual Information**: via call to NLTK python toolkit
* **FreeLing NER**: using [FreeLing](http://nlp.lsi.upc.edu/freeling/) NER via external call

For the Disambiguation and Linker part: the [Wikiminer](http://www.sciencedirect.com/science/article/pii/S000437021200077X) is used. 

**License**: GPL V2

###TODO's
- Integrate IXA-PIPEs to only depend on OPEN-NLP Library (reduce the POM size)

###Contact information
````shell
Angel Conde
Galan Research Group
University of the Basque Country (UPV/EHU)
E-20018 Donostia-San Sebastián
angel.conde@ehu.eus
````







