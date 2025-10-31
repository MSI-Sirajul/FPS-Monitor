package com.view.fps;

import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.graphics.drawable.Icon;

public class FpsTileService extends TileService {
    @Override
    public void onClick() {
        Tile tile = getQsTile();
        if (tile.getState() == Tile.STATE_INACTIVE) {
            startForegroundService(new Intent(this, MainActivity.FPSForegroundService.class));
            tile.setState(Tile.STATE_ACTIVE);
        } else {
            stopService(new Intent(this, MainActivity.FPSForegroundService.class));
            tile.setState(Tile.STATE_INACTIVE);
        }
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        tile.setLabel("FPS Monitor");
        tile.setIcon(Icon.createWithResource(this, R.mipmap.tile));
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
