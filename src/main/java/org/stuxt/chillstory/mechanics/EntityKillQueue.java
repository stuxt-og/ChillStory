package org.stuxt.chillstory.mechanics;

import java.util.ArrayList;

public class EntityKillQueue {
    public static class Container {
        private final int id;
        private final String world;

        public Container(int id, String world) {
            this.id = id;
            this.world = world;
        }

        public int getID() {
            return id;
        }

        public String getWorld() {
            return world;
        }
    }

    private static ArrayList<Container> ents = new ArrayList();

    public static void add(int id, String world) {
        ents.add(new Container(id, world));
    }

    public static Container next() {
        return ents.iterator().next();
    }

    public static boolean hasNext() {
        return ents.iterator().hasNext();
    }

    public static void remove(Container in) {
        ents.remove(in);
    }
}
