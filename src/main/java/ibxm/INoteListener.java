package ibxm;

public interface INoteListener {
	public void onNote(float posInSec, int note, int noteVol, int noteKey, int globalVolume, int instrumentId);
}
