package org.stuxt.chillstory;

import java.util.Iterator;

// required by BossBarDataManager for getting nicks
class NickIterator implements Iterator<String> {
    private Iterator<BBData> iterator;

    public NickIterator(Iterator<BBData> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public String next() {
        return iterator.next().nick;
    }
}