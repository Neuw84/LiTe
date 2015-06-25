package edu.ehu.galan.lite.stemmers.spanish;
/*
 *    Copyright (C) 2013 Angel Conde Manjon, neuw84 at gmail dot com
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

import edu.ehu.galan.lite.stemmers.IStemmer;
import edu.ehu.galan.lite.utils.yago2.FinalMap;
import edu.ehu.galan.lite.utils.yago2.FinalSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * Class that convert to lower case and removes plurals in the input text using Spanish language
 * conventions, gathered from http://www.wikilengua.org/ and based in "Real Academia de la Lengua"
 * recommendations. More advanced rules could be added using Regular expressions or adding POS tags
 * information (but that.... for future versions ;)
 *
 *
 * @author Angel Conde Manjon
 */
public class SpanishInflectorStemmer implements IStemmer {

    /**
     * Cuts a suffix from a string (that is the number of chars given by the suffix)
     *
     * @param s
     * @param suffix
     * @return
     */
    public String cut(String s, String suffix) {
        return (s.substring(0, s.length() - suffix.length()));
    }

    /**
     * Maps Spanish irregular plural nouns to their singular form
     */
    public Map<String, String> irregular = new FinalMap<>(
            "oes", "o", "espráis", "espray",
            "noes", "no",
            "yoes", "yos",
            "volúmenes", "volumen",
            "cracs", "crac",
            "albalaes", "albalá",
            "faralaes", "faralá",
            "clubes", "club",
            "países", "país",
            "jerséis", "jersey",
            "especímenes", "espécimen",
            "caracteres", "carácter",
            "menús", "menú",
            "regímenes", "régimen",
            "currículos", "curriculum",
            "ultimatos", "ultimátum",
            "memorandos", "memorándum",
            "referendos", "referéndum",
            "canciones", "canción",
            "sándwiches", "sándwich");

    /**
     * Contains word forms that can either be plural or singular
     */
    public Set<String> singAndPlur = new FinalSet<>(
            "dux",
            "paraguas",
            "tijeras",
            "compost",
            "test",
            "valses",
            "escolaridad",
            "análisis",
            "caries",
            "trust",
            "dosis",
            "éxtasis",
            "hipótesis",
            "metamorfosis",
            "síntesis",
            "tesis",
            "alias",
            "crisis",
            "rascacielos",
            "parabrisas",
            "sacacorchos",
            "pararrayos",
            "portaequipajes",
            "guardarropas",
            "marcapasos",
            "gafas",
            "vacaciones",
            "víveres",
            "lunes",
            "afrikáans",
            "fórceps",
            "triceps",
            "cuadriceps",
            "martes",
            "miércoles",
            "jueves",
            "viernes",
            "cumpleaños",
            "virus",
            "atlas",
            "sms",
            "déficit"
    );

    /**
     * contains special words
     */
    public Set<String> noPlural = new FinalSet<>(
            "nada",
            "nadie",
            "pereza",
            "adolescencia",
            "generosidad",
            "pánico",
            "decrepitud",
            "eternidad",
            "caos",
            "yo",
            "tu",
            "tú",
            "el",
            "él",
            "ella",
            "nosotros",
            "nosotras",
            "vosotros",
            "vosotras",
            "ellos",
            "ellas",
            "viescas"
    );
 
    @Override
    public String stem(String pString) {
        String lowerText = pString.toLowerCase();
        // Handle irregular ones
        String irreg = irregular.get(lowerText);
        if (irreg != null) {
            return (lowerText = irreg);
        }
        // Handle words that can be plural or singular
        if (singAndPlur.contains(lowerText)) {
            return lowerText;
        }
        // Handle words that do not have plural
        if (noPlural.contains(lowerText)) {
            return lowerText;
        }
        //rules start
        /////////////////
        if (lowerText.endsWith("bs")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //crac -- cracs
        if (lowerText.endsWith("cs")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //verdad -- verdades
        if (lowerText.endsWith("des")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //carriles 
        if (lowerText.endsWith("les")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //itemes
        if (lowerText.endsWith("mes")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //relojes
        if (lowerText.endsWith("jes")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //raids -- raid
        if (lowerText.endsWith("ds")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //charteres 
        if (lowerText.endsWith("res")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //camiones
        if (lowerText.endsWith("nes")) {
            return lowerText = cut(lowerText, "es");
        }

        //casa -- casas
        if (lowerText.endsWith("as")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //puf -- pufs
        if (lowerText.endsWith("fs")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //zigzag -- zigzags
        if (lowerText.endsWith("gs")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //relojes -- reloj
        if (lowerText.endsWith("jes")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //chandals
        if (lowerText.endsWith("ls")) {
            return (lowerText = cut(lowerText, "s"));
        }
        if (lowerText.endsWith("ks")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //relojes -- reloj
        if (lowerText.endsWith("les")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //items item
        if (lowerText.endsWith("ms")) {
            return (lowerText = cut(lowerText, "s"));
        }
        if (lowerText.endsWith("nes")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //chip -- chips
        if (lowerText.endsWith("ps")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //chip -- chips
        if (lowerText.endsWith("os")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //bunker -- bunkers
        if (lowerText.endsWith("rs")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //complot- complots
        if (lowerText.endsWith("ts")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //claxons eslalons...
        if (lowerText.endsWith("ns")) {
            return (lowerText = cut(lowerText, "s"));
        }
        if (lowerText.endsWith("vs")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //champús -- champu
        if (lowerText.endsWith("ús")) {
            return (lowerText = cut(lowerText, "s"));
        }

        if (lowerText.endsWith("xes")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //ley -- leyes mas irregulares
        if (lowerText.endsWith("yes")) {
            return (lowerText = cut(lowerText, "es"));
        }
        // alhelí -- alhelís
        if (lowerText.endsWith("ís")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //jabali -- jabalíes
        if (lowerText.endsWith("íes")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //rondó  -- rondoes 
        if (lowerText.endsWith("oes")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //xes 
        if (lowerText.endsWith("xes")) {
            return (lowerText = cut(lowerText, "es"));
        }
        //rondó  -- rondós 
        if (lowerText.endsWith("ós")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //bajá  -- bajás
        if (lowerText.endsWith("ás")) {
            return (lowerText = cut(lowerText, "s"));
        }
        //luz -- luces
        if (lowerText.endsWith("ces")) {
            return (lowerText = cut(lowerText, "ces").concat("z"));
        }
        //jerseis 
        if (lowerText.endsWith("éis") || lowerText.endsWith("áis") || lowerText.endsWith("óis") || lowerText.endsWith("úis")) {
            return lowerText = cut(lowerText, "xxx").concat("y");
        }
        //generic rule for es ending!
        if (lowerText.endsWith("es")) {
            return (lowerText = cut(lowerText, "s"));
        }

        //if no rule is fired just return the input
        return lowerText;
    }
}
