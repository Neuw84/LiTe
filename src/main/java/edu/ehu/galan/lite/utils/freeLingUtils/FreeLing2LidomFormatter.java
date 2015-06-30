package edu.ehu.galan.lite.utils.freeLingUtils;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Class that format the output from FreeLing parser to the output that 
 * lidom uses
 * @author Angel Conde Manjon
 */
public class FreeLing2LidomFormatter {

    private List<List<String>> lineList = null;
    private int currentLine;

    /**
     *
     */
    public FreeLing2LidomFormatter() {
        lineList = new ArrayList<>();
        lineList.add(new ArrayList<>());
        currentLine = 0;
    }

    /**
     * @return the lineList
     */
    public List<List<String>> getLineList() {
        return lineList;
    }

    /**
     * @param lineList the lineList to set
     */
    public void setLineList(List<List<String>> lineList) {
        this.lineList = lineList;
    }

    /**
     *
     */
    public void addLine() {
        lineList.add(new ArrayList<>());
        currentLine++;
    }

    /**
     *
     * @param pWord
     */
    public void addWord(String pWord) {
        lineList.get(currentLine).add(pWord);
    }

    /**
     *
     * @return
     */
    public String getContent() {
        List<String> currentL = null;
        String word;
        StringBuilder content = new StringBuilder();
        for (List<String> lineList1 : lineList) {
            currentL = lineList1;
            for (int j = 0; j < currentL.size(); j++) {
                word = currentL.get(j);
                if (j == 0) {
                    if (!word.startsWith("\"<")) {
                        content.append("\t");
                    }
                }
                content.append(word);
                content.append(" ");
            }
            content.append(System.getProperty("line.separator"));
        }
        return content.toString();
    }
    
}
