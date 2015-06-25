#import libraries

import nltk.collocations
import nltk.corpus
import collections
import sys
from nltk.collocations import *
from nltk.corpus.reader.plaintext import PlaintextCorpusReader

def f(x):
    return {
        'bigram_chi_sq': nltk.collocations.BigramAssocMeasures().chi_sq,
        'trigram_chi_sq': nltk.collocations.TrigramAssocMeasures().chi_sq,
	'bigram_pmi': nltk.collocations.BigramAssocMeasures().pmi,
	'trigram_pmi': nltk.collocations.TrigramAssocMeasures().pmi,
	'bigram_raw_freq': nltk.collocations.BigramAssocMeasures().raw_freq,
	'trigram_raw_freq': nltk.collocations.BigramAssocMeasures().raw_freq,
	'bigram_student_t': nltk.collocations.BigramAssocMeasures().student_t,
	'trigram_student_t': nltk.collocations.TrigramAssocMeasures().student_t,
	'bigram_likelihood_ratio': nltk.collocations.BigramAssocMeasures().likelihood_ratio,
	'trigram_likelihood_ratio': nltk.collocations.TrigramAssocMeasures().likelihood_ratio,
        }.get(x, nltk.collocations.BigramAssocMeasures().pmi)    # 9 is default if x not found


# -------------------------------------------------------------------------------
# main()
# -------------------------------------------------------------------------------
def main():
	corpus_root = sys.argv[1]
	num_text_files=int(sys.argv[2])
	algorithm_type=sys.argv[3]
	pmi_freq_filter=int(sys.argv[4])
	file_list=[]
	for i in range(0,num_text_files):
		file_list.append(sys.argv[5+i])
	corpus = PlaintextCorpusReader(corpus_root, '.*')
	if 'bigram' in algorithm_type: 
		measures=nltk.collocations.BigramAssocMeasures()
		finder = BigramCollocationFinder.from_words(corpus.words())
		finder.apply_freq_filter(pmi_freq_filter)
		scored = finder.score_ngrams((f(algorithm_type)))
	else: 
		measures=nltk.collocations.TrigramAssocMeasures()
		finder = TrigramCollocationFinder.from_words(corpus.words())
		finder.apply_freq_filter(pmi_freq_filter)	
		scored = finder.score_ngrams((f(algorithm_type)))

	sort= (sorted(scored , key=lambda tu: tu[1]))
	for key in sort:
		ngrams= len(key[0])
		if(ngrams == 2):
			print key[0][0] + "\t" + key[0][1] + "\t"+ str(key[1])
		else:
			print key[0][0] + "\t" + key[0][1] + "\t"+  key[0][2]+ "\t"+ str(key[1])

if __name__ == '__main__':
    main()

