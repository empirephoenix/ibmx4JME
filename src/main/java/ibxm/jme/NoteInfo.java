package ibxm.jme;

public class NoteInfo {

	public int	id;
	public int	volume;
	public int	noteKey;
	public int	globalVolume;
	public int	instrumentid;
	public int	panning;
	public int	freq;

	public NoteInfo(final int id, final int volume, final int noteKey, final int fadeoutVol, final int instrumentid, final int panning, int freq) {
		this.id = id;
		this.volume = volume;
		this.noteKey = noteKey;
		this.globalVolume = fadeoutVol;
		this.instrumentid = instrumentid;
		this.panning = panning;
		this.freq = freq;
	}

	@Override
	public String toString() {
		return "NoteInfo [id=" + this.id + ", volume=" + this.volume + ", noteKey=" + this.noteKey + ", globalVolume=" + this.globalVolume + ", instrumentid=" + this.instrumentid + ", panning=" + this.panning + ", freq=" + this.freq + "]";
	}

}
