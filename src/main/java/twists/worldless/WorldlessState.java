package twists.worldless;

import xyz.nucleoid.fantasy.RuntimeWorldHandle;

public class WorldlessState {

    private boolean enabled;
    private boolean paused;
    private long ticksPerWorld = 6000;
    private long ticksUntilReset;

    public RuntimeWorldHandle overworldHandle;
    public RuntimeWorldHandle netherHandle;
    public RuntimeWorldHandle endHandle;


    public boolean isEnabled() {
        return this.enabled && !paused;
    }

    public void setPaused(boolean value) {
        this.paused = value;
    }
    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void setTicksPerWorld(long value) {
        this.ticksPerWorld = value;
    }

    public long getTicksUntilReset() {
        return this.ticksUntilReset;
    }
    public void modifyTicksUntilReset(long diff) {
        this.ticksUntilReset = Math.max(0, this.ticksUntilReset + diff);
    }
    public void setTicksUntilReset(long value) {
        this.ticksUntilReset = Math.max(0, value);
    }

    //Returns if the world should reset
    public boolean tick() {
        if (this.enabled && --this.ticksUntilReset <= 0) {
            ticksUntilReset = ticksPerWorld;
            return true;
        }
        return false;
    }
}
