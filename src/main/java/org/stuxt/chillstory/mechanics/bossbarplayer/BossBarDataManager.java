package org.stuxt.chillstory.mechanics.bossbarplayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.stuxt.chillstory.MainClass;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;

// BossBarData (client data)
class BBData{
    public final int edgesWidth;
    public final int edgesHeight;
    public final int healthBarRX; // relative
    public final int healthBarY;
    public final int healthBarW;
    public final int healthBarH;
    public final String edgesPath;
    public final String healthBarPath;

    public BBData(int edgesWidth, int edgesHeight, int healthBarRX, int healthBarY, int healthBarW, int healthBarH, String path, String healthBarPath, String nick) {
        this.edgesWidth = edgesWidth;
        this.edgesHeight = edgesHeight;
        this.healthBarRX = healthBarRX;
        this.healthBarY = healthBarY;
        this.healthBarW = healthBarW;
        this.healthBarH = healthBarH;
        this.edgesPath = path;
        this.healthBarPath = healthBarPath;
    }
}

public class BossBarDataManager {
    private static ArrayList<BBData> cl_data;

    public static void add(int w, int h, int healthBarRX, int healthBarRY, int healthBarW, int healthBarH, String edgesPath, String healthBarPath, String nick, boolean showBossBar){
        if (!MainClass.isOnServer) {
            boolean found = false;

            if (cl_data == null) {
                cl_data = new ArrayList<>();
            }
            else {
                Iterator<BBSData.Container> iterator = MainClass.bbsData_client.showData.iterator();

                while (iterator.hasNext()) {
                    BBSData.Container buf = iterator.next();

                    if (buf.nick.equalsIgnoreCase(nick)) {
                        buf.enabled = showBossBar;
                        found = true;
                        break;
                    }
                }
            }

            if(!found) {
                cl_data.add(new BBData(w, h, healthBarRX, healthBarRY, healthBarW, healthBarH, edgesPath, healthBarPath, nick));

                MainClass.bbsData_client.showData.add(new BBSData.Container(nick, showBossBar));
            }
        }
        else {
            Iterator<BBSData.Container> iterator = MainClass.bbsData_client.showData.iterator();

            while (iterator.hasNext()) {
                BBSData.Container buf = iterator.next();

                if (buf.nick.equalsIgnoreCase(nick)) {
                    buf.enabled = showBossBar;
                    return;
                }
            }

            MainClass.bbsData_server.showData.add(new BBSData.Container(nick, showBossBar));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean setShowBossBar(String nick, boolean val){
        Iterator<BBSData.Container> iterator = MainClass.bbsData_client.showData.iterator();

        while (iterator.hasNext()){
            BBSData.Container buf = iterator.next();

            if (buf.nick.equalsIgnoreCase(nick)){
                buf.enabled = val;
                return true;
            }
        }

        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean getShowBossBar(String nick){
        Iterator<BBSData.Container> iterator = MainClass.bbsData_client.showData.iterator();

        while (iterator.hasNext()){
            BBSData.Container buf = iterator.next();

            if (buf.nick.equalsIgnoreCase(nick)){
                return buf.enabled;
            }
        }

        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static Iterator<BBSData.Container> showDataIterator(){
        return MainClass.bbsData_client.showData.iterator();
    }

    @OnlyIn(Dist.CLIENT)
    public static @Nullable String getEdgesTexturePath(String nick){
        Iterator<BBData> iterator = cl_data.iterator();
        Iterator<BBSData.Container> nickIterator = MainClass.bbsData_client.showData.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();
            String sbuf = nickIterator.next().nick;

            if (sbuf.equalsIgnoreCase(nick)){
                return buf.edgesPath;
            }
        }

        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getEdgesTextureWidth(String nick){
        Iterator<BBData> iterator = cl_data.iterator();
        Iterator<BBSData.Container> nickIterator = MainClass.bbsData_client.showData.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();
            String sbuf = nickIterator.next().nick;

            if (sbuf.equalsIgnoreCase(nick)){
                return buf.edgesWidth;
            }
        }

        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getEdgesTextureHeight(String nick){
        Iterator<BBData> iterator = cl_data.iterator();
        Iterator<BBSData.Container> nickIterator = MainClass.bbsData_client.showData.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();
            String sbuf = nickIterator.next().nick;

            if (sbuf.equalsIgnoreCase(nick)){
                return buf.edgesHeight;
            }
        }

        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static @Nullable String getHealthBarTexturePath(String nick){
        Iterator<BBData> iterator = cl_data.iterator();
        Iterator<BBSData.Container> nickIterator = MainClass.bbsData_client.showData.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();
            String sbuf = nickIterator.next().nick;

            if (sbuf.equalsIgnoreCase(nick)){
                return buf.healthBarPath;
            }
        }

        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getHealthBarTextureRX(String nick){
        Iterator<BBData> iterator = cl_data.iterator();
        Iterator<BBSData.Container> nickIterator = MainClass.bbsData_client.showData.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();
            String sbuf = nickIterator.next().nick;

            if (sbuf.equalsIgnoreCase(nick)){
                return buf.healthBarRX;
            }
        }

        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getHealthBarTextureY(String nick){
        Iterator<BBData> iterator = cl_data.iterator();
        Iterator<BBSData.Container> nickIterator = MainClass.bbsData_client.showData.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();
            String sbuf = nickIterator.next().nick;

            if (sbuf.equalsIgnoreCase(nick)){
                return buf.healthBarY;
            }
        }

        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getHealthBarTextureWidth(String nick){
        Iterator<BBData> iterator = cl_data.iterator();
        Iterator<BBSData.Container> nickIterator = MainClass.bbsData_client.showData.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();
            String sbuf = nickIterator.next().nick;

            if (sbuf.equalsIgnoreCase(nick)){
                return buf.healthBarW;
            }
        }

        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getHealthBarTextureHeight(String nick){
        Iterator<BBData> iterator = cl_data.iterator();
        Iterator<BBSData.Container> nickIterator = MainClass.bbsData_client.showData.iterator();

        while (iterator.hasNext()){
            BBData buf = iterator.next();
            String sbuf = nickIterator.next().nick;

            if (sbuf.equalsIgnoreCase(nick)){
                return buf.healthBarH;
            }
        }

        return 0;
    }
}
