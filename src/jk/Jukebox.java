package jk;

import arc.*;
import arc.func.*;
import arc.scene.event.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.Icon;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;

public class Jukebox extends Mod{

    public Jukebox(){
        Log.info("Jukebox mod initialized. Waiting on music loader...");

        Events.run(EventType.ClientLoadEvent.class, () -> {
            try{
                loadSettings();
                MusicLoader.load();
            }catch(Exception e){
                Vars.ui.showException("Jukebox failed to load.", e);
            }
        });
    }

    private static void loadSettings(){
        Vars.ui.settings.addCategory("Jukebox", Icon.play, e -> {
            e.checkPref("jukebox-remove-vanilla-tracks", true);
            e.pref(new ButtonSetting("jukebox-restore-vanilla-tracks", MusicLoader::restore));
            e.pref(new ButtonSetting("jukebox-reload-tracks", MusicLoader::load));
        });
    }

    private static class ButtonSetting extends SettingsMenuDialog.SettingsTable.Setting{
        Runnable onClick;

        public ButtonSetting(String name, Runnable onClick){
            super(name);
            this.onClick = onClick;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table){
            table.button(title, onClick).tooltip(description).growX().row();
        }
    }
}
