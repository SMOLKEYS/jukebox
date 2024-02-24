package jk;

import arc.*;
import arc.audio.*;
import arc.files.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.audio.*;

public class MusicLoader{

    public static SoundControl sourceTarget;
    private static final Fi mainDir = Core.settings.getDataDirectory().child("smol-common").child("jukebox");
    private static final Seq<Seq<Music>> vanillaTracks = new Seq<>();
    private static final ObjectMap<String, Seq<Music>> cachedTracks = new ObjectMap<>(); //TODO

    public static void load(){
        sourceTarget = Vars.control.sound;
        if(Core.settings.getBool("jukebox-remove-vanilla-tracks", true)) rewrite();
        mainDir.mkdirs();

        Fi ambi = mainDir.child("ambi"), boss = mainDir.child("boss"), dark = mainDir.child("dark");

        ambi.mkdirs();
        dark.mkdirs();
        boss.mkdirs();

        ambi.walk(b -> {
            if(b.extension().equals("zip")){
                try{
                    new ZipFi(b).walk(e -> sourceTarget.ambientMusic.add(loadFile(e)));
                }catch(Exception e){
                    Log.err("Could not open zip file: " + b.name());
                }
            }else{
                sourceTarget.ambientMusic.add(loadFile(b));
            }
        });
        dark.walk(b -> {
            if(b.extension().equals("zip")){
                try{
                    new ZipFi(b).walk(e -> sourceTarget.darkMusic.add(loadFile(e)));
                }catch(Exception e){
                    Log.err("Could not open zip file: " + b.name());
                }
            }else{
                sourceTarget.darkMusic.add(loadFile(b));
            }
        });
        boss.walk(b -> {
            if(b.extension().equals("zip")){
                try{
                    new ZipFi(b).walk(e -> sourceTarget.bossMusic.add(loadFile(e)));
                }catch(Exception e){
                    Log.err("Could not open zip file: " + b.name());
                }
            }else{
                sourceTarget.bossMusic.add(loadFile(b));
            }
        });
    }

    private static Music loadFile(Fi fi){
        Music m = new Music();

        try{
            m.load(fi);
        }catch(Exception e){
            Log.err("Could not load music file: " + fi.name(), e);
        }

        if(!fi.extension().equals("ogg") || !fi.extension().equals("mp3")) Log.warn("Questionable file extension: " + fi.name());

        return m;
    }

    public static void rewrite(){
        vanillaTracks.add(sourceTarget.ambientMusic.copy(), sourceTarget.bossMusic.copy(), sourceTarget.darkMusic.copy());
        sourceTarget.ambientMusic.clear();
        sourceTarget.bossMusic.clear();
        sourceTarget.darkMusic.clear();
    }

    public static void restore(){
        sourceTarget.ambientMusic.clear().add(vanillaTracks.get(0));
        sourceTarget.bossMusic.clear().add(vanillaTracks.get(1));
        sourceTarget.darkMusic.clear().add(vanillaTracks.get(2));
    }
}
