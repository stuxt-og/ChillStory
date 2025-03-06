package org.stuxt.chillstory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;

class BBData{
    public final int width;
    public final int height;
    public final String path;
    public final String nick;
    public boolean showBossBar;

    public BBData(int width, int height, String path, String nick, boolean showBossBar) {
        this.width = width;
        this.height = height;
        this.path = path;
        this.nick = nick;
        this.showBossBar = showBossBar;
    }
}

public class BossBarDataManager {
    private final static ArrayList<BBData> datas = new ArrayList<>();

    public void add(int w, int h, String path, String nick, boolean showBossBar){
        datas.add(new BBData(w, h, path, nick, showBossBar));
    }

    public static boolean setShowBossBar(String nick, boolean val){
        Iterator<BBData> iterator = datas.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();

            if (buf.nick.equalsIgnoreCase(nick)){
                buf.showBossBar = val;
                return true;
            }
        }

        return false;
    }

    public static boolean getShowBossBar(String nick){
        Iterator<BBData> iterator = datas.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();

            if (buf.nick.equalsIgnoreCase(nick)){
                return buf.showBossBar;
            }
        }

        return false;
    }

    public static NickIterator nickIterator(){
        return new NickIterator(datas.iterator());
    }

    public static @Nullable String getTexturePath(String nick){
        Iterator<BBData> iterator = datas.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();

            if (buf.nick.equalsIgnoreCase(nick)){
                return buf.path;
            }
        }

        return null;
    }

    public static int getTextureWidth(String nick){
        Iterator<BBData> iterator = datas.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();

            if (buf.nick.equalsIgnoreCase(nick)){
                return buf.width;
            }
        }

        return 0;
    }

    public static int getTextureHeight(String nick){
        Iterator<BBData> iterator = datas.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();

            if (buf.nick.equalsIgnoreCase(nick)){
                return buf.height;
            }
        }

        return 0;
    }
}
