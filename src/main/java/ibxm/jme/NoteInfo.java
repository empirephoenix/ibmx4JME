package ibxm.jme;

public class NoteInfo {

	public int	note;
	public int	volume;
	public int	noteKey;
	public int	globalVolume;
	public int	instrumentid;
	public int	panning;

	public NoteInfo(final int note, final int volume, final int noteKey, final int fadeoutVol, final int instrumentid, final int panning) {
		this.note = note;
		this.volume = volume;
		this.noteKey = noteKey;
		this.globalVolume = fadeoutVol;
		this.instrumentid = instrumentid;
		this.panning = panning;
	}

}
