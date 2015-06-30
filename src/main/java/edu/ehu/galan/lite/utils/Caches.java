/*
 * Copyright (C) 2014 angel
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
package edu.ehu.galan.lite.utils;

import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.Topic;
import java.util.HashMap;

/**
 * Various caches for speed up some calculations
 *
 * @author Angel Conde Manjon
 */
public class Caches {
    private final HashMap<Integer, Topic> id2Topic = new HashMap<>();

    /**
     *
     * @param pDoc
     */
    public void initializeId2TopicMap(Document pDoc) {
        if (!id2Topic.isEmpty()) {
            id2Topic.clear();
        }
        pDoc.getTopicList().stream().forEach((Topic t) -> id2Topic.put(t.getId(), t));
    }

    /**
     *
     * @return
     */
    public HashMap<Integer, Topic> getId2TopicMap() {
        return id2Topic;
    }

    /**
     *
     */
    public void clearId2TopicMap() {
        id2Topic.clear();
    }
}
